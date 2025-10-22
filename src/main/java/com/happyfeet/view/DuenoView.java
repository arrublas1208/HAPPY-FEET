// DuenoView.java
package com.happyfeet.view;

import javax.swing.JOptionPane;

public class DuenoView {

    public DuenoView() {
        // Constructor por defecto
    }

    /**
     * Muestra un mensaje al usuario
     * @param mensaje El mensaje a mostrar
     */
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Happy Feet Veterinaria",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra un mensaje de error
     * @param mensaje El mensaje de error a mostrar
     */
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Error - Happy Feet Veterinaria",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Muestra un mensaje de confirmación
     * @param mensaje El mensaje de confirmación
     * @return true si el usuario confirma, false si cancela
     */
    public boolean mostrarConfirmacion(String mensaje) {
        int respuesta = JOptionPane.showConfirmDialog(null, mensaje,
                "Confirmación - Happy Feet Veterinaria",
                JOptionPane.YES_NO_OPTION);
        return respuesta == JOptionPane.YES_OPTION;
    }

    /**
     * Muestra un mensaje de advertencia
     * @param mensaje El mensaje de advertencia
     */
    public void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Advertencia - Happy Feet Veterinaria",
                JOptionPane.WARNING_MESSAGE);
    }
}