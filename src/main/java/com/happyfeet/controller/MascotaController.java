package com.happyfeet.controller;

import com.happyfeet.model.entities.Mascota;
import com.happyfeet.model.entities.Dueno;
import com.happyfeet.service.MascotaService;
import com.happyfeet.service.DuenoService;
import com.happyfeet.view.MascotaView;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MascotaController {

    private final MascotaService mascotaService;
    private final DuenoService duenoService;
    private final MascotaView mascotaView;

    public MascotaController(MascotaService mascotaService, DuenoService duenoService, MascotaView mascotaView) {
        this.mascotaService = mascotaService;
        this.duenoService = duenoService;
        this.mascotaView = mascotaView;
    }

    /**
     * Crear nueva mascota
     */
    public void crearMascota(String nombre, Integer duenoId, Integer razaId, LocalDate fechaNacimiento,
                             Mascota.Sexo sexo, String color, String seniasParticulares, Double pesoActual,
                             String microchip, LocalDate fechaImplantacionMicrochip, Boolean agresivo) {
        try {
            // Verificar que el dueño existe
            Optional<Dueno> dueno = duenoService.buscarPorId(duenoId.longValue());
            if (dueno.isEmpty()) {
                mascotaView.mostrarError("No se encontró dueño con ID: " + duenoId);
                return;
            }

            Mascota nuevaMascota = Mascota.Builder.create()
                    .withNombre(nombre)
                    .withDuenoId(duenoId)
                    .withRazaId(razaId)
                    .withFechaNacimiento(fechaNacimiento)
                    .withSexo(sexo)
                    .withColor(color)
                    .withSeniasParticulares(seniasParticulares)
                    .withPesoActual(pesoActual)
                    .withMicrochip(microchip)
                    .withFechaImplantacionMicrochip(fechaImplantacionMicrochip)
                    .withAgresivo(agresivo)
                    .build();

            Mascota mascotaCreada = mascotaService.crearMascota(nuevaMascota);
            mascotaView.mostrarMensaje("Mascota creada exitosamente: " + mascotaCreada.getNombre());

        } catch (Exception e) {
            mascotaView.mostrarError("Error al crear mascota: " + e.getMessage());
        }
    }

    /**
     * Versión simplificada para crear mascota
     */
    public void crearMascotaBasica(String nombre, Integer duenoId, Mascota.Sexo sexo, String color) {
        crearMascota(nombre, duenoId, null, null, sexo, color, null, null, null, null, null);
    }

    /**
     * Buscar mascota por ID
     */
    public Optional<Mascota> buscarPorId(Long id) {
        try {
            Optional<Mascota> mascota = mascotaService.buscarPorId(id);
            if (mascota.isEmpty()) {
                mascotaView.mostrarMensaje("No se encontró mascota con ID: " + id);
            }
            return mascota;
        } catch (Exception e) {
            mascotaView.mostrarError("Error al buscar mascota: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Buscar mascotas por dueño
     */
    public List<Mascota> buscarPorDueno(Long duenoId) {
        try {
            List<Mascota> mascotas = mascotaService.buscarPorDueno(duenoId);
            if (mascotas.isEmpty()) {
                mascotaView.mostrarMensaje("No se encontraron mascotas para el dueño con ID: " + duenoId);
            }
            return mascotas;
        } catch (Exception e) {
            mascotaView.mostrarError("Error al buscar mascotas: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Actualizar mascota
     */
    public void actualizarMascota(Long id, Mascota cambios) {
        try {
            Mascota mascotaActualizada = mascotaService.actualizarMascota(id, cambios);
            mascotaView.mostrarMensaje("Mascota actualizada exitosamente: " + mascotaActualizada.getNombre());
        } catch (Exception e) {
            mascotaView.mostrarError("Error al actualizar mascota: " + e.getMessage());
        }
    }

    /**
     * Eliminar mascota
     */
    public void eliminarMascota(Long id) {
        try {
            Optional<Mascota> mascota = mascotaService.buscarPorId(id);
            if (mascota.isPresent()) {
                boolean confirmar = mascotaView.mostrarConfirmacion(
                        "¿Está seguro de eliminar la mascota: " + mascota.get().getNombre() + "?"
                );
                if (confirmar) {
                    mascotaService.eliminarMascota(id);
                    mascotaView.mostrarMensaje("Mascota eliminada exitosamente");
                }
            } else {
                mascotaView.mostrarError("No se encontró mascota con ID: " + id);
            }
        } catch (Exception e) {
            mascotaView.mostrarError("Error al eliminar mascota: " + e.getMessage());
        }
    }

    /**
     * Listar todas las mascotas
     */
    public void listarTodas() {
        try {
            List<Mascota> mascotas = mascotaService.listarTodas();
            if (mascotas.isEmpty()) {
                mascotaView.mostrarMensaje("No hay mascotas registradas");
            } else {
                StringBuilder sb = new StringBuilder("=== LISTA DE MASCOTAS ===\n");
                for (int i = 0; i < mascotas.size(); i++) {
                    Mascota m = mascotas.get(i);
                    sb.append(String.format("%d. %s - %s - Dueño ID: %d\n",
                            i + 1, m.getNombre(),
                            m.getSexo() != null ? m.getSexo().name() : "N/A",
                            m.getDuenoId()));
                }
                mascotaView.mostrarMensaje(sb.toString());
            }
        } catch (Exception e) {
            mascotaView.mostrarError("Error al listar mascotas: " + e.getMessage());
        }
    }

    /**
     * Mostrar detalles completos de una mascota
     */
    public void mostrarDetallesMascota(Long id) {
        try {
            Optional<Mascota> mascotaOpt = mascotaService.buscarPorId(id);
            if (mascotaOpt.isPresent()) {
                Mascota m = mascotaOpt.get();
                String detalles = String.format("""
                    === DETALLES DE MASCOTA ===
                    Nombre: %s
                    Dueño ID: %d
                    Sexo: %s
                    Color: %s
                    Peso: %.2f kg
                    Microchip: %s
                    Fecha Nacimiento: %s
                    Señas particulares: %s
                    Alergias: %s
                    Agresivo: %s
                    """,
                        m.getNombre(),
                        m.getDuenoId(),
                        m.getSexo() != null ? m.getSexo().name() : "N/A",
                        m.getColor() != null ? m.getColor() : "N/A",
                        m.getPesoActual() != null ? m.getPesoActual() : 0.0,
                        m.getMicrochip() != null ? m.getMicrochip() : "No registrado",
                        m.getFechaNacimiento() != null ? m.getFechaNacimiento() : "N/A",
                        m.getSeniasParticulares() != null ? m.getSeniasParticulares() : "Ninguna",
                        m.getAlergias() != null ? m.getAlergias() : "Ninguna",
                        m.getAgresivo() != null ? (m.getAgresivo() ? "Sí" : "No") : "N/A"
                );
                mascotaView.mostrarMascotaDetalles(detalles);
            } else {
                mascotaView.mostrarError("Mascota no encontrada");
            }
        } catch (Exception e) {
            mascotaView.mostrarError("Error al mostrar detalles: " + e.getMessage());
        }
    }

    /**
     * Buscar mascotas por nombre
     */
    public void buscarPorNombre(String termino) {
        try {
            List<Mascota> resultados = mascotaService.buscarPorNombre(termino);
            if (resultados.isEmpty()) {
                mascotaView.mostrarMensaje("No se encontraron mascotas con: " + termino);
            } else {
                StringBuilder sb = new StringBuilder("=== RESULTADOS DE BÚSQUEDA ===\n");
                for (Mascota m : resultados) {
                    sb.append(String.format("- %s (%s) - Dueño ID: %d\n",
                            m.getNombre(),
                            m.getSexo() != null ? m.getSexo().name() : "N/A",
                            m.getDuenoId()));
                }
                mascotaView.mostrarMensaje(sb.toString());
            }
        } catch (Exception e) {
            mascotaView.mostrarError("Error en la búsqueda: " + e.getMessage());
        }
    }

    /**
     * Verificar si existe mascota con microchip
     */
    public boolean existePorMicrochip(String microchip) {
        try {
            return mascotaService.existePorMicrochip(microchip);
        } catch (Exception e) {
            mascotaView.mostrarError("Error al verificar microchip: " + e.getMessage());
            return false;
        }
    }

    // Getters para testing
    public MascotaService getMascotaService() {
        return mascotaService;
    }

    public DuenoService getDuenoService() {
        return duenoService;
    }

    public MascotaView getMascotaView() {
        return mascotaView;
    }


    /**
     * Transferir propiedad de mascota a nuevo dueño
     */
    public void transferirPropiedadMascota(Long mascotaId, Long nuevoDuenoId) {
        try {
            // Verificar que la mascota existe
            Optional<Mascota> mascotaOpt = mascotaService.buscarPorId(mascotaId);
            if (mascotaOpt.isEmpty()) {
                mascotaView.mostrarError("Mascota no encontrada con ID: " + mascotaId);
                return;
            }

            // Verificar que el nuevo dueño existe
            Optional<Dueno> nuevoDuenoOpt = duenoService.buscarPorId(nuevoDuenoId);
            if (nuevoDuenoOpt.isEmpty()) {
                mascotaView.mostrarError("Dueño no encontrado con ID: " + nuevoDuenoId);
                return;
            }

            Mascota mascota = mascotaOpt.get();
            Dueno nuevoDueno = nuevoDuenoOpt.get();
            Long duenoAnteriorId = mascota.getDuenoId().longValue();

            // Obtener información del dueño anterior
            Optional<Dueno> duenoAnteriorOpt = duenoService.buscarPorId(duenoAnteriorId);
            String nombreAnterior = duenoAnteriorOpt.map(Dueno::getNombreCompleto).orElse("Desconocido");

            // Confirmar transferencia
            boolean confirmar = mascotaView.mostrarConfirmacion(
                String.format("¿Confirma transferir '%s' de %s a %s?",
                    mascota.getNombre(), nombreAnterior, nuevoDueno.getNombreCompleto())
            );

            if (confirmar) {
                mascota.setDuenoId(nuevoDuenoId.intValue());
                mascotaService.actualizarMascota(mascotaId, mascota);

                mascotaView.mostrarMensaje(String.format(
                    "Propiedad transferida exitosamente:\n" +
                    "Mascota: %s\n" +
                    "Dueño anterior: %s\n" +
                    "Nuevo dueño: %s",
                    mascota.getNombre(), nombreAnterior, nuevoDueno.getNombreCompleto()
                ));
            } else {
                mascotaView.mostrarMensaje("Transferencia cancelada");
            }

        } catch (Exception e) {
            mascotaView.mostrarError("Error al transferir propiedad: " + e.getMessage());
        }
    }


    // Métodos añadidos desde versión integrada

    
    
    public void run() {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            mostrarMenuMascotas();
            try {
                int opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1 -> registrarMascotaCompleta(scanner);
                    case 2 -> buscarMascotaInteractivo(scanner);
                    case 3 -> actualizarMascotaInteractivo(scanner);
                    case 4 -> transferirPropiedadInteractivo(scanner);
                    case 5 -> listarTodas();
                    case 6 -> mostrarMascotasPorDueno(scanner);
                    case 7 -> verificarMicrochipInteractivo(scanner);
                    case 8 -> eliminarMascotaInteractivo(scanner);
                    case 0 -> continuar = false;
                    default -> System.out.println("❌ Opción no válida");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Por favor ingrese un número válido");
            }
        }
    }

    private void mostrarMenuMascotas() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    GESTIÓN DE MASCOTAS                      ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.println("║ [1] Registrar nueva mascota (ficha completa)                ║");
        System.out.println("║ [2] Buscar mascota por ID o nombre                          ║");
        System.out.println("║ [3] Actualizar información de mascota                       ║");
        System.out.println("║ [4] Transferir propiedad de mascota                         ║");
        System.out.println("║ [5] Listar todas las mascotas                               ║");
        System.out.println("║ [6] Consultar mascotas por dueño                            ║");
        System.out.println("║ [7] Verificar microchip                                     ║");
        System.out.println("║ [8] Eliminar registro de mascota                            ║");
        System.out.println("║ [0] Volver al menú principal                                ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.print("Seleccione una opción: ");
    }

    private void registrarMascotaCompleta(java.util.Scanner scanner) {
        try {
            System.out.println("\n=== REGISTRO DE MASCOTA COMPLETO ===");

            System.out.print("Nombre de la mascota: ");
            String nombre = scanner.nextLine();

            System.out.print("ID del dueño: ");
            Integer duenoId = Integer.parseInt(scanner.nextLine());

            System.out.print("Sexo (MACHO/HEMBRA): ");
            Mascota.Sexo sexo = Mascota.Sexo.valueOf(scanner.nextLine().toUpperCase());

            System.out.print("Color: ");
            String color = scanner.nextLine();

            System.out.print("Peso actual (kg): ");
            Double peso = Double.parseDouble(scanner.nextLine());

            System.out.print("Alergias conocidas: ");
            String alergias = scanner.nextLine();

            System.out.print("Condiciones preexistentes: ");
            String condiciones = scanner.nextLine();

            System.out.print("Historial de vacunas: ");
            String historialVacunas = scanner.nextLine();

            System.out.print("Procedimientos previos: ");
            String procedimientos = scanner.nextLine();

            System.out.print("Número de microchip (opcional): ");
            String microchip = scanner.nextLine();
            if (microchip.trim().isEmpty()) microchip = null;

            System.out.print("URL de foto (opcional): ");
            String fotoUrl = scanner.nextLine();
            if (fotoUrl.trim().isEmpty()) fotoUrl = null;

            System.out.print("¿Es agresivo? (s/n): ");
            Boolean agresivo = scanner.nextLine().toLowerCase().startsWith("s");

            Mascota nuevaMascota = Mascota.Builder.create()
                    .withNombre(nombre)
                    .withDuenoId(duenoId)
                    .withSexo(sexo)
                    .withColor(color)
                    .withPesoActual(peso)
                    .withAlergias(alergias)
                    .withCondicionesPreexistentes(condiciones)
                    .withHistorialVacunas(historialVacunas)
                    .withProcedimientosPrevios(procedimientos)
                    .withMicrochip(microchip)
                    .withUrlFoto(fotoUrl)
                    .withAgresivo(agresivo)
                    .build();

            Mascota mascotaCreada = mascotaService.crearMascota(nuevaMascota);
            System.out.println("✅ Mascota registrada exitosamente: " + mascotaCreada.getNombre());

        } catch (Exception e) {
            System.out.println("❌ Error al registrar mascota: " + e.getMessage());
        }
    }

    private void buscarMascotaInteractivo(java.util.Scanner scanner) {
        System.out.println("\n=== BUSCAR MASCOTA ===");
        System.out.println("[1] Buscar por ID");
        System.out.println("[2] Buscar por nombre");
        System.out.print("Seleccione opción: ");

        try {
            int opcion = Integer.parseInt(scanner.nextLine());
            switch (opcion) {
                case 1 -> {
                    System.out.print("Ingrese ID de la mascota: ");
                    Long id = Long.parseLong(scanner.nextLine());
                    mostrarDetallesMascota(id);
                }
                case 2 -> {
                    System.out.print("Ingrese nombre o parte del nombre: ");
                    String nombre = scanner.nextLine();
                    buscarPorNombre(nombre);
                }
                default -> System.out.println("❌ Opción no válida");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Formato numérico inválido");
        }
    }

    private void transferirPropiedadInteractivo(java.util.Scanner scanner) {
        System.out.println("\n=== TRANSFERIR PROPIEDAD DE MASCOTA ===");
        try {
            System.out.print("ID de la mascota a transferir: ");
            Long mascotaId = Long.parseLong(scanner.nextLine());

            System.out.print("ID del nuevo dueño: ");
            Long nuevoDuenoId = Long.parseLong(scanner.nextLine());

            transferirPropiedadMascota(mascotaId, nuevoDuenoId);

        } catch (NumberFormatException e) {
            System.out.println("❌ Por favor ingrese números válidos");
        }
    }

    private void mostrarMascotasPorDueno(java.util.Scanner scanner) {
        System.out.println("\n=== MASCOTAS POR DUEÑO ===");
        try {
            System.out.print("Ingrese ID del dueño: ");
            Long duenoId = Long.parseLong(scanner.nextLine());
            buscarPorDueno(duenoId);
        } catch (NumberFormatException e) {
            System.out.println("❌ Por favor ingrese un número válido");
        }
    }

    private void verificarMicrochipInteractivo(java.util.Scanner scanner) {
        System.out.println("\n=== VERIFICAR MICROCHIP ===");
        System.out.print("Ingrese número de microchip: ");
        String microchip = scanner.nextLine();

        boolean existe = existePorMicrochip(microchip);
        if (existe) {
            System.out.println("✅ Microchip encontrado en el sistema");
        } else {
            System.out.println("ℹ️ Microchip no registrado en el sistema");
        }
    }

    private void actualizarMascotaInteractivo(java.util.Scanner scanner) {
        System.out.println("\n=== ACTUALIZAR MASCOTA ===");
        try {
            System.out.print("ID de la mascota a actualizar: ");
            Long id = Long.parseLong(scanner.nextLine());

            Optional<Mascota> mascotaOpt = buscarPorId(id);
            if (mascotaOpt.isPresent()) {
                Mascota mascota = mascotaOpt.get();
                System.out.println("Datos actuales: " + mascota.getNombre());

                System.out.print("Nuevo peso (actual: " + mascota.getPesoActual() + " kg): ");
                String pesoStr = scanner.nextLine();
                if (!pesoStr.trim().isEmpty()) {
                    mascota.setPesoActual(Double.parseDouble(pesoStr));
                }

                actualizarMascota(id, mascota);
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Formato numérico inválido");
        }
    }

    private void eliminarMascotaInteractivo(java.util.Scanner scanner) {
        System.out.println("\n=== ELIMINAR MASCOTA ===");
        try {
            System.out.print("ID de la mascota a eliminar: ");
            Long id = Long.parseLong(scanner.nextLine());
            eliminarMascota(id);
        } catch (NumberFormatException e) {
            System.out.println("❌ Por favor ingrese un número válido");
        }
    }

}
