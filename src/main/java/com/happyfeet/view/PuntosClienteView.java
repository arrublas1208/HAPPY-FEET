package com.happyfeet.view;

import java.util.List;

public class PuntosClienteView {
    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    public void mostrarBeneficios(List<String> beneficios) {
        System.out.println("Beneficios disponibles:");
        if (beneficios == null || beneficios.isEmpty()) {
            System.out.println("No hay beneficios disponibles.");
            return;
        }
        beneficios.forEach(System.out::println);
    }

    public void mostrarHistorial(List<String> historial) {
        System.out.println("Historial de puntos:");
        if (historial == null || historial.isEmpty()) {
            System.out.println("No hay movimientos de puntos.");
            return;
        }
        historial.forEach(System.out::println);
    }
}