package com.happyfeet.view;

import com.happyfeet.model.entities.HistorialMedico;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Vista para la gestión del historial médico
 * Proporciona interfaz de usuario para mostrar información médica y solicitar datos
 */
public class HistorialMedicoView {

    private static final DateTimeFormatter FECHA_FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
     * Muestra un mensaje de éxito
     */
    public void mostrarExito(String mensaje) {
        System.out.println("✅ " + mensaje);
    }

    /**
     * Muestra una advertencia médica
     */
    public void mostrarAdvertenciaMedica(String mensaje) {
        System.out.println("⚠️ ADVERTENCIA MÉDICA: " + mensaje);
    }

    /**
     * Muestra los detalles completos de un historial médico
     */
    public void mostrarHistorialCompleto(HistorialMedico historial) {
        if (historial == null) {
            mostrarError("No hay información de historial médico para mostrar");
            return;
        }

        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    HISTORIAL MÉDICO                         ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");

        // Información básica
        System.out.printf("║ ID: %-56d ║%n", historial.getId() != null ? historial.getId() : 0);
        System.out.printf("║ Fecha: %-52s ║%n",
            historial.getFechaEvento() != null ?
            historial.getFechaEvento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "No registrada");

        if (historial.getMascota() != null) {
            System.out.printf("║ Mascota: %-50s ║%n", historial.getMascota().getNombre());
        }

        if (historial.getVeterinario() != null) {
            System.out.printf("║ Veterinario: %-46s ║%n", historial.getVeterinario().getNombreCompleto());
        }

        System.out.println("╠══════════════════════════════════════════════════════════════╣");

        // Signos vitales
        System.out.println("║                        SIGNOS VITALES                       ║");
        System.out.printf("║ Temperatura: %-46s ║%n",
            historial.getTemperatura() != null ? historial.getTemperatura() + "°C" : "No registrada");
        System.out.printf("║ Frecuencia Cardíaca: %-38s ║%n",
            historial.getFrecuenciaCardiaca() != null ? historial.getFrecuenciaCardiaca() + " lpm" : "No registrada");
        System.out.printf("║ Frecuencia Respiratoria: %-34s ║%n",
            historial.getFrecuenciaRespiratoria() != null ? historial.getFrecuenciaRespiratoria() + " rpm" : "No registrada");
        System.out.printf("║ Peso: %-54s ║%n",
            historial.getPeso() != null ? historial.getPeso() + " kg" : "No registrado");

        System.out.println("╠══════════════════════════════════════════════════════════════╣");

        // Información clínica
        System.out.println("║                    INFORMACIÓN CLÍNICA                      ║");
        mostrarCampoTexto("Síntomas", historial.getSintomas());
        mostrarCampoTexto("Diagnóstico", historial.getDiagnostico());
        mostrarCampoTexto("Tratamiento", historial.getTratamientoPrescrito());
        mostrarCampoTexto("Medicamentos", historial.getMedicamentosRecetados());
        mostrarCampoTexto("Recomendaciones", historial.getRecomendaciones());
        mostrarCampoTexto("Observaciones", historial.getObservaciones());

        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        // Alertas médicas si existen valores anormales
        mostrarAlertasMedicas(historial);
    }

    /**
     * Muestra un campo de texto de manera formateada
     */
    private void mostrarCampoTexto(String etiqueta, String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            System.out.printf("║ %s: No registrado%n", etiqueta);
            return;
        }

        // Si el texto es muy largo, lo divide en líneas
        String[] lineas = dividirTexto(valor, 50);
        for (int i = 0; i < lineas.length; i++) {
            if (i == 0) {
                System.out.printf("║ %s: %-*s ║%n", etiqueta, 50 - etiqueta.length(), lineas[i]);
            } else {
                System.out.printf("║   %s%-*s ║%n", " ".repeat(etiqueta.length()), 50 - etiqueta.length(), lineas[i]);
            }
        }
    }

    /**
     * Divide un texto largo en líneas de longitud máxima
     */
    private String[] dividirTexto(String texto, int longitudMaxima) {
        if (texto.length() <= longitudMaxima) {
            return new String[]{texto};
        }

        java.util.List<String> lineas = new java.util.ArrayList<>();
        String[] palabras = texto.split(" ");
        StringBuilder lineaActual = new StringBuilder();

        for (String palabra : palabras) {
            if (lineaActual.length() + palabra.length() + 1 <= longitudMaxima) {
                if (lineaActual.length() > 0) {
                    lineaActual.append(" ");
                }
                lineaActual.append(palabra);
            } else {
                if (lineaActual.length() > 0) {
                    lineas.add(lineaActual.toString());
                    lineaActual = new StringBuilder(palabra);
                } else {
                    lineas.add(palabra.substring(0, Math.min(palabra.length(), longitudMaxima)));
                }
            }
        }

        if (lineaActual.length() > 0) {
            lineas.add(lineaActual.toString());
        }

        return lineas.toArray(new String[0]);
    }

    /**
     * Muestra alertas médicas basadas en signos vitales anormales
     */
    private void mostrarAlertasMedicas(HistorialMedico historial) {
        boolean hayAlertas = false;

        // Verificar temperatura
        if (historial.getTemperatura() != null) {
            BigDecimal temp = historial.getTemperatura();
            if (temp.compareTo(new BigDecimal("37.5")) < 0 || temp.compareTo(new BigDecimal("39.2")) > 0) {
                if (!hayAlertas) {
                    System.out.println("\n⚠️ ALERTAS MÉDICAS:");
                    hayAlertas = true;
                }
                System.out.println("• Temperatura anormal: " + temp + "°C (Normal: 37.5-39.2°C)");
            }
        }

        // Verificar frecuencia cardíaca
        if (historial.getFrecuenciaCardiaca() != null) {
            int fc = historial.getFrecuenciaCardiaca();
            if (fc < 60 || fc > 140) {
                if (!hayAlertas) {
                    System.out.println("\n⚠️ ALERTAS MÉDICAS:");
                    hayAlertas = true;
                }
                System.out.println("• Frecuencia cardíaca anormal: " + fc + " lpm (Normal: 60-140 lpm)");
            }
        }

        // Verificar frecuencia respiratoria
        if (historial.getFrecuenciaRespiratoria() != null) {
            int fr = historial.getFrecuenciaRespiratoria();
            if (fr < 10 || fr > 30) {
                if (!hayAlertas) {
                    System.out.println("\n⚠️ ALERTAS MÉDICAS:");
                    hayAlertas = true;
                }
                System.out.println("• Frecuencia respiratoria anormal: " + fr + " rpm (Normal: 10-30 rpm)");
            }
        }

        if (hayAlertas) {
            System.out.println("⚠️ Se requiere atención veterinaria especializada\n");
        }
    }

    /**
     * Muestra un resumen del historial médico
     */
    public void mostrarResumenHistorial(HistorialMedico historial) {
        if (historial == null) {
            mostrarError("No hay información de historial para mostrar");
            return;
        }

        System.out.println("\n📋 RESUMEN MÉDICO:");
        System.out.println("Fecha: " + (historial.getFechaEvento() != null ?
            historial.getFechaEvento().format(FECHA_FORMATO) : "No registrada"));
        System.out.println("Diagnóstico: " + (historial.getDiagnostico() != null ?
            historial.getDiagnostico() : "No registrado"));
        System.out.println("Estado: " + (historial.getRequiereSeguimiento() != null && historial.getRequiereSeguimiento() ?
            "Requiere seguimiento" : "Sin seguimiento requerido"));
    }

    /**
     * Muestra el reporte médico generado
     */
    public void mostrarReporteMedico(String reporte) {
        if (reporte == null || reporte.trim().isEmpty()) {
            mostrarError("No hay reporte médico para mostrar");
            return;
        }

        System.out.println("\n📄 REPORTE MÉDICO:");
        System.out.println("═".repeat(80));
        System.out.println(reporte);
        System.out.println("═".repeat(80));
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
}
