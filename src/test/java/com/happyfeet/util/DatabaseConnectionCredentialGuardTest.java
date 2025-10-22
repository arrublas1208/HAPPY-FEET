package com.happyfeet.util;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionCredentialGuardTest {

    @BeforeEach
    void reset() {
        System.clearProperty("db.url");
        System.clearProperty("db.username");
        System.clearProperty("db.password");
        DatabaseConnection.getInstance().resetForTests();
    }

    @Test
    void getConnectionDebeFallarSiFaltaPassword() {
        System.setProperty("db.url", "jdbc:mysql://localhost:3306/test_db");
        System.setProperty("db.username", "root");
        // Intencionalmente NO seteamos db.password

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            DatabaseConnection.getInstance().getConnection();
        });
        String msg = ex.getMessage();
        assertTrue(msg != null && msg.contains("Configuración de base de datos incompleta") || msg.contains("No se pudo conectar"),
                "Debe fallar por configuración incompleta o por no poder conectar, nunca intentar sin password");
    }
}
