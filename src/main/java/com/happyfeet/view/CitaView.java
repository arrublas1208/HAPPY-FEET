package com.happyfeet.view;

import javax.swing.JOptionPane;
import java.time.format.DateTimeFormatter;

public class CitaView {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Happy Feet Veterinaria - Citas",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Error - Happy Feet Veterinaria",
                JOptionPane.ERROR_MESSAGE);
    }

    public boolean mostrarConfirmacion(String mensaje) {
        int respuesta = JOptionPane.showConfirmDialog(null, mensaje,
                "Confirmación - Happy Feet Veterinaria",
                JOptionPane.YES_NO_OPTION);
        return respuesta == JOptionPane.YES_OPTION;
    }

    public void mostrarDetallesCita(String detalles) {
        JOptionPane.showMessageDialog(null, detalles, "Detalles de Cita",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Mostrar opciones de estado para cambiar una cita
     */
    public String mostrarOpcionesEstado() {
        String[] opciones = {"Confirmar", "Iniciar", "Finalizar", "Cancelar", "Reprogramar", "Volver"};
        int seleccion = JOptionPane.showOptionDialog(null,
                "Seleccione la acción a realizar:",
                "Gestión de Cita",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);

        return seleccion >= 0 ? opciones[seleccion] : "Volver";
    }

    /**
     * Formatear fecha y hora para mostrar
     */
    public String formatearFechaHora(java.time.LocalDateTime fechaHora) {
        if (fechaHora == null) return "N/A";
        return fechaHora.format(DATE_FORMATTER) + " " + fechaHora.format(TIME_FORMATTER);
    }
}