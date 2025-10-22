package com.happyfeet.controller;

import com.happyfeet.model.entities.Cita;
import com.happyfeet.model.entities.HistorialMedico;
import com.happyfeet.model.entities.Mascota;
import com.happyfeet.model.entities.Veterinario;
import com.happyfeet.model.entities.Inventario;
import com.happyfeet.model.entities.enums.CitaEstado;
import com.happyfeet.service.CitaService;
import com.happyfeet.service.MascotaService;
import com.happyfeet.service.VeterinarioService;
import com.happyfeet.service.InventarioService;
import com.happyfeet.view.CitaView;
import com.happyfeet.service.dto.UsoInsumo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class CitaController {

    private final CitaService citaService;
    private final MascotaService mascotaService;
    private final VeterinarioService veterinarioService;
    private final InventarioService inventarioService;
    private final CitaView citaView;

    public CitaController(CitaService citaService, MascotaService mascotaService,
                          VeterinarioService veterinarioService, InventarioService inventarioService, CitaView citaView) {
        this.citaService = citaService;
        this.mascotaService = mascotaService;
        this.veterinarioService = veterinarioService;
        this.inventarioService = inventarioService;
        this.citaView = citaView;
    }

    public void crearCita(Long idVeterinario, Long idMascota, LocalDateTime inicio,
                          LocalDateTime fin, String motivo) {
        try {
            // Validaciones básicas
            if (inicio.isBefore(LocalDateTime.now())) {
                citaView.mostrarError("No se pueden crear citas en fechas pasadas");
                return;
            }

            if (fin != null && fin.isBefore(inicio)) {
                citaView.mostrarError("La hora de fin no puede ser anterior al inicio");
                return;
            }

            // Verificar disponibilidad del veterinario
            if (citaService.haySolape(idVeterinario, inicio, fin)) {
                citaView.mostrarError("El veterinario ya tiene una cita en ese horario");
                return;
            }

            Cita nuevaCita = new Cita();
            nuevaCita.setIdVeterinario(idVeterinario);
            nuevaCita.setIdMascota(idMascota);
            nuevaCita.setInicio(inicio);
            nuevaCita.setFin(fin);
            nuevaCita.setMotivo(motivo);
            // No establecer estado explícito aquí; el servicio asigna el estado inicial adecuado

            Cita citaCreada = citaService.crear(nuevaCita);
            citaView.mostrarMensaje("Cita creada exitosamente para: " +
                    citaView.formatearFechaHora(inicio));

        } catch (Exception e) {
            citaView.mostrarError("Error al crear cita: " + e.getMessage());
        }
    }


    public Optional<Cita> buscarPorId(Long id) {
        try {
            Optional<Cita> cita = citaService.buscarPorId(id);
            if (cita.isEmpty()) {
                citaView.mostrarMensaje("No se encontró cita con ID: " + id);
            }
            return cita;
        } catch (Exception e) {
            citaView.mostrarError("Error al buscar cita: " + e.getMessage());
            return Optional.empty();
        }
    }


    public void actualizarCita(Long id, Cita cambios) {
        try {
            Cita citaActualizada = citaService.actualizar(id, cambios);
            citaView.mostrarMensaje("Cita actualizada exitosamente");
        } catch (Exception e) {
            citaView.mostrarError("Error al actualizar cita: " + e.getMessage());
        }
    }


    public void confirmarCita(Long id) {
        try {
            citaService.confirmar(id);
            citaView.mostrarMensaje("Cita confirmada exitosamente");
        } catch (Exception e) {
            citaView.mostrarError("Error al confirmar cita: " + e.getMessage());
        }
    }


    public void iniciarCita(Long id) {
        try {
            citaService.iniciar(id);
            citaView.mostrarMensaje("Cita iniciada");
        } catch (Exception e) {
            citaView.mostrarError("Error al iniciar cita: " + e.getMessage());
        }
    }


    public void finalizarCita(Long id) {
        try {
            citaService.finalizar(id);
            citaView.mostrarMensaje("Cita finalizada exitosamente");
        } catch (Exception e) {
            citaView.mostrarError("Error al finalizar cita: " + e.getMessage());
        }
    }


    public void cancelarCita(Long id) {
        try {
            boolean confirmar = citaView.mostrarConfirmacion("¿Está seguro de cancelar esta cita?");
            if (confirmar) {
                citaService.cancelar(id);
                citaView.mostrarMensaje("Cita cancelada exitosamente");
            }
        } catch (Exception e) {
            citaView.mostrarError("Error al cancelar cita: " + e.getMessage());
        }
    }


    public void reprogramarCita(Long id, LocalDateTime nuevoInicio, LocalDateTime nuevoFin, String nuevoMotivo) {
        try {
            Cita citaReprogramada = citaService.reprogramar(id, nuevoInicio, nuevoFin, nuevoMotivo);
            citaView.mostrarMensaje("Cita reprogramada para: " +
                    citaView.formatearFechaHora(nuevoInicio));
        } catch (Exception e) {
            citaView.mostrarError("Error al reprogramar cita: " + e.getMessage());
        }
    }


    public void listarCitasPorFecha(LocalDate fecha) {
        try {
            List<Cita> citas = citaService.listarPorFecha(fecha);
            mostrarListaCitas(citas, "Citas para " + fecha.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } catch (Exception e) {
            citaView.mostrarError("Error al listar citas: " + e.getMessage());
        }
    }


    public void listarCitasPorEstado(CitaEstado estado) {
        try {
            List<Cita> citas = citaService.listarPorEstado(estado);
            mostrarListaCitas(citas, "Citas en estado: " + estado.name());
        } catch (Exception e) {
            citaView.mostrarError("Error al listar citas: " + e.getMessage());
        }
    }


    public void listarCitasVeterinario(Long idVeterinario, LocalDateTime desde, LocalDateTime hasta) {
        try {
            List<Cita> citas = citaService.listarPorVeterinarioYEntre(idVeterinario, desde, hasta);
            mostrarListaCitas(citas, "Citas del veterinario entre " +
                    citaView.formatearFechaHora(desde) + " y " +
                    citaView.formatearFechaHora(hasta));
        } catch (Exception e) {
            citaView.mostrarError("Error al listar citas: " + e.getMessage());
        }
    }


    public void mostrarDetallesCita(Long id) {
        try {
            Optional<Cita> citaOpt = citaService.buscarPorId(id);
            if (citaOpt.isPresent()) {
                Cita cita = citaOpt.get();
                String detalles = String.format("""
                    === DETALLES DE CITA ===
                    ID: %d
                    Veterinario ID: %d
                    Mascota ID: %d
                    Fecha/Hora: %s - %s
                    Estado: %s
                    Motivo: %s
                    """,
                        cita.getId(),
                        cita.getIdVeterinario(),
                        cita.getIdMascota(),
                        citaView.formatearFechaHora(cita.getInicio()),
                        citaView.formatearFechaHora(cita.getFin()),
                        cita.getEstado() != null ? cita.getEstado().name() : "N/A",
                        cita.getMotivo() != null ? cita.getMotivo() : "N/A"
                );
                citaView.mostrarDetallesCita(detalles);
            } else {
                citaView.mostrarError("Cita no encontrada");
            }
        } catch (Exception e) {
            citaView.mostrarError("Error al mostrar detalles: " + e.getMessage());
        }
    }


    public void gestionarCita(Long id) {
        try {
            Optional<Cita> citaOpt = citaService.buscarPorId(id);
            if (citaOpt.isEmpty()) {
                citaView.mostrarError("Cita no encontrada");
                return;
            }

            Cita cita = citaOpt.get();
            boolean continuar = true;

            while (continuar) {
                String opcion = citaView.mostrarOpcionesEstado();

                switch (opcion) {
                    case "Confirmar":
                        confirmarCita(id);
                        break;
                    case "Iniciar":
                        iniciarCita(id);
                        break;
                    case "Finalizar":
                        finalizarCita(id);
                        break;
                    case "Cancelar":
                        cancelarCita(id);
                        break;
                    case "Reprogramar":
                        // Aquí podrías pedir los nuevos datos al usuario
                        citaView.mostrarMensaje("Función de reprogramación - implementar diálogo de entrada");
                        break;
                    case "Volver":
                    default:
                        continuar = false;
                        break;
                }

                // Actualizar estado de la cita después de cada operación
                citaOpt = citaService.buscarPorId(id);
                if (citaOpt.isPresent()) {
                    cita = citaOpt.get();
                }
            }

        } catch (Exception e) {
            citaView.mostrarError("Error en gestión de cita: " + e.getMessage());
        }
    }


    public boolean verificarDisponibilidad(Long idVeterinario, LocalDateTime inicio, LocalDateTime fin) {
        try {
            return !citaService.haySolape(idVeterinario, inicio, fin);
        } catch (Exception e) {
            citaView.mostrarError("Error al verificar disponibilidad: " + e.getMessage());
            return false;
        }
    }


    private void mostrarListaCitas(List<Cita> citas, String titulo) {
        if (citas.isEmpty()) {
            citaView.mostrarMensaje("No hay citas para mostrar");
        } else {
            StringBuilder sb = new StringBuilder("=== " + titulo + " ===\n");
            for (int i = 0; i < citas.size(); i++) {
                Cita c = citas.get(i);
                sb.append(String.format("%d. [%s] %s - Vet: %d - Mascota: %d\n",
                        i + 1,
                        c.getEstado() != null ? c.getEstado().name() : "N/A",
                        citaView.formatearFechaHora(c.getInicio()),
                        c.getIdVeterinario(),
                        c.getIdMascota()));
            }
            citaView.mostrarMensaje(sb.toString());
        }
    }

    // Getters para testing
    public CitaService getCitaService() {
        return citaService;
    }

    public MascotaService getMascotaService() {
        return mascotaService;
    }

    public VeterinarioService getVeterinarioService() {
        return veterinarioService;
    }

    public CitaView getCitaView() {
        return citaView;
    }

    // Métodos añadidos desde versión integrada

    
    
    public void run() {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            mostrarMenuCitas();
            try {
                int opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1 -> agendarCitaInteractivo(scanner);
                    case 2 -> confirmarCitaInteractivo(scanner);
                    case 3 -> iniciarConsultaInteractivo(scanner);
                    case 4 -> registrarConsultaMedicaCompleta(scanner);
                    case 5 -> cancelarCitaInteractivo(scanner);
                    case 6 -> reprogramarCitaInteractivo(scanner);
                    case 7 -> listarCitasPorFechaInteractivo(scanner);
                    case 8 -> verificarDisponibilidadInteractivo(scanner);
                    case 0 -> continuar = false;
                    default -> System.out.println("❌ Opción no válida");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Por favor ingrese un número válido");
            }
        }
    }

    private void mostrarMenuCitas() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    GESTIÓN DE CITAS                         ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.println("║ [1] Agendar nueva cita                                      ║");
        System.out.println("║ [2] Confirmar cita pendiente                                ║");
        System.out.println("║ [3] Iniciar consulta                                        ║");
        System.out.println("║ [4] Registrar consulta médica completa                      ║");
        System.out.println("║ [5] Cancelar cita                                           ║");
        System.out.println("║ [6] Reprogramar cita                                        ║");
        System.out.println("║ [7] Listar citas por fecha                                  ║");
        System.out.println("║ [8] Verificar disponibilidad de veterinario                ║");
        System.out.println("║ [0] Volver al menú principal                                ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.print("Seleccione una opción: ");
    }

    private void agendarCitaInteractivo(java.util.Scanner scanner) {
        try {
            System.out.println("\n=== AGENDAR NUEVA CITA ===");

            System.out.print("ID del veterinario: ");
            Long idVeterinario = Long.parseLong(scanner.nextLine());

            System.out.print("ID de la mascota: ");
            Long idMascota = Long.parseLong(scanner.nextLine());

            System.out.print("Fecha y hora de inicio (YYYY-MM-DD HH:MM): ");
            String inicioStr = scanner.nextLine();
            LocalDateTime inicio = LocalDateTime.parse(inicioStr.replace(" ", "T"));

            System.out.print("Duración en minutos (ej: 30): ");
            int duracion = Integer.parseInt(scanner.nextLine());
            LocalDateTime fin = inicio.plusMinutes(duracion);

            System.out.print("Motivo de la consulta: ");
            String motivo = scanner.nextLine();

            crearCita(idVeterinario, idMascota, inicio, fin, motivo);

        } catch (Exception e) {
            System.out.println("❌ Error al agendar cita: " + e.getMessage());
        }
    }

    private void confirmarCitaInteractivo(java.util.Scanner scanner) {
        try {
            System.out.println("\n=== CONFIRMAR CITA ===");
            System.out.print("ID de la cita a confirmar: ");
            Long id = Long.parseLong(scanner.nextLine());
            confirmarCita(id);
        } catch (NumberFormatException e) {
            System.out.println("❌ Por favor ingrese un ID válido");
        }
    }

    private void iniciarConsultaInteractivo(java.util.Scanner scanner) {
        try {
            System.out.println("\n=== INICIAR CONSULTA ===");
            System.out.print("ID de la cita a iniciar: ");
            Long id = Long.parseLong(scanner.nextLine());
            iniciarCita(id);
        } catch (NumberFormatException e) {
            System.out.println("❌ Por favor ingrese un ID válido");
        }
    }

    private void cancelarCitaInteractivo(java.util.Scanner scanner) {
        try {
            System.out.println("\n=== CANCELAR CITA ===");
            System.out.print("ID de la cita a cancelar: ");
            Long id = Long.parseLong(scanner.nextLine());
            cancelarCita(id);
        } catch (NumberFormatException e) {
            System.out.println("❌ Por favor ingrese un ID válido");
        }
    }

    private void reprogramarCitaInteractivo(java.util.Scanner scanner) {
        try {
            System.out.println("\n=== REPROGRAMAR CITA ===");
            System.out.print("ID de la cita a reprogramar: ");
            Long id = Long.parseLong(scanner.nextLine());

            System.out.print("Nueva fecha y hora de inicio (YYYY-MM-DD HH:MM): ");
            String inicioStr = scanner.nextLine();
            LocalDateTime nuevoInicio = LocalDateTime.parse(inicioStr.replace(" ", "T"));

            System.out.print("Duración en minutos: ");
            int duracion = Integer.parseInt(scanner.nextLine());
            LocalDateTime nuevoFin = nuevoInicio.plusMinutes(duracion);

            System.out.print("Nuevo motivo (opcional): ");
            String nuevoMotivo = scanner.nextLine();

            reprogramarCita(id, nuevoInicio, nuevoFin, nuevoMotivo);

        } catch (Exception e) {
            System.out.println("❌ Error al reprogramar cita: " + e.getMessage());
        }
    }

    private void listarCitasPorFechaInteractivo(java.util.Scanner scanner) {
        try {
            System.out.println("\n=== LISTAR CITAS POR FECHA ===");
            System.out.print("Fecha (YYYY-MM-DD): ");
            String fechaStr = scanner.nextLine();
            LocalDate fecha = LocalDate.parse(fechaStr);
            listarCitasPorFecha(fecha);
        } catch (Exception e) {
            System.out.println("❌ Error al procesar fecha: " + e.getMessage());
        }
    }

    private void verificarDisponibilidadInteractivo(java.util.Scanner scanner) {
        try {
            System.out.println("\n=== VERIFICAR DISPONIBILIDAD ===");
            System.out.print("ID del veterinario: ");
            Long idVeterinario = Long.parseLong(scanner.nextLine());

            System.out.print("Fecha y hora de inicio (YYYY-MM-DD HH:MM): ");
            String inicioStr = scanner.nextLine();
            LocalDateTime inicio = LocalDateTime.parse(inicioStr.replace(" ", "T"));

            System.out.print("Duración en minutos: ");
            int duracion = Integer.parseInt(scanner.nextLine());
            LocalDateTime fin = inicio.plusMinutes(duracion);

            boolean disponible = verificarDisponibilidad(idVeterinario, inicio, fin);
            if (disponible) {
                System.out.println("✅ Veterinario disponible en el horario solicitado");
            } else {
                System.out.println("❌ Veterinario no disponible en el horario solicitado");
            }

        } catch (Exception e) {
            System.out.println("❌ Error al verificar disponibilidad: " + e.getMessage());
        }
    }

    /**
     * Registra una consulta médica completa con historial médico y descuento automático de inventario
     */
    private void registrarConsultaMedicaCompleta(java.util.Scanner scanner) {
        try {
            System.out.println("\n=== REGISTRAR CONSULTA MÉDICA COMPLETA ===");

            System.out.print("ID de la cita: ");
            Long citaId = Long.parseLong(scanner.nextLine());

            Optional<Cita> citaOpt = citaService.buscarPorId(citaId);
            if (citaOpt.isEmpty()) {
                System.out.println("❌ Cita no encontrada");
                return;
            }

            Cita cita = citaOpt.get();

            // Verificar que la cita esté en curso
            if (cita.getEstado() != CitaEstado.EN_CURSO) {
                System.out.println("❌ La cita debe estar en curso para registrar la consulta");
                return;
            }

            // Obtener información de mascota y veterinario
            Optional<Mascota> mascotaOpt = mascotaService.buscarPorId(cita.getIdMascota());
            Optional<Veterinario> veterinarioOpt = veterinarioService.buscarPorId(cita.getIdVeterinario());

            if (mascotaOpt.isEmpty() || veterinarioOpt.isEmpty()) {
                System.out.println("❌ Error al obtener datos de mascota o veterinario");
                return;
            }

            Mascota mascota = mascotaOpt.get();
            Veterinario veterinario = veterinarioOpt.get();

            // Registro de consulta médica
            System.out.println("\n--- DATOS DE LA CONSULTA ---");
            System.out.println("Mascota: " + mascota.getNombre());
            System.out.println("Veterinario: " + veterinario.getNombreCompleto());

            // Signos vitales
            System.out.print("Temperatura (°C): ");
            String tempStr = scanner.nextLine();
            java.math.BigDecimal temperatura = tempStr.isEmpty() ? null : new java.math.BigDecimal(tempStr);

            System.out.print("Frecuencia cardíaca (lpm): ");
            String fcStr = scanner.nextLine();
            Integer frecuenciaCardiaca = fcStr.isEmpty() ? null : Integer.parseInt(fcStr);

            System.out.print("Frecuencia respiratoria (rpm): ");
            String frStr = scanner.nextLine();
            Integer frecuenciaRespiratoria = frStr.isEmpty() ? null : Integer.parseInt(frStr);

            System.out.print("Peso actual (kg): ");
            String pesoStr = scanner.nextLine();
            java.math.BigDecimal peso = pesoStr.isEmpty() ? null : new java.math.BigDecimal(pesoStr);

            System.out.print("Síntomas observados: ");
            String sintomas = scanner.nextLine();

            System.out.print("Diagnóstico: ");
            String diagnostico = scanner.nextLine();

            System.out.print("Tratamiento prescrito: ");
            String tratamiento = scanner.nextLine();

            System.out.print("Recomendaciones: ");
            String recomendaciones = scanner.nextLine();

            // Crear historial médico
            HistorialMedico historial = HistorialMedico.builder()
                    .withMascota(mascota)
                    .withVeterinario(veterinario)
                    .withCita(cita)
                    .withEventoMedico(HistorialMedico.TipoEventoMedico.CONSULTA, "Consulta General")
                    .withSignosVitales(temperatura, frecuenciaCardiaca, frecuenciaRespiratoria, peso)
                    .withSintomas(sintomas)
                    .withDiagnostico(diagnostico)
                    .withTratamientoPrescrito(tratamiento)
                    .build();

            historial.setRecomendaciones(recomendaciones);

            // Gestionar medicamentos y descuento de inventario
            System.out.println("\n--- MEDICAMENTOS UTILIZADOS ---");
            System.out.print("¿Se utilizaron medicamentos? (s/n): ");
            String usarMedicamentos = scanner.nextLine();

            if (usarMedicamentos.toLowerCase().startsWith("s")) {
                registrarUsoMedicamentos(scanner, historial);
            }

            // Finalizar cita
            citaService.finalizar(citaId);

            // Actualizar peso de la mascota si se registró
            if (peso != null) {
                mascota.setPesoActual(peso.doubleValue());
                mascotaService.actualizarMascota(mascota.getId().longValue(), mascota);
            }

            System.out.println("✅ Consulta médica registrada exitosamente");
            System.out.println("\n" + historial.generarReporteMedico());

        } catch (Exception e) {
            System.out.println("❌ Error al registrar consulta: " + e.getMessage());
        }
    }

    private void registrarUsoMedicamentos(java.util.Scanner scanner, HistorialMedico historial) {
        boolean continuarMedicamentos = true;
        java.util.List<UsoInsumo> medicamentosUsados = new java.util.ArrayList<>();

        while (continuarMedicamentos) {
            System.out.println("\n--- AGREGAR MEDICAMENTO ---");
            System.out.print("Nombre del medicamento: ");
            String medicamento = scanner.nextLine();

            System.out.print("Cantidad utilizada: ");
            int cantidad = Integer.parseInt(scanner.nextLine());

            System.out.print("Dosis: ");
            String dosis = scanner.nextLine();

            System.out.print("Frecuencia (ej: Cada 8 horas): ");
            String frecuencia = scanner.nextLine();

            System.out.print("Duración en días: ");
            int duracion = Integer.parseInt(scanner.nextLine());

            // Verificar disponibilidad en inventario
            try {
                boolean disponible = inventarioService.verificarDisponibilidad(medicamento, cantidad);
                if (disponible) {
                    // Descontar del inventario
                    inventarioService.descontarStock(medicamento, cantidad);

                    // Agregar al historial
                    historial.agregarTratamiento(medicamento, dosis, frecuencia, duracion);

                    // Registrar uso para auditoria
                    UsoInsumo uso = new UsoInsumo();
                    uso.setNombreInsumo(medicamento);
                    uso.setCantidadUsada(cantidad);
                    uso.setFechaUso(java.time.LocalDateTime.now());
                    medicamentosUsados.add(uso);

                    System.out.println("✅ Medicamento agregado y descontado del inventario");
                } else {
                    System.out.println("❌ Stock insuficiente para: " + medicamento);
                    System.out.print("¿Continuar de todas formas? (s/n): ");
                    String continuar = scanner.nextLine();
                    if (continuar.toLowerCase().startsWith("s")) {
                        historial.agregarTratamiento(medicamento, dosis, frecuencia, duracion);
                        System.out.println("⚠️ Medicamento agregado sin descuento de inventario");
                    }
                }
            } catch (Exception e) {
                System.out.println("❌ Error al procesar medicamento: " + e.getMessage());
            }

            System.out.print("¿Agregar otro medicamento? (s/n): ");
            String otro = scanner.nextLine();
            continuarMedicamentos = otro.toLowerCase().startsWith("s");
        }

        if (!medicamentosUsados.isEmpty()) {
            System.out.println("\n--- RESUMEN DE MEDICAMENTOS USADOS ---");
            for (UsoInsumo uso : medicamentosUsados) {
                System.out.println("• " + uso.getNombreInsumo() + " - Cantidad: " + uso.getCantidadUsada());
            }
        }
    }

}
