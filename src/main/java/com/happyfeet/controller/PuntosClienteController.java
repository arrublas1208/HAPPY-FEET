package com.happyfeet.controller;

import com.happyfeet.service.PuntosClienteService;
import com.happyfeet.view.PuntosClienteView;

public class PuntosClienteController {
    private final PuntosClienteService puntosService;
    private final PuntosClienteView view;

    public PuntosClienteController(PuntosClienteService puntosService, PuntosClienteView view) {
        this.puntosService = puntosService;
        this.view = view;
    }

    public void acumularPuntos(Integer duenoId, Integer puntos, String concepto) {
        puntosService.acumularPuntos(duenoId, puntos, concepto);
        view.mostrarMensaje("Puntos acumulados correctamente.");
    }

    public void canjearPuntos(Integer duenoId, Integer puntos, String concepto) {
        boolean exito = puntosService.canjearPuntos(duenoId, puntos, concepto);
        if (exito) {
            view.mostrarMensaje("Puntos canjeados correctamente.");
        } else {
            view.mostrarMensaje("No tienes suficientes puntos para canjear.");
        }
    }

    public void consultarBeneficios(Integer duenoId) {
        var beneficios = puntosService.obtenerBeneficios(duenoId);
        view.mostrarBeneficios(beneficios);
    }

    public void verHistorialPuntos(Integer duenoId) {
        var historial = puntosService.obtenerHistorialPuntos(duenoId);
        view.mostrarHistorial(historial);
    }
}