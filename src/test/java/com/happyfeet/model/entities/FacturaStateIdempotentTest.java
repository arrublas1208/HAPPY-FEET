package com.happyfeet.model.entities;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FacturaStateIdempotentTest {

    @Test
    void cambiarEstadoMismoEstadoEsNoOp() throws Exception {
        Factura f = new Factura();
        // Forzamos estado inicial a PENDIENTE por reflexión (simula objeto leído de BD)
        Field estadoField = Factura.class.getDeclaredField("estado");
        estadoField.setAccessible(true);
        estadoField.set(f, Factura.FacturaEstado.PENDIENTE);

        List<Factura.CambioEstadoFactura> historialAntes = f.getHistorialEstados();
        int sizeAntes = historialAntes.size();

        // No debe lanzar excepción ni modificar historial al intentar el mismo estado
        assertDoesNotThrow(() -> f.cambiarEstado(Factura.FacturaEstado.PENDIENTE, "Sin cambios", "tester"));

        assertEquals(Factura.FacturaEstado.PENDIENTE, estadoField.get(f));
        assertEquals(sizeAntes, f.getHistorialEstados().size(), "No debe registrar cambio de estado cuando es el mismo");
    }
}
