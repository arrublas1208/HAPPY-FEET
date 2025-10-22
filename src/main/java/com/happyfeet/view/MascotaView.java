package com.happyfeet.view;

/**
 * Vista para la gestión de mascotas
 * Proporciona interfaz de usuario para mostrar información y confirmaciones
 */
public class MascotaView {

    /**
     * Muestra un mensaje informativo
     */
    public void mostrarMensaje(String mensaje) {
        System.out.println("ℹ️ " + mensaje);
    }

    /**
     * Muestra un mensaje de error
     */
    public void mostrarError(String mensaje) {
        System.err.println("❌ ERROR: " + mensaje);
    }

    /**
     * Muestra una confirmación y retorna la respuesta del usuario
     */
    public boolean mostrarConfirmacion(String mensaje) {
        System.out.print("❓ " + mensaje + " (s/n): ");
        try {
            String respuesta = System.console() != null ?
                System.console().readLine() :
                new java.util.Scanner(System.in).nextLine();
            return respuesta != null && respuesta.toLowerCase().startsWith("s");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Muestra los detalles de una mascota
     */
    public void mostrarMascotaDetalles(String detalles) {
        System.out.println("\n" + detalles);
    }

    /**
     * Muestra información detallada de una mascota
     */
    public void mostrarInformacionMascota(String nombre, String especie, String raza,
                                        String edad, String dueño, String estado) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    INFORMACIÓN DE MASCOTA                   ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Nombre: %-50s ║%n", nombre);
        System.out.printf("║ Especie: %-49s ║%n", especie);
        System.out.printf("║ Raza: %-52s ║%n", raza);
        System.out.printf("║ Edad: %-52s ║%n", edad);
        System.out.printf("║ Dueño: %-51s ║%n", dueño);
        System.out.printf("║ Estado: %-50s ║%n", estado);
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
    }

    /**
     * Muestra un resultado de búsqueda
     */
    public void mostrarResultadoBusqueda(String resultado) {
        System.out.println("🔍 " + resultado);
    }

    /**
     * Muestra un mensaje de éxito
     */
    public void mostrarExito(String mensaje) {
        System.out.println("✅ " + mensaje);
    }

    /**
     * Muestra una advertencia
     */
    public void mostrarAdvertencia(String mensaje) {
        System.out.println("⚠️ " + mensaje);
    }
}