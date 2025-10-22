package com.happyfeet.util;

import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    @BeforeEach
    void clearSystemProps() {
        System.clearProperty("db.url");
        System.clearProperty("db.username");
        System.clearProperty("db.password");
        // Reiniciar singleton para pruebas aisladas
        DatabaseConnection.getInstance().resetForTests();
    }

    @Test
    void testLeePropertiesDesdeArchivo() throws Exception {
        // Asegurarnos que existe el archivo database.properties en la raíz del módulo
        Path root = Path.of(".").toAbsolutePath().normalize();
        File propsFile = root.resolve("database.properties").toFile();
        assertTrue(propsFile.exists(), "Debe existir database.properties en la raíz del proyecto para esta prueba");

        DatabaseConnection db = DatabaseConnection.getInstance();
        Properties cfg = db.getCurrentConfig();

        assertNotNull(cfg.getProperty("db.url"), "db.url debe leerse desde database.properties");
        assertNotNull(cfg.getProperty("db.username"), "db.username debe leerse desde database.properties");
        assertTrue(cfg.containsKey("db.password"), "db.password debe estar presente (aunque sea vacío)");

        // No se intenta abrir conexión real en esta prueba
    }

    @Test
    void testOverridePorSystemProperties() {
        String url = "jdbc:mysql://localhost:3306/db_prueba";
        String user = "tester";
        String pass = "secret";
        System.setProperty("db.url", url);
        System.setProperty("db.username", user);
        System.setProperty("db.password", pass);

        DatabaseConnection db = DatabaseConnection.getInstance();
        Properties cfg = db.getCurrentConfig();

        assertEquals(url, cfg.getProperty("db.url"));
        assertEquals(user, cfg.getProperty("db.username"));
        assertEquals(pass, cfg.getProperty("db.password"));
    }

    @Test
    void testResetForTests() {
        System.setProperty("db.url", "jdbc:mysql://localhost:3306/db_x");
        System.setProperty("db.username", "u");
        System.setProperty("db.password", "p");
        DatabaseConnection db = DatabaseConnection.getInstance();
        Properties cfg1 = db.getCurrentConfig();
        assertEquals("jdbc:mysql://localhost:3306/db_x", cfg1.getProperty("db.url"));

        db.resetForTests();
        System.clearProperty("db.url");
        System.clearProperty("db.username");
        System.clearProperty("db.password");

        Properties cfg2 = db.getCurrentConfig();
        assertNotNull(cfg2.getProperty("db.url"), "Tras reset y sin system props, debe cargar de archivo o defaults");
    }
}
