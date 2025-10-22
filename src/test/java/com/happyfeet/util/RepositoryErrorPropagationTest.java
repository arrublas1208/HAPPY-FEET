package com.happyfeet.util;

import com.happyfeet.repository.impl.InventarioRepositoryImpl;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryErrorPropagationTest {

    @BeforeEach
    void reset() {
        System.clearProperty("db.url");
        System.clearProperty("db.username");
        System.clearProperty("db.password");
        DatabaseConnection.getInstance().resetForTests();
    }

    @Test
    void repositorioNoDebeDevolverListaVaciaAnteErrorDeConexion() {
        // Forzamos configuración incompleta para provocar fallo de conexión
        System.setProperty("db.url", "jdbc:mysql://localhost:3306/happy_feet_veterinaria");
        System.setProperty("db.username", "root");
        // SIN password

        InventarioRepositoryImpl repo = new InventarioRepositoryImpl(DatabaseConnection.getInstance());
        RuntimeException ex = assertThrows(RuntimeException.class, repo::findAll);
        assertNotNull(ex);
    }
}
