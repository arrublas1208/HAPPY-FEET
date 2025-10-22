package com.happyfeet.integration;

import com.happyfeet.util.DatabaseConnection;
import com.happyfeet.repository.impl.InventarioRepositoryImpl;
import com.happyfeet.model.entities.Inventario;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class
DatabaseSmokeTest {

    private static boolean canConnect() {
        try {
            if (!"true".equalsIgnoreCase(System.getenv().getOrDefault("RUN_DB_TESTS", "false"))) {
                return false; // se ejecuta sólo si se habilita explícitamente
            }
            Connection c = DatabaseConnection.getInstance().getConnection();
            try (var st = c.createStatement(); var rs = st.executeQuery("SELECT 1")) {
                return rs.next();
            }
        } catch (Exception e) {
            System.out.println("[SMOKE] No se ejecutan pruebas DB: " + e.getMessage());
            return false;
        }
    }

    @BeforeAll
    static void precheck() {
        assumeTrue(canConnect(), "Se omiten pruebas de BD (RUN_DB_TESTS != true o sin conexión)");
    }

    @Test
    void insertAndReadInventario() {
        InventarioRepositoryImpl repo = new InventarioRepositoryImpl(DatabaseConnection.getInstance());
        Inventario inv = new Inventario();
        inv.setNombreProducto("SMOKE-Test-Producto");
        inv.setPrecioVenta(new BigDecimal("9999"));
        inv.setCantidadStock(10);
        inv.setFechaVencimiento(LocalDate.now().plusDays(10));
        Inventario creado = repo.save(inv);
        assertNotNull(creado.getId());
        Optional<Inventario> buscado = repo.findById(creado.getId());
        assertTrue(buscado.isPresent());
        assertEquals("SMOKE-Test-Producto", buscado.get().getNombreProducto());
    }
}
