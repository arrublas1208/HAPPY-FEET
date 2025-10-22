package com.happyfeet.view;

import com.happyfeet.controller.ActividadesEspecialesController.MascotaAdopcion;
import com.happyfeet.controller.ActividadesEspecialesController.JornadaVacunacion;
import com.happyfeet.controller.ActividadesEspecialesController.ClienteFrecuente;

import java.util.List;

public class ActividadesEspecialesView {

    public void mostrarMascotasAdopcion(List<MascotaAdopcion> mascotas) {
        System.out.println("\n=== MASCOTAS DISPONIBLES PARA ADOPCIÓN ===");
        if (mascotas.isEmpty()) {
            System.out.println("No hay mascotas disponibles.");
            return;
        }
        mascotas.forEach(m -> System.out.println(m));
    }

    public void mostrarJornadasVacunacion(List<JornadaVacunacion> jornadas) {
        System.out.println("\n=== JORNADAS DE VACUNACIÓN ===");
        if (jornadas.isEmpty()) {
            System.out.println("No hay jornadas registradas.");
            return;
        }
        jornadas.forEach(j -> System.out.println(j.getNombre() + " - " + j.getFecha() + " - " + j.getUbicacion()));
    }

    public void mostrarClientesFrecuentes(List<ClienteFrecuente> clientes) {
        System.out.println("\n=== CLIENTES FRECUENTES ===");
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes frecuentes.");
            return;
        }
        clientes.forEach(c -> System.out.println(c.getNombreCompleto() + " - Nivel: " + c.getNivelCliente() + " - Puntos: " + c.getPuntosAcumulados()));
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }
}
