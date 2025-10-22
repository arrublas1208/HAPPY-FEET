package com.happyfeet.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

public class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    private Connection connection;
    private String url;
    private String username;
    private String password;

    // Registro perezoso del driver (se hará al primer uso)
    static {
        // No forzar carga del driver aquí para no romper el arranque si falta el conector/BD.
    }

    private DatabaseConnection() {
        // No abrimos conexión aquí; sólo cargamos configuración. La conexión se abrirá al primer uso.
        try {
            Properties props = loadDatabaseProperties();
            this.url = props.getProperty("db.url");
            this.username = props.getProperty("db.username");
            this.password = props.getProperty("db.password");
        } catch (Exception ignored) {
            // Usaremos valores por defecto si es necesario al primer uso
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    // Solo para pruebas: restablece el estado interno
    void resetForTests() {
        closeConnection();
        this.connection = null;
        this.url = null;
        this.username = null;
        this.password = null;
    }

    private void initializeConnection() {
        try {
            Properties props = loadDatabaseProperties();
            this.url = props.getProperty("db.url");
            this.username = props.getProperty("db.username");
            this.password = props.getProperty("db.password");
            // No abrimos conexión aquí; se abrirá bajo demanda en getConnection().
        } catch (Exception e) {
            System.err.println("Error cargando configuración de BD: " + e.getMessage());
        }
    }

    private Properties loadDatabaseProperties() {
        Properties props = new Properties();

        // 1) Permitir override por System properties (útil en pruebas CI)
        String sysUrl = System.getProperty("db.url");
        String sysUser = System.getProperty("db.username");
        String sysPass = System.getProperty("db.password");
        if (sysUrl != null) props.setProperty("db.url", sysUrl);
        if (sysUser != null) props.setProperty("db.username", sysUser);
        if (sysPass != null) props.setProperty("db.password", sysPass);

        // 1b) Permitir override por variables de entorno si faltan claves
        if (!props.containsKey("db.url") || !props.containsKey("db.username") || !props.containsKey("db.password")) {
            String envUrl = System.getenv("DB_URL");
            String envUser = System.getenv("DB_USERNAME");
            String envPass = System.getenv("DB_PASSWORD");
            if (envUrl != null) props.setProperty("db.url", envUrl);
            if (envUser != null) props.setProperty("db.username", envUser);
            if (envPass != null) props.setProperty("db.password", envPass);
        }

        // 2) Si no se establecieron vía sistema/entorno, intentar cargar desde classpath o archivos conocidos
        //    Cargar desde alguna fuente si falta cualquiera de las 3 claves (url, username o password)
        if (!props.containsKey("db.url") || !props.containsKey("db.username") || !props.containsKey("db.password")) {
            try {
                boolean loaded = false;

                // 2.a) Primero intentar desde el classpath (útil cuando se empaqueta el JAR)
                try (InputStream cp = Thread.currentThread().getContextClassLoader().getResourceAsStream("database.properties")) {
                    if (cp != null) {
                        Properties p2 = new Properties();
                        p2.load(cp);
                        props.putAll(p2);
                        System.out.println("Configuración cargada desde classpath: database.properties");
                        loaded = true;
                    }
                }

                // 2.b) Intentar archivo en el directorio de trabajo actual
                if (!loaded) {
                    File configFile = new File("database.properties");
                    if (configFile.exists()) {
                        try (InputStream input = new FileInputStream(configFile)) {
                            Properties p2 = new Properties();
                            p2.load(input);
                            props.putAll(p2);
                            System.out.println("Configuración cargada desde archivo: " + configFile.getAbsolutePath());
                            loaded = true;
                        }
                    }
                }

                // 2.c) Intentar archivo en ruta del módulo (cuando se ejecuta desde el directorio raíz del repositorio)
                if (!loaded) {
                    File moduleFile = new File("HappyFeet_Integrated_Carlos_clean\\database.properties");
                    if (!moduleFile.exists()) {
                        moduleFile = new File("HappyFeet_Integrated_Carlos_clean/database.properties"); // compatibilidad
                    }
                    if (moduleFile.exists()) {
                        try (InputStream input = new FileInputStream(moduleFile)) {
                            Properties p2 = new Properties();
                            p2.load(input);
                            props.putAll(p2);
                            System.out.println("Configuración cargada desde archivo del módulo: " + moduleFile.getAbsolutePath());
                            loaded = true;
                        }
                    }
                }

                // 2.d) Si no se pudo cargar de ningún lugar, usar valores por defecto razonables (MySQL local)
                if (!loaded) {
                    System.out.println("No se encontró database.properties en classpath ni en archivos conocidos, usando valores por defecto");
                    props.setProperty("db.url", props.getProperty("db.url", "jdbc:mysql://localhost:3306/happy_feet_veterinaria"));
                    props.setProperty("db.username", props.getProperty("db.username", "root"));
                    props.setProperty("db.password", props.getProperty("db.password", ""));
                    createExampleConfigFile();
                }
            } catch (IOException e) {
                System.err.println("Error al cargar configuración: " + e.getMessage());
                props.setProperty("db.url", props.getProperty("db.url", "jdbc:mysql://localhost:3306/happy_feet_veterinaria"));
                props.setProperty("db.username", props.getProperty("db.username", "root"));
                props.setProperty("db.password", props.getProperty("db.password", ""));
            }
        }

        return props;
    }

    private void createExampleConfigFile() {
        try {
            Properties exampleProps = new Properties();
            exampleProps.setProperty("db.url", "jdbc:mysql://localhost:3306/happy_feet_veterinaria");
            exampleProps.setProperty("db.username", "root");
            exampleProps.setProperty("db.password", "tu_password_mysql");
            exampleProps.setProperty("db.connection.timeout", "30");

            // No guardamos el ejemplo automáticamente para evitar sobrescribir
            System.out.println("Crea un archivo 'database.properties' con la configuración de tu BD");
        } catch (Exception e) {
            System.err.println("Error al crear archivo de ejemplo: " + e.getMessage());
        }
    }

    private void testConnection() throws SQLException {
        try (var stmt = connection.createStatement()) {
            var rs = stmt.executeQuery("SELECT 1");
            if (rs.next()) {
                System.out.println("Test de conexión a BD exitoso");
            }
        }
    }

    private void registerDriverForUrl(String jdbcUrl) {
        if (jdbcUrl == null) return;
        try {
            if (jdbcUrl.startsWith("jdbc:mysql:")) {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } else if (jdbcUrl.startsWith("jdbc:h2:")) {
                Class.forName("org.h2.Driver");
            } else {
                // Intento genérico: algunos drivers se registran automáticamente con ServiceLoader
                // Así que no forzamos nada si no es conocido.
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se encontró el driver JDBC para la URL: " + jdbcUrl + ". Agrega la dependencia correspondiente al pom.xml.", e);
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                synchronized (this) {
                    if (connection == null || connection.isClosed()) {
                        // Cargar configuración si falta cualquiera de las credenciales
                        if (url == null || username == null || password == null || password.isEmpty()) {
                            initializeConnection();
                        }
                        if (url == null || username == null || password == null || password.isEmpty()) {
                            String missing = (url == null ? "db.url " : "") +
                                             (username == null ? "db.username " : "") +
                                             ((password == null || password.isEmpty()) ? "db.password " : "");
                            throw new RuntimeException("Configuración de base de datos incompleta o inválida (" + missing.trim() + "). Define db.url, db.username y db.password en System properties, variables de entorno o en 'database.properties'.");
                        }
                        // Registrar driver de forma perezosa según URL
                        registerDriverForUrl(url);
                        System.out.println("[BD] Intentando conectar -> URL: " + url + ", Usuario: " + username + ", Password definido: " + ((password != null && !password.isEmpty()) ? "SI" : "NO"));
                        this.connection = DriverManager.getConnection(url, username, password);
                        // Probar conexión con SELECT 1
                        try {
                            testConnection();
                        } catch (SQLException ignore) {}
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener conexión: " + e.getMessage());
            System.err.println("URL: " + url + ", Usuario: " + username);
            System.err.println("Password definido: " + ((password != null && !password.isEmpty()) ? "SI" : "NO"));
            throw new RuntimeException("No se pudo conectar a la base de datos. Verifica que el motor esté ejecutándose y las credenciales en database.properties o System properties.", e);
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión a BD cerrada");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
    }

    // Método para verificar el estado de la conexión
    public boolean isConnectionValid() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }

    // Exponer configuración efectiva sin abrir conexión
    public Properties getCurrentConfig() {
        if (url == null || username == null) {
            initializeConnection();
        }
        Properties p = new Properties();
        if (url != null) p.setProperty("db.url", url);
        if (username != null) p.setProperty("db.username", username);
        if (password != null) p.setProperty("db.password", password);
        return p;
    }

    // Método para obtener información de la conexión (útil para debugging)
    public String getConnectionInfo() {
        try {
            if (connection != null && !connection.isClosed()) {
                var metaData = connection.getMetaData();
                return String.format("BD: %s, Versión: %s",
                        metaData.getDatabaseProductName(),
                        metaData.getDatabaseProductVersion());
            }
        } catch (SQLException e) {
            // Ignorar error en este contexto
        }
        return "Conexión no disponible";
    }
}