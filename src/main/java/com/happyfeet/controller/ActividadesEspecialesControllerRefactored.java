package com.happyfeet.controller;

import com.happyfeet.model.entities.*;
import com.happyfeet.service.MascotaAdopcionService;
import com.happyfeet.service.JornadaVacunacionService;
import com.happyfeet.service.DuenoService;
import com.happyfeet.repository.PuntosClienteRepository;
import com.happyfeet.repository.MovimientoPuntosRepository;
import com.happyfeet.repository.CompraClubFrecuenteRepository;
import com.happyfeet.util.FileLogger;
import com.happyfeet.view.ConsoleUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador REFACTORIZADO para el módulo de Actividades Especiales.
 * AHORA USA 100% BASE DE DATOS - NO HAY ALMACENAMIENTO EN MEMORIA
 *
 * Incluye:
 * - Días de Adopción (mascotas_adopcion)
 * - Jornadas de Vacunación (jornadas_vacunacion + registros_vacunacion_jornada)
 * - Club de Mascotas Frecuentes (puntos_cliente + movimientos_puntos + compras_club_frecuentes)
 */
public class ActividadesEspecialesControllerRefactored {

    private static final FileLogger LOG = FileLogger.getInstance();

    // SERVICIOS Y REPOSITORIOS - TODO EN BASE DE DATOS
    private final MascotaAdopcionService mascotaAdopcionService;
    private final JornadaVacunacionService jornadaVacunacionService;
    private final DuenoService duenoService;
    private final PuntosClienteRepository puntosClienteRepo;
    private final MovimientoPuntosRepository movimientoPuntosRepo;
    private final CompraClubFrecuenteRepository compraClubRepo;

    public ActividadesEspecialesControllerRefactored(
            MascotaAdopcionService mascotaAdopcionService,
            JornadaVacunacionService jornadaVacunacionService,
            DuenoService duenoService,
            PuntosClienteRepository puntosClienteRepo,
            MovimientoPuntosRepository movimientoPuntosRepo,
            CompraClubFrecuenteRepository compraClubRepo) {
        this.mascotaAdopcionService = mascotaAdopcionService;
        this.jornadaVacunacionService = jornadaVacunacionService;
        this.duenoService = duenoService;
        this.puntosClienteRepo = puntosClienteRepo;
        this.movimientoPuntosRepo = movimientoPuntosRepo;
        this.compraClubRepo = compraClubRepo;
        LOG.info("ActividadesEspecialesController REFACTORIZADO inicializado - 100% BASE DE DATOS");
    }

    // ============ MENÚ PRINCIPAL ============

    public void run() {
        while (true) {
            int opcion = ConsoleUtils.menu("ACTIVIDADES ESPECIALES",
                    "Gestión de Adopciones",
                    "Jornadas de Vacunación",
                    "Club de Mascotas Frecuentes",
                    "Resumen de Actividades"
            );

            switch (opcion) {
                case 1 -> gestionarAdopciones();
                case 2 -> gestionarJornadasVacunacion();
                case 3 -> gestionarClubFrecuentes();
                case 4 -> mostrarResumenActividades();
                case 0 -> { return; }
                default -> System.out.println("Opción no válida");
            }

            ConsoleUtils.pause();
        }
    }

    // ============ DÍAS DE ADOPCIÓN ============

    public void gestionarAdopciones() {
        while (true) {
            int opcion = ConsoleUtils.menu("Gestión de Adopciones",
                    "Listar mascotas disponibles",
                    "Registrar nueva mascota",
                    "Marcar como adoptada",
                    "Generar contrato de adopción",
                    "Buscar mascota",
                    "Estadísticas de adopción"
            );

            switch (opcion) {
                case 1 -> listarMascotasDisponibles();
                case 2 -> registrarMascotaAdopcion();
                case 3 -> marcarComoAdoptada();
                case 4 -> generarContratoAdopcion();
                case 5 -> buscarMascotaAdopcion();
                case 6 -> mostrarEstadisticasAdopcion();
                case 0 -> { return; }
            }
            ConsoleUtils.pause();
        }
    }

    private void listarMascotasDisponibles() {
        List<MascotaAdopcion> disponibles = mascotaAdopcionService.listarDisponibles();

        if (disponibles.isEmpty()) {
            System.out.println("No hay mascotas disponibles para adopción");
            return;
        }

        System.out.println("\n=== MASCOTAS DISPONIBLES PARA ADOPCIÓN ===");
        System.out.printf("%-5s %-15s %-10s %-15s %-10s %-12s %-30s\n",
                "ID", "NOMBRE", "ESPECIE", "RAZA", "EDAD", "INGRESO", "DESCRIPCIÓN");
        System.out.println("-".repeat(100));

        for (MascotaAdopcion mascota : disponibles) {
            String descripcionCorta = mascota.getDescripcion() != null && mascota.getDescripcion().length() > 30 ?
                    mascota.getDescripcion().substring(0, 27) + "..." :
                    (mascota.getDescripcion() != null ? mascota.getDescripcion() : "N/A");

            System.out.printf("%-5d %-15s %-10s %-15s %-10s %-12s %-30s\n",
                    mascota.getId(),
                    mascota.getNombre(),
                    mascota.getEspecie(),
                    mascota.getRaza() != null ? mascota.getRaza() : "Mestizo",
                    mascota.getEdadFormateada(),
                    mascota.getFechaIngreso(),
                    descripcionCorta);
        }
        System.out.printf("\nTotal de mascotas disponibles: %d\n", disponibles.size());
    }

    private void registrarMascotaAdopcion() {
        try {
            String nombre = ConsoleUtils.readNonEmpty("Nombre de la mascota");
            String especie = ConsoleUtils.readNonEmpty("Especie (Perro, Gato, etc.)");
            String raza = ConsoleUtils.readNonEmpty("Raza");
            int edadMeses = ConsoleUtils.readInt("Edad en meses");

            System.out.println("Sexo: 1-Macho, 2-Hembra");
            int sexoOpcion = ConsoleUtils.readInt("Seleccione sexo");
            MascotaAdopcion.Sexo sexo = sexoOpcion == 1 ? MascotaAdopcion.Sexo.MACHO : MascotaAdopcion.Sexo.HEMBRA;

            String descripcion = ConsoleUtils.readOptional("Descripción de la mascota");
            String necesidadesEspeciales = ConsoleUtils.readOptional("Necesidades especiales");
            String contactoResponsable = ConsoleUtils.readOptional("Contacto responsable");

            MascotaAdopcion nuevaMascota = MascotaAdopcion.builder()
                    .withNombre(nombre)
                    .withEspecie(especie)
                    .withRaza(raza)
                    .withEdadMeses(edadMeses)
                    .withSexo(sexo)
                    .withDescripcion(descripcion)
                    .withNecesidadesEspeciales(necesidadesEspeciales)
                    .withContactoResponsable(contactoResponsable)
                    .build();

            mascotaAdopcionService.registrarMascota(nuevaMascota);

            LOG.info("Mascota registrada para adopción en BD: " + nombre);
            System.out.println("✅ Mascota registrada para adopción exitosamente en la BASE DE DATOS");

        } catch (Exception e) {
            LOG.error("Error registrando mascota para adopción", e);
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void marcarComoAdoptada() {
        try {
            listarMascotasDisponibles();
            int id = ConsoleUtils.readInt("ID de la mascota adoptada");
            int duenoId = ConsoleUtils.readInt("ID del dueño adoptante");

            Optional<MascotaAdopcion> mascotaOpt = mascotaAdopcionService.buscarPorId(id);
            if (mascotaOpt.isEmpty()) {
                System.out.println("No se encontró mascota con ID: " + id);
                return;
            }

            MascotaAdopcion mascota = mascotaOpt.get();
            if (mascota.getAdoptada()) {
                System.out.println("Esta mascota ya fue adoptada");
                return;
            }

            System.out.println("Mascota: " + mascota.getNombre());
            if (ConsoleUtils.confirm("¿Confirma que fue adoptada?")) {
                boolean success = mascotaAdopcionService.procesarAdopcion(id, duenoId);
                if (success) {
                    LOG.info("Mascota adoptada en BD: " + mascota.getNombre());
                    System.out.println("✅ Mascota marcada como adoptada EN LA BASE DE DATOS");
                } else {
                    System.out.println("❌ Error al procesar la adopción");
                }
            }

        } catch (Exception e) {
            LOG.error("Error marcando mascota como adoptada", e);
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void generarContratoAdopcion() {
        try {
            int mascotaId = ConsoleUtils.readInt("ID de la mascota");
            Optional<MascotaAdopcion> mascotaOpt = mascotaAdopcionService.buscarPorId(mascotaId);

            if (mascotaOpt.isEmpty() || !mascotaOpt.get().getAdoptada()) {
                System.out.println("Mascota no encontrada o no adoptada");
                return;
            }

            MascotaAdopcion mascota = mascotaOpt.get();
            String adoptante = ConsoleUtils.readNonEmpty("Nombre del adoptante");
            String documentoAdoptante = ConsoleUtils.readNonEmpty("Documento del adoptante");
            String direccion = ConsoleUtils.readNonEmpty("Dirección del adoptante");
            String telefono = ConsoleUtils.readNonEmpty("Teléfono del adoptante");

            String contrato = generarTextoContrato(mascota, adoptante, documentoAdoptante, direccion, telefono);
            System.out.println("\n" + contrato);

            LOG.info("Contrato generado para adopción de: " + mascota.getNombre());

        } catch (Exception e) {
            LOG.error("Error generando contrato", e);
            System.err.println("Error: " + e.getMessage());
        }
    }

    private String generarTextoContrato(MascotaAdopcion mascota, String adoptante,
                                        String documento, String direccion, String telefono) {
        return String.format("""
            =====================================================
                      CONTRATO DE ADOPCIÓN DE MASCOTA
                    VETERINARIA HAPPY FEET
            =====================================================

            Fecha: %s

            DATOS DE LA MASCOTA:
            - Nombre: %s
            - Especie: %s
            - Raza: %s
            - Edad: %s
            - Sexo: %s

            DATOS DEL ADOPTANTE:
            - Nombre: %s
            - Documento: %s
            - Dirección: %s
            - Teléfono: %s

            COMPROMISOS DEL ADOPTANTE:
            1. Brindar cuidado, alimentación y refugio adecuado
            2. Proporcionar atención veterinaria cuando sea necesario
            3. No abandonar ni maltratar al animal
            4. Informar a Happy Feet en caso de no poder continuar con la adopción

            COMPROMISOS DE HAPPY FEET:
            1. Entregar la mascota en buen estado de salud
            2. Proporcionar información sobre cuidados específicos
            3. Ofrecer seguimiento post-adopción

            Firma Adoptante: _________________
            Firma Happy Feet: ________________

            =====================================================
            """,
                LocalDate.now(),
                mascota.getNombre(),
                mascota.getEspecie(),
                mascota.getRaza(),
                mascota.getEdadFormateada(),
                mascota.getSexo(),
                adoptante,
                documento,
                direccion,
                telefono);
    }

    private void buscarMascotaAdopcion() {
        String termino = ConsoleUtils.readNonEmpty("Especie a buscar (Perro, Gato, etc.)");
        List<MascotaAdopcion> resultados = mascotaAdopcionService.buscarPorEspecie(termino);

        if (resultados.isEmpty()) {
            System.out.println("No se encontraron mascotas con: " + termino);
            return;
        }

        System.out.println("\n=== RESULTADOS DE BÚSQUEDA ===");
        resultados.forEach(System.out::println);
    }

    private void mostrarEstadisticasAdopcion() {
        String estadisticas = mascotaAdopcionService.obtenerEstadisticas();
        System.out.println(estadisticas);
    }

    // ============ JORNADAS DE VACUNACIÓN ============

    public void gestionarJornadasVacunacion() {
        while (true) {
            int opcion = ConsoleUtils.menu("Jornadas de Vacunación",
                    "Listar jornadas activas",
                    "Crear nueva jornada",
                    "Registrar vacunación",
                    "Ver reporte de jornada",
                    "Cerrar jornada"
            );

            switch (opcion) {
                case 1 -> listarJornadasActivas();
                case 2 -> crearJornadaVacunacion();
                case 3 -> registrarVacunacion();
                case 4 -> verReporteJornada();
                case 5 -> cerrarJornada();
                case 0 -> { return; }
            }
            ConsoleUtils.pause();
        }
    }

    private void listarJornadasActivas() {
        List<JornadaVacunacion> activas = jornadaVacunacionService.listarActivas();

        if (activas.isEmpty()) {
            System.out.println("No hay jornadas activas");
            return;
        }

        System.out.println("\n=== JORNADAS DE VACUNACIÓN ACTIVAS ===");
        System.out.printf("%-5s %-20s %-12s %-20s %-10s %-8s\n",
                "ID", "NOMBRE", "FECHA", "UBICACIÓN", "PRECIO", "ESTADO");
        System.out.println("-".repeat(80));

        for (JornadaVacunacion jornada : activas) {
            String nombreCorto = jornada.getNombre().length() > 20 ?
                    jornada.getNombre().substring(0, 17) + "..." : jornada.getNombre();
            String ubicacionCorta = jornada.getUbicacion().length() > 20 ?
                    jornada.getUbicacion().substring(0, 17) + "..." : jornada.getUbicacion();

            System.out.printf("%-5d %-20s %-12s %-20s $%-9.0f %-8s\n",
                    jornada.getId(),
                    nombreCorto,
                    jornada.getFecha(),
                    ubicacionCorta,
                    jornada.getPrecioEspecial(),
                    jornada.getActiva() ? "ACTIVA" : "CERRADA");
        }
    }

    private void crearJornadaVacunacion() {
        try {
            String nombre = ConsoleUtils.readNonEmpty("Nombre de la jornada");
            LocalDate fecha = ConsoleUtils.readDate("Fecha de la jornada");
            String ubicacion = ConsoleUtils.readNonEmpty("Ubicación");
            String vacunasDisponibles = ConsoleUtils.readNonEmpty("Vacunas disponibles");
            BigDecimal precioEspecial = new BigDecimal(ConsoleUtils.readDouble("Precio especial"));
            int capacidadMaxima = ConsoleUtils.readInt("Capacidad máxima");

            JornadaVacunacion jornada = JornadaVacunacion.builder()
                    .withNombre(nombre)
                    .withFecha(fecha)
                    .withUbicacion(ubicacion)
                    .withVacunasDisponibles(vacunasDisponibles)
                    .withPrecioEspecial(precioEspecial)
                    .withCapacidadMaxima(capacidadMaxima)
                    .build();

            jornadaVacunacionService.crearJornada(jornada);

            LOG.info("Jornada de vacunación creada en BD: " + nombre);
            System.out.println("✅ Jornada creada exitosamente EN LA BASE DE DATOS");

        } catch (Exception e) {
            LOG.error("Error creando jornada", e);
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void registrarVacunacion() {
        try {
            listarJornadasActivas();
            int jornadaId = ConsoleUtils.readInt("ID de la jornada");

            String nombreMascota = ConsoleUtils.readNonEmpty("Nombre de la mascota");
            String nombreDueno = ConsoleUtils.readNonEmpty("Nombre del dueño");
            String telefono = ConsoleUtils.readNonEmpty("Teléfono");
            String vacuna = ConsoleUtils.readNonEmpty("Vacuna aplicada");

            jornadaVacunacionService.registrarVacunacion(jornadaId, nombreMascota,
                    nombreDueno, telefono, vacuna);

            LOG.info("Vacunación registrada en BD para jornada ID: " + jornadaId);
            System.out.println("✅ Vacunación registrada EN LA BASE DE DATOS");

        } catch (Exception e) {
            LOG.error("Error registrando vacunación", e);
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void verReporteJornada() {
        int jornadaId = ConsoleUtils.readInt("ID de la jornada");
        String reporte = jornadaVacunacionService.generarReporte(jornadaId);
        System.out.println(reporte);
    }

    private void cerrarJornada() {
        int jornadaId = ConsoleUtils.readInt("ID de la jornada");

        if (ConsoleUtils.confirm("¿Confirma cerrar la jornada?")) {
            boolean success = jornadaVacunacionService.cerrarJornada(jornadaId);
            if (success) {
                LOG.info("Jornada cerrada en BD: ID " + jornadaId);
                System.out.println("✅ Jornada cerrada exitosamente EN LA BASE DE DATOS");
            } else {
                System.out.println("❌ Error al cerrar la jornada");
            }
        }
    }

    // ============ CLUB DE MASCOTAS FRECUENTES ============

    public void gestionarClubFrecuentes() {
        while (true) {
            int opcion = ConsoleUtils.menu("Club de Mascotas Frecuentes",
                    "Listar clientes frecuentes",
                    "Registrar nueva compra",
                    "Ver historial de puntos",
                    "Reporte del club"
            );

            switch (opcion) {
                case 1 -> listarClientesFrecuentes();
                case 2 -> registrarCompraCliente();
                case 3 -> verHistorialPuntos();
                case 4 -> reporteClubFrecuentes();
                case 0 -> { return; }
            }
            ConsoleUtils.pause();
        }
    }

    private void listarClientesFrecuentes() {
        List<PuntosCliente> clientes = puntosClienteRepo.findAll();

        if (clientes.isEmpty()) {
            System.out.println("No hay clientes frecuentes registrados");
            return;
        }

        System.out.println("\n=== CLIENTES FRECUENTES ===");
        System.out.printf("%-5s %-25s %-10s %-10s\n",
                "ID", "CLIENTE", "PUNTOS", "NIVEL");
        System.out.println("-".repeat(55));

        for (PuntosCliente cliente : clientes) {
            // Calcular total gastado desde compras
            BigDecimal totalGastado = compraClubRepo.calcularTotalGastado(cliente.getDuenoId());
            String nivel = calcularNivel(totalGastado);

            Optional<Dueno> duenoOpt = duenoService.buscarPorId((long) cliente.getDuenoId());
            String nombreCliente = duenoOpt.map(Dueno::getNombreCompleto).orElse("N/A");

            System.out.printf("%-5d %-25s %-10d %-10s\n",
                    cliente.getDuenoId(),
                    nombreCliente.length() > 25 ? nombreCliente.substring(0, 22) + "..." : nombreCliente,
                    cliente.getPuntosAcumulados(),
                    nivel);
        }
    }

    private void registrarCompraCliente() {
        try {
            int duenoId = ConsoleUtils.readInt("ID del cliente");
            BigDecimal montoCompra = new BigDecimal(ConsoleUtils.readDouble("Monto de la compra"));
            String descripcion = ConsoleUtils.readOptional("Descripción (opcional)");

            // Calcular puntos: 1 punto por cada $1000
            int puntosGenerados = CompraClubFrecuente.calcularPuntos(montoCompra);

            // Registrar compra
            CompraClubFrecuente compra = CompraClubFrecuente.builder()
                    .withDuenoId(duenoId)
                    .withMonto(montoCompra)
                    .withPuntosGenerados(puntosGenerados)
                    .withDescripcion(descripcion)
                    .build();
            compraClubRepo.save(compra);

            // Registrar movimiento de puntos
            if (puntosGenerados > 0) {
                MovimientoPuntos movimiento = MovimientoPuntos.builder()
                        .withDuenoId(duenoId)
                        .withPuntos(puntosGenerados)
                        .withConcepto("Compra $" + montoCompra)
                        .withTipo(MovimientoPuntos.TipoMovimiento.GANADOS)
                        .build();
                movimientoPuntosRepo.save(movimiento);

                // Actualizar puntos acumulados
                PuntosCliente puntos = puntosClienteRepo.findByDuenoId(duenoId);
                if (puntos != null) {
                    // Actualizar los puntos existentes
                    puntos.setPuntosAcumulados(puntos.getPuntosAcumulados() + puntosGenerados);
                    puntosClienteRepo.update(puntos);
                } else {
                    // Crear registro si no existe
                    PuntosCliente nuevosPuntos = new PuntosCliente();
                    nuevosPuntos.setDuenoId(duenoId);
                    nuevosPuntos.setPuntosAcumulados(puntosGenerados);
                    puntosClienteRepo.save(nuevosPuntos);
                }
            }

            BigDecimal totalGastado = compraClubRepo.calcularTotalGastado(duenoId);
            String nivel = calcularNivel(totalGastado);

            LOG.info("Compra registrada en BD para cliente ID: " + duenoId);
            System.out.printf("✅ Compra registrada EN LA BASE DE DATOS. Nivel: %s, Puntos ganados: %d\n",
                    nivel, puntosGenerados);

        } catch (Exception e) {
            LOG.error("Error registrando compra", e);
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void verHistorialPuntos() {
        int duenoId = ConsoleUtils.readInt("ID del cliente");

        Optional<Dueno> duenoOpt = duenoService.buscarPorId((long) duenoId);
        if (duenoOpt.isEmpty()) {
            System.out.println("Cliente no encontrado");
            return;
        }

        Dueno dueno = duenoOpt.get();
        int totalPuntos = movimientoPuntosRepo.calcularTotalPuntos(duenoId);
        BigDecimal totalGastado = compraClubRepo.calcularTotalGastado(duenoId);
        String nivel = calcularNivel(totalGastado);

        System.out.println("\n=== HISTORIAL DE " + dueno.getNombreCompleto() + " ===");
        System.out.println("Nivel: " + nivel);
        System.out.println("Puntos actuales: " + totalPuntos);
        System.out.printf("Total gastado: $%.2f\n", totalGastado);

        List<MovimientoPuntos> movimientos = movimientoPuntosRepo.findByDuenoIdOrderByFechaDesc(duenoId);
        if (movimientos.isEmpty()) {
            System.out.println("No hay movimientos de puntos");
            return;
        }

        System.out.println("\n=== MOVIMIENTOS DE PUNTOS ===");
        System.out.printf("%-12s %-8s %-30s %-10s\n", "FECHA", "PUNTOS", "CONCEPTO", "TIPO");
        System.out.println("-".repeat(65));

        movimientos.forEach(mov -> {
            System.out.printf("%-12s %-8d %-30s %-10s\n",
                    mov.getFecha().toLocalDate(),
                    mov.getPuntos(),
                    mov.getConcepto().length() > 30 ? mov.getConcepto().substring(0, 27) + "..." : mov.getConcepto(),
                    mov.getTipo().getNombre());
        });
    }

    private void reporteClubFrecuentes() {
        List<PuntosCliente> todosClientes = puntosClienteRepo.findAll();
        List<CompraClubFrecuente> todasCompras = compraClubRepo.findAll();

        long totalClientes = todosClientes.size();
        int totalPuntos = todosClientes.stream()
                .mapToInt(PuntosCliente::getPuntosAcumulados)
                .sum();
        BigDecimal totalGastado = todasCompras.stream()
                .map(CompraClubFrecuente::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        System.out.println("\n=== REPORTE CLUB FRECUENTES ===");
        System.out.println("Total clientes: " + totalClientes);
        System.out.println("Puntos en circulación: " + totalPuntos);
        System.out.printf("Ventas del club: $%.2f\n", totalGastado);

        // Por niveles
        Map<String, Long> clientesPorNivel = todosClientes.stream()
                .collect(Collectors.groupingBy(c -> {
                    BigDecimal gastado = compraClubRepo.calcularTotalGastado(c.getDuenoId());
                    return calcularNivel(gastado);
                }, Collectors.counting()));

        System.out.println("\n=== CLIENTES POR NIVEL ===");
        clientesPorNivel.forEach((nivel, cantidad) ->
                System.out.println(nivel + ": " + cantidad));
    }

    private String calcularNivel(BigDecimal totalGastado) {
        if (totalGastado.compareTo(new BigDecimal("500000")) >= 0) {
            return "PLATINO";
        } else if (totalGastado.compareTo(new BigDecimal("200000")) >= 0) {
            return "ORO";
        } else if (totalGastado.compareTo(new BigDecimal("50000")) >= 0) {
            return "PLATA";
        } else {
            return "BRONCE";
        }
    }

    // ============ RESUMEN GENERAL ============

    private void mostrarResumenActividades() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║           RESUMEN DE ACTIVIDADES ESPECIALES (BD)           ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        // Resumen de adopciones desde BD
        long disponibles = mascotaAdopcionService.listarDisponibles().size();
        long adoptadas = mascotaAdopcionService.listarAdoptadas().size();
        long totalMascotas = disponibles + adoptadas;

        System.out.println("\n🏠 ADOPCIONES (desde BASE DE DATOS):");
        System.out.println("   • Total registradas: " + totalMascotas);
        System.out.println("   • Adoptadas: " + adoptadas);
        System.out.println("   • Disponibles: " + disponibles);
        if (totalMascotas > 0) {
            double porcentaje = (adoptadas * 100.0) / totalMascotas;
            System.out.printf("   • Tasa de adopción: %.1f%%\n", porcentaje);
        }

        // Resumen de jornadas desde BD
        List<JornadaVacunacion> jornadas = jornadaVacunacionService.listarActivas();
        System.out.println("\n💉 JORNADAS DE VACUNACIÓN (desde BASE DE DATOS):");
        System.out.println("   • Jornadas activas: " + jornadas.size());

        // Resumen del club desde BD
        List<PuntosCliente> clientes = puntosClienteRepo.findAll();
        int totalPuntos = clientes.stream().mapToInt(PuntosCliente::getPuntosAcumulados).sum();
        List<CompraClubFrecuente> compras = compraClubRepo.findAll();
        BigDecimal totalGastado = compras.stream()
                .map(CompraClubFrecuente::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        System.out.println("\n⭐ CLUB DE CLIENTES FRECUENTES (desde BASE DE DATOS):");
        System.out.println("   • Total clientes: " + clientes.size());
        System.out.println("   • Puntos en circulación: " + totalPuntos);
        System.out.printf("   • Ventas del club: $%.2f\n", totalGastado);

        System.out.println("\n📊 ESTADO:");
        System.out.println("   ✅ Sistema funcionando 100% con BASE DE DATOS");
        System.out.println("   ✅ NO hay datos en memoria - TODO persiste en MySQL");
    }
}