package com.happyfeet.controller;

import com.happyfeet.model.entities.Mascota;
import com.happyfeet.service.MascotaService;
import com.happyfeet.service.DuenoService;
import com.happyfeet.util.FileLogger;
import com.happyfeet.view.ConsoleUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Controlador para el módulo de Actividades Especiales que incluye:
 * - Días de Adopción
 * - Jornadas de Vacunación
 * - Club de Mascotas Frecuentes
 */
public class ActividadesEspecialesController {

    private static final FileLogger LOG = FileLogger.getInstance();

    private final MascotaService mascotaService;
    private final DuenoService duenoService;

    // Storage en memoria para las actividades especiales
    private final Map<Integer, MascotaAdopcion> mascotasAdopcion = new ConcurrentHashMap<>();
    private final Map<Integer, JornadaVacunacion> jornadasVacunacion = new ConcurrentHashMap<>();
    private final Map<Integer, ClienteFrecuente> clientesFrecuentes = new ConcurrentHashMap<>();

    private final AtomicInteger adopcionIdSequence = new AtomicInteger(1);
    private final AtomicInteger jornadaIdSequence = new AtomicInteger(1);

    public ActividadesEspecialesController(MascotaService mascotaService, DuenoService duenoService) {
        this.mascotaService = mascotaService;
        this.duenoService = duenoService;
        LOG.info("ActividadesEspecialesController inicializado");
        inicializarDatosEjemplo();
    }

    // ============ DÍAS DE ADOPCIÓN ============

    public static class MascotaAdopcion {
        private Integer id;
        private String nombre;
        private String especie;
        private String raza;
        private int edadMeses;
        private Mascota.Sexo sexo;
        private String descripcion;
        private String necesidadesEspeciales;
        private LocalDate fechaIngreso;
        private boolean adoptada;
        private String contactoResponsable;
        private String fotoUrl;

        // Constructor, getters y setters
        public MascotaAdopcion() {
            this.fechaIngreso = LocalDate.now();
            this.adoptada = false;
        }

        // Getters y setters
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getEspecie() { return especie; }
        public void setEspecie(String especie) { this.especie = especie; }
        public String getRaza() { return raza; }
        public void setRaza(String raza) { this.raza = raza; }
        public int getEdadMeses() { return edadMeses; }
        public void setEdadMeses(int edadMeses) { this.edadMeses = edadMeses; }
        public Mascota.Sexo getSexo() { return sexo; }
        public void setSexo(Mascota.Sexo sexo) { this.sexo = sexo; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
        public String getNecesidadesEspeciales() { return necesidadesEspeciales; }
        public void setNecesidadesEspeciales(String necesidadesEspeciales) { this.necesidadesEspeciales = necesidadesEspeciales; }
        public LocalDate getFechaIngreso() { return fechaIngreso; }
        public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
        public boolean isAdoptada() { return adoptada; }
        public void setAdoptada(boolean adoptada) { this.adoptada = adoptada; }
        public String getContactoResponsable() { return contactoResponsable; }
        public void setContactoResponsable(String contactoResponsable) { this.contactoResponsable = contactoResponsable; }
        public String getFotoUrl() { return fotoUrl; }
        public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

        @Override
        public String toString() {
            return String.format("%s - %s %s (%d meses) - %s",
                    nombre, especie, raza, edadMeses, adoptada ? "ADOPTADA" : "DISPONIBLE");
        }
    }

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
        List<MascotaAdopcion> disponibles = mascotasAdopcion.values().stream()
                .filter(m -> !m.isAdoptada())
                .sorted(Comparator.comparing(MascotaAdopcion::getFechaIngreso))
                .toList();

        if (disponibles.isEmpty()) {
            System.out.println("No hay mascotas disponibles para adopción");
            return;
        }

        System.out.println("\n=== MASCOTAS DISPONIBLES PARA ADOPCIÓN ===");
        System.out.printf("%-5s %-15s %-10s %-15s %-8s %-12s %-30s\n",
                "ID", "NOMBRE", "ESPECIE", "RAZA", "EDAD", "INGRESO", "DESCRIPCIÓN");
        System.out.println("-".repeat(100));

        for (MascotaAdopcion mascota : disponibles) {
            String descripcionCorta = mascota.getDescripcion() != null && mascota.getDescripcion().length() > 30 ?
                    mascota.getDescripcion().substring(0, 27) + "..." :
                    (mascota.getDescripcion() != null ? mascota.getDescripcion() : "N/A");

            System.out.printf("%-5d %-15s %-10s %-15s %-8d %-12s %-30s\n",
                    mascota.getId(),
                    mascota.getNombre(),
                    mascota.getEspecie(),
                    mascota.getRaza(),
                    mascota.getEdadMeses(),
                    mascota.getFechaIngreso(),
                    descripcionCorta);
        }
        System.out.printf("\nTotal de mascotas disponibles: %d\n", disponibles.size());
    }

    private void registrarMascotaAdopcion() {
        try {
            MascotaAdopcion nuevaMascota = new MascotaAdopcion();
            nuevaMascota.setId(adopcionIdSequence.getAndIncrement());

            nuevaMascota.setNombre(ConsoleUtils.readNonEmpty("Nombre de la mascota"));
            nuevaMascota.setEspecie(ConsoleUtils.readNonEmpty("Especie (Perro, Gato, etc.)"));
            nuevaMascota.setRaza(ConsoleUtils.readNonEmpty("Raza"));
            nuevaMascota.setEdadMeses(ConsoleUtils.readInt("Edad en meses"));

            // Sexo
            System.out.println("Sexo: 1-Macho, 2-Hembra");
            int sexoOpcion = ConsoleUtils.readInt("Seleccione sexo");
            nuevaMascota.setSexo(sexoOpcion == 1 ? Mascota.Sexo.MACHO : Mascota.Sexo.HEMBRA);

            nuevaMascota.setDescripcion(ConsoleUtils.readOptional("Descripción de la mascota"));
            nuevaMascota.setNecesidadesEspeciales(ConsoleUtils.readOptional("Necesidades especiales"));
            nuevaMascota.setContactoResponsable(ConsoleUtils.readOptional("Contacto responsable"));

            mascotasAdopcion.put(nuevaMascota.getId(), nuevaMascota);

            LOG.info("Mascota registrada para adopción: " + nuevaMascota.getNombre());
            System.out.println("✅ Mascota registrada para adopción exitosamente");

        } catch (Exception e) {
            LOG.error("Error registrando mascota para adopción", e);
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void marcarComoAdoptada() {
        try {
            listarMascotasDisponibles();
            int id = ConsoleUtils.readInt("ID de la mascota adoptada");

            MascotaAdopcion mascota = mascotasAdopcion.get(id);
            if (mascota == null) {
                System.out.println("No se encontró mascota con ID: " + id);
                return;
            }

            if (mascota.isAdoptada()) {
                System.out.println("Esta mascota ya fue adoptada");
                return;
            }

            System.out.println("Mascota: " + mascota.getNombre());
            if (ConsoleUtils.confirm("¿Confirma que fue adoptada?")) {
                mascota.setAdoptada(true);
                LOG.info("Mascota adoptada: " + mascota.getNombre());
                System.out.println("✅ Mascota marcada como adoptada");
            }

        } catch (Exception e) {
            LOG.error("Error marcando mascota como adoptada", e);
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void generarContratoAdopcion() {
        try {
            int mascotaId = ConsoleUtils.readInt("ID de la mascota");
            MascotaAdopcion mascota = mascotasAdopcion.get(mascotaId);

            if (mascota == null || !mascota.isAdoptada()) {
                System.out.println("Mascota no encontrada o no adoptada");
                return;
            }

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
            - Edad: %d meses
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
                mascota.getEdadMeses(),
                mascota.getSexo(),
                adoptante,
                documento,
                direccion,
                telefono);
    }

    private void buscarMascotaAdopcion() {
        String termino = ConsoleUtils.readNonEmpty("Término de búsqueda (nombre, especie, raza)");
        String terminoLower = termino.toLowerCase();

        List<MascotaAdopcion> resultados = mascotasAdopcion.values().stream()
                .filter(m -> m.getNombre().toLowerCase().contains(terminoLower) ||
                        m.getEspecie().toLowerCase().contains(terminoLower) ||
                        m.getRaza().toLowerCase().contains(terminoLower))
                .toList();

        if (resultados.isEmpty()) {
            System.out.println("No se encontraron mascotas con: " + termino);
            return;
        }

        System.out.println("\n=== RESULTADOS DE BÚSQUEDA ===");
        resultados.forEach(System.out::println);
    }

    private void mostrarEstadisticasAdopcion() {
        long totalMascotas = mascotasAdopcion.size();
        long adoptadas = mascotasAdopcion.values().stream().filter(MascotaAdopcion::isAdoptada).count();
        long disponibles = totalMascotas - adoptadas;

        System.out.println("\n=== ESTADÍSTICAS DE ADOPCIÓN ===");
        System.out.println("Total de mascotas registradas: " + totalMascotas);
        System.out.println("Adoptadas: " + adoptadas);
        System.out.println("Disponibles: " + disponibles);

        if (totalMascotas > 0) {
            double porcentajeAdopcion = (adoptadas * 100.0) / totalMascotas;
            System.out.printf("Porcentaje de adopción: %.1f%%\n", porcentajeAdopcion);
        }
    }

    // ============ JORNADAS DE VACUNACIÓN ============

    public static class JornadaVacunacion {
        private Integer id;
        private String nombre;
        private LocalDate fecha;
        private String ubicacion;
        private String vacunasDisponibles;
        private BigDecimal precioEspecial;
        private int capacidadMaxima;
        private List<RegistroVacunacion> registros;
        private boolean activa;

        public JornadaVacunacion() {
            this.registros = new ArrayList<>();
            this.activa = true;
        }

        // Getters y setters
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public LocalDate getFecha() { return fecha; }
        public void setFecha(LocalDate fecha) { this.fecha = fecha; }
        public String getUbicacion() { return ubicacion; }
        public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
        public String getVacunasDisponibles() { return vacunasDisponibles; }
        public void setVacunasDisponibles(String vacunasDisponibles) { this.vacunasDisponibles = vacunasDisponibles; }
        public BigDecimal getPrecioEspecial() { return precioEspecial; }
        public void setPrecioEspecial(BigDecimal precioEspecial) { this.precioEspecial = precioEspecial; }
        public int getCapacidadMaxima() { return capacidadMaxima; }
        public void setCapacidadMaxima(int capacidadMaxima) { this.capacidadMaxima = capacidadMaxima; }
        public List<RegistroVacunacion> getRegistros() { return registros; }
        public boolean isActiva() { return activa; }
        public void setActiva(boolean activa) { this.activa = activa; }

        public int getCapacidadDisponible() {
            return capacidadMaxima - registros.size();
        }

        public boolean puedeRecibirMas() {
            return activa && getCapacidadDisponible() > 0;
        }
    }

    public static class RegistroVacunacion {
        private String nombreMascota;
        private String nombreDueno;
        private String telefono;
        private String vacunaAplicada;
        private LocalDateTime fechaHora;

        public RegistroVacunacion(String nombreMascota, String nombreDueno, String telefono, String vacuna) {
            this.nombreMascota = nombreMascota;
            this.nombreDueno = nombreDueno;
            this.telefono = telefono;
            this.vacunaAplicada = vacuna;
            this.fechaHora = LocalDateTime.now();
        }

        // Getters
        public String getNombreMascota() { return nombreMascota; }
        public String getNombreDueno() { return nombreDueno; }
        public String getTelefono() { return telefono; }
        public String getVacunaAplicada() { return vacunaAplicada; }
        public LocalDateTime getFechaHora() { return fechaHora; }
    }

    public void gestionarJornadasVacunacion() {
        while (true) {
            int opcion = ConsoleUtils.menu("Jornadas de Vacunación",
                    "Listar jornadas activas",
                    "Crear nueva jornada",
                    "Registrar vacunación",
                    "Ver registros de jornada",
                    "Cerrar jornada",
                    "Reporte de jornadas"
            );

            switch (opcion) {
                case 1 -> listarJornadasActivas();
                case 2 -> crearJornadaVacunacion();
                case 3 -> registrarVacunacion();
                case 4 -> verRegistrosJornada();
                case 5 -> cerrarJornada();
                case 6 -> reporteJornadas();
                case 0 -> { return; }
            }
            ConsoleUtils.pause();
        }
    }

    private void listarJornadasActivas() {
        List<JornadaVacunacion> activas = jornadasVacunacion.values().stream()
                .filter(JornadaVacunacion::isActiva)
                .sorted(Comparator.comparing(JornadaVacunacion::getFecha))
                .toList();

        if (activas.isEmpty()) {
            System.out.println("No hay jornadas activas");
            return;
        }

        System.out.println("\n=== JORNADAS DE VACUNACIÓN ACTIVAS ===");
        System.out.printf("%-5s %-20s %-12s %-20s %-10s %-8s\n",
                "ID", "NOMBRE", "FECHA", "UBICACIÓN", "PRECIO", "CUPOS");
        System.out.println("-".repeat(80));

        for (JornadaVacunacion jornada : activas) {
            System.out.printf("%-5d %-20s %-12s %-20s $%-9.0f %-8s\n",
                    jornada.getId(),
                    jornada.getNombre().length() > 20 ? jornada.getNombre().substring(0, 17) + "..." : jornada.getNombre(),
                    jornada.getFecha(),
                    jornada.getUbicacion().length() > 20 ? jornada.getUbicacion().substring(0, 17) + "..." : jornada.getUbicacion(),
                    jornada.getPrecioEspecial(),
                    jornada.getCapacidadDisponible() + "/" + jornada.getCapacidadMaxima());
        }
    }

    private void crearJornadaVacunacion() {
        try {
            JornadaVacunacion jornada = new JornadaVacunacion();
            jornada.setId(jornadaIdSequence.getAndIncrement());

            jornada.setNombre(ConsoleUtils.readNonEmpty("Nombre de la jornada"));
            jornada.setFecha(ConsoleUtils.readDate("Fecha de la jornada"));
            jornada.setUbicacion(ConsoleUtils.readNonEmpty("Ubicación"));
            jornada.setVacunasDisponibles(ConsoleUtils.readNonEmpty("Vacunas disponibles"));
            jornada.setPrecioEspecial(new BigDecimal(ConsoleUtils.readDouble("Precio especial")));
            jornada.setCapacidadMaxima(ConsoleUtils.readInt("Capacidad máxima"));

            jornadasVacunacion.put(jornada.getId(), jornada);

            LOG.info("Jornada de vacunación creada: " + jornada.getNombre());
            System.out.println("✅ Jornada creada exitosamente");

        } catch (Exception e) {
            LOG.error("Error creando jornada", e);
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void registrarVacunacion() {
        try {
            listarJornadasActivas();
            int jornadaId = ConsoleUtils.readInt("ID de la jornada");

            JornadaVacunacion jornada = jornadasVacunacion.get(jornadaId);
            if (jornada == null || !jornada.isActiva()) {
                System.out.println("Jornada no encontrada o inactiva");
                return;
            }

            if (!jornada.puedeRecibirMas()) {
                System.out.println("La jornada ha alcanzado su capacidad máxima");
                return;
            }

            String nombreMascota = ConsoleUtils.readNonEmpty("Nombre de la mascota");
            String nombreDueno = ConsoleUtils.readNonEmpty("Nombre del dueño");
            String telefono = ConsoleUtils.readNonEmpty("Teléfono");

            System.out.println("Vacunas disponibles: " + jornada.getVacunasDisponibles());
            String vacuna = ConsoleUtils.readNonEmpty("Vacuna aplicada");

            RegistroVacunacion registro = new RegistroVacunacion(nombreMascota, nombreDueno, telefono, vacuna);
            jornada.getRegistros().add(registro);

            LOG.info("Vacunación registrada en jornada " + jornada.getNombre() + " para " + nombreMascota);
            System.out.printf("✅ Vacunación registrada. Precio: $%.0f\n", jornada.getPrecioEspecial());

        } catch (Exception e) {
            LOG.error("Error registrando vacunación", e);
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void verRegistrosJornada() {
        int jornadaId = ConsoleUtils.readInt("ID de la jornada");
        JornadaVacunacion jornada = jornadasVacunacion.get(jornadaId);

        if (jornada == null) {
            System.out.println("Jornada no encontrada");
            return;
        }

        System.out.println("\n=== REGISTROS DE " + jornada.getNombre() + " ===");
        if (jornada.getRegistros().isEmpty()) {
            System.out.println("No hay registros para esta jornada");
            return;
        }

        System.out.printf("%-20s %-20s %-15s %-15s %-12s\n",
                "MASCOTA", "DUEÑO", "TELÉFONO", "VACUNA", "FECHA/HORA");
        System.out.println("-".repeat(85));

        for (RegistroVacunacion registro : jornada.getRegistros()) {
            System.out.printf("%-20s %-20s %-15s %-15s %-12s\n",
                    registro.getNombreMascota(),
                    registro.getNombreDueno(),
                    registro.getTelefono(),
                    registro.getVacunaAplicada(),
                    registro.getFechaHora().toLocalTime());
        }
    }

    private void cerrarJornada() {
        int jornadaId = ConsoleUtils.readInt("ID de la jornada");
        JornadaVacunacion jornada = jornadasVacunacion.get(jornadaId);

        if (jornada == null) {
            System.out.println("Jornada no encontrada");
            return;
        }

        if (ConsoleUtils.confirm("¿Confirma cerrar la jornada " + jornada.getNombre() + "?")) {
            jornada.setActiva(false);
            LOG.info("Jornada cerrada: " + jornada.getNombre());
            System.out.println("✅ Jornada cerrada exitosamente");
        }
    }

    private void reporteJornadas() {
        System.out.println("\n=== REPORTE DE JORNADAS ===");

        long totalJornadas = jornadasVacunacion.size();
        long jornadasActivas = jornadasVacunacion.values().stream().filter(JornadaVacunacion::isActiva).count();
        int totalVacunaciones = jornadasVacunacion.values().stream()
                .mapToInt(j -> j.getRegistros().size()).sum();

        System.out.println("Total de jornadas: " + totalJornadas);
        System.out.println("Jornadas activas: " + jornadasActivas);
        System.out.println("Total de vacunaciones: " + totalVacunaciones);

        if (totalJornadas > 0) {
            double promedioVacunaciones = (double) totalVacunaciones / totalJornadas;
            System.out.printf("Promedio vacunaciones por jornada: %.1f\n", promedioVacunaciones);
        }
    }

    // ============ CLUB DE MASCOTAS FRECUENTES ============

    public static class ClienteFrecuente {
        private Integer duenoId;
        private String nombreCompleto;
        private String email;
        private int puntosAcumulados;
        private BigDecimal totalGastado;
        private LocalDate fechaRegistro;
        private String nivelCliente;
        private List<MovimientoPuntos> historialPuntos;

        public ClienteFrecuente() {
            this.historialPuntos = new ArrayList<>();
            this.fechaRegistro = LocalDate.now();
            this.puntosAcumulados = 0;
            this.totalGastado = BigDecimal.ZERO;
            actualizarNivel();
        }

        public void agregarPuntos(int puntos, String concepto) {
            this.puntosAcumulados += puntos;
            this.historialPuntos.add(new MovimientoPuntos(puntos, concepto, "GANADOS"));
            actualizarNivel();
        }

        public boolean canjearPuntos(int puntos, String concepto) {
            if (puntos <= puntosAcumulados) {
                this.puntosAcumulados -= puntos;
                this.historialPuntos.add(new MovimientoPuntos(-puntos, concepto, "CANJEADOS"));
                return true;
            }
            return false;
        }

        public void registrarCompra(BigDecimal monto) {
            this.totalGastado = this.totalGastado.add(monto);
            // 1 punto por cada $1000 gastados
            int puntosGanados = monto.divide(new BigDecimal("1000")).intValue();
            if (puntosGanados > 0) {
                agregarPuntos(puntosGanados, "Compra $" + monto);
            }
        }

        private void actualizarNivel() {
            if (totalGastado.compareTo(new BigDecimal("500000")) >= 0) {
                nivelCliente = "PLATINO";
            } else if (totalGastado.compareTo(new BigDecimal("200000")) >= 0) {
                nivelCliente = "ORO";
            } else if (totalGastado.compareTo(new BigDecimal("50000")) >= 0) {
                nivelCliente = "PLATA";
            } else {
                nivelCliente = "BRONCE";
            }
        }

        // Getters y setters
        public Integer getDuenoId() { return duenoId; }
        public void setDuenoId(Integer duenoId) { this.duenoId = duenoId; }
        public String getNombreCompleto() { return nombreCompleto; }
        public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public int getPuntosAcumulados() { return puntosAcumulados; }
        public BigDecimal getTotalGastado() { return totalGastado; }
        public LocalDate getFechaRegistro() { return fechaRegistro; }
        public String getNivelCliente() { return nivelCliente; }
        public List<MovimientoPuntos> getHistorialPuntos() { return historialPuntos; }
    }

    public static class MovimientoPuntos {
        private int puntos;
        private String concepto;
        private String tipo;
        private LocalDateTime fecha;

        public MovimientoPuntos(int puntos, String concepto, String tipo) {
            this.puntos = puntos;
            this.concepto = concepto;
            this.tipo = tipo;
            this.fecha = LocalDateTime.now();
        }

        // Getters
        public int getPuntos() { return puntos; }
        public String getConcepto() { return concepto; }
        public String getTipo() { return tipo; }
        public LocalDateTime getFecha() { return fecha; }
    }

    public void gestionarClubFrecuentes() {
        while (true) {
            int opcion = ConsoleUtils.menu("Club de Mascotas Frecuentes",
                    "Listar clientes frecuentes",
                    "Registrar nuevo cliente",
                    "Registrar compra",
                    "Canjear puntos",
                    "Ver historial de cliente",
                    "Reporte del club"
            );

            switch (opcion) {
                case 1 -> listarClientesFrecuentes();
                case 2 -> registrarClienteFrecuente();
                case 3 -> registrarCompraCliente();
                case 4 -> canjearPuntosCliente();
                case 5 -> verHistorialCliente();
                case 6 -> reporteClubFrecuentes();
                case 0 -> { return; }
            }
            ConsoleUtils.pause();
        }
    }

    private void listarClientesFrecuentes() {
        if (clientesFrecuentes.isEmpty()) {
            System.out.println("No hay clientes frecuentes registrados");
            return;
        }

        System.out.println("\n=== CLIENTES FRECUENTES ===");
        System.out.printf("%-5s %-25s %-10s %-8s %-10s %-12s\n",
                "ID", "CLIENTE", "NIVEL", "PUNTOS", "GASTADO", "REGISTRO");
        System.out.println("-".repeat(75));

        clientesFrecuentes.values().stream()
                .sorted(Comparator.comparing(ClienteFrecuente::getPuntosAcumulados).reversed())
                .forEach(cliente -> {
                    String nombreCorto = cliente.getNombreCompleto().length() > 25 ?
                            cliente.getNombreCompleto().substring(0, 22) + "..." : cliente.getNombreCompleto();

                    System.out.printf("%-5d %-25s %-10s %-8d $%-9.0f %-12s\n",
                            cliente.getDuenoId(),
                            nombreCorto,
                            cliente.getNivelCliente(),
                            cliente.getPuntosAcumulados(),
                            cliente.getTotalGastado(),
                            cliente.getFechaRegistro());
                });
    }

    private void registrarClienteFrecuente() {
        try {
            int duenoId = ConsoleUtils.readInt("ID del dueño");

            if (clientesFrecuentes.containsKey(duenoId)) {
                System.out.println("El cliente ya está registrado en el club");
                return;
            }

            var duenoOpt = duenoService.buscarPorId((long) duenoId);
            if (duenoOpt.isEmpty()) {
                System.out.println("No se encontró dueño con ID: " + duenoId);
                return;
            }

            var dueno = duenoOpt.get();
            ClienteFrecuente cliente = new ClienteFrecuente();
            cliente.setDuenoId(duenoId);
            cliente.setNombreCompleto(dueno.getNombreCompleto());
            cliente.setEmail(dueno.getEmail());

            clientesFrecuentes.put(duenoId, cliente);

            LOG.info("Cliente frecuente registrado: " + dueno.getNombreCompleto());
            System.out.println("✅ Cliente registrado en el club exitosamente");

        } catch (Exception e) {
            LOG.error("Error registrando cliente frecuente", e);
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void registrarCompraCliente() {
        try {
            int duenoId = ConsoleUtils.readInt("ID del cliente");
            ClienteFrecuente cliente = clientesFrecuentes.get(duenoId);

            if (cliente == null) {
                System.out.println("Cliente no encontrado en el club");
                return;
            }

            BigDecimal montoCompra = new BigDecimal(ConsoleUtils.readDouble("Monto de la compra"));
            cliente.registrarCompra(montoCompra);

            System.out.printf("✅ Compra registrada. Nivel: %s, Puntos: %d\n",
                    cliente.getNivelCliente(), cliente.getPuntosAcumulados());

        } catch (Exception e) {
            LOG.error("Error registrando compra", e);
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void canjearPuntosCliente() {
        try {
            int duenoId = ConsoleUtils.readInt("ID del cliente");
            ClienteFrecuente cliente = clientesFrecuentes.get(duenoId);

            if (cliente == null) {
                System.out.println("Cliente no encontrado en el club");
                return;
            }

            System.out.println("Puntos disponibles: " + cliente.getPuntosAcumulados());
            int puntosACanjear = ConsoleUtils.readInt("Puntos a canjear");
            String concepto = ConsoleUtils.readNonEmpty("Concepto del canje");

            if (cliente.canjearPuntos(puntosACanjear, concepto)) {
                System.out.println("✅ Puntos canjeados exitosamente");
                System.out.println("Puntos restantes: " + cliente.getPuntosAcumulados());
            } else {
                System.out.println("Puntos insuficientes");
            }

        } catch (Exception e) {
            LOG.error("Error canjeando puntos", e);
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void verHistorialCliente() {
        int duenoId = ConsoleUtils.readInt("ID del cliente");
        ClienteFrecuente cliente = clientesFrecuentes.get(duenoId);

        if (cliente == null) {
            System.out.println("Cliente no encontrado");
            return;
        }

        System.out.println("\n=== HISTORIAL DE " + cliente.getNombreCompleto() + " ===");
        System.out.println("Nivel: " + cliente.getNivelCliente());
        System.out.println("Puntos actuales: " + cliente.getPuntosAcumulados());
        System.out.printf("Total gastado: $%.2f\n", cliente.getTotalGastado());

        if (cliente.getHistorialPuntos().isEmpty()) {
            System.out.println("No hay movimientos de puntos");
            return;
        }

        System.out.println("\n=== MOVIMIENTOS DE PUNTOS ===");
        System.out.printf("%-12s %-8s %-30s %-10s\n", "FECHA", "PUNTOS", "CONCEPTO", "TIPO");
        System.out.println("-".repeat(65));

        cliente.getHistorialPuntos().forEach(mov -> {
            System.out.printf("%-12s %-8d %-30s %-10s\n",
                    mov.getFecha().toLocalDate(),
                    mov.getPuntos(),
                    mov.getConcepto().length() > 30 ? mov.getConcepto().substring(0, 27) + "..." : mov.getConcepto(),
                    mov.getTipo());
        });
    }

    private void reporteClubFrecuentes() {
        System.out.println("\n=== REPORTE CLUB FRECUENTES ===");

        long totalClientes = clientesFrecuentes.size();
        int totalPuntos = clientesFrecuentes.values().stream()
                .mapToInt(ClienteFrecuente::getPuntosAcumulados).sum();
        BigDecimal totalGastado = clientesFrecuentes.values().stream()
                .map(ClienteFrecuente::getTotalGastado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        System.out.println("Total clientes: " + totalClientes);
        System.out.println("Puntos en circulación: " + totalPuntos);
        System.out.printf("Ventas del club: $%.2f\n", totalGastado);

        // Por niveles
        Map<String, Long> clientesPorNivel = clientesFrecuentes.values().stream()
                .collect(Collectors.groupingBy(ClienteFrecuente::getNivelCliente, Collectors.counting()));

        System.out.println("\n=== CLIENTES POR NIVEL ===");
        clientesPorNivel.forEach((nivel, cantidad) ->
                System.out.println(nivel + ": " + cantidad));
    }

    // ============ DATOS DE EJEMPLO ============

    private void inicializarDatosEjemplo() {
        // Mascotas para adopción
        MascotaAdopcion mascota1 = new MascotaAdopcion();
        mascota1.setId(adopcionIdSequence.getAndIncrement());
        mascota1.setNombre("Luna");
        mascota1.setEspecie("Perro");
        mascota1.setRaza("Mestizo");
        mascota1.setEdadMeses(18);
        mascota1.setSexo(Mascota.Sexo.HEMBRA);
        mascota1.setDescripcion("Muy cariñosa y juguetona");
        mascota1.setContactoResponsable("Dr. García - 555-0123");
        mascotasAdopcion.put(mascota1.getId(), mascota1);

        MascotaAdopcion mascota2 = new MascotaAdopcion();
        mascota2.setId(adopcionIdSequence.getAndIncrement());
        mascota2.setNombre("Simón");
        mascota2.setEspecie("Gato");
        mascota2.setRaza("Siamés");
        mascota2.setEdadMeses(12);
        mascota2.setSexo(Mascota.Sexo.MACHO);
        mascota2.setDescripcion("Tranquilo, ideal para apartamento");
        mascotasAdopcion.put(mascota2.getId(), mascota2);

        // Jornada de vacunación ejemplo
        JornadaVacunacion jornada1 = new JornadaVacunacion();
        jornada1.setId(jornadaIdSequence.getAndIncrement());
        jornada1.setNombre("Jornada de Vacunación Enero 2025");
        jornada1.setFecha(LocalDate.now().plusDays(15));
        jornada1.setUbicacion("Parque Central");
        jornada1.setVacunasDisponibles("Triple, Antirrábica, Parvovirus");
        jornada1.setPrecioEspecial(new BigDecimal("25000"));
        jornada1.setCapacidadMaxima(50);
        jornadasVacunacion.put(jornada1.getId(), jornada1);

        LOG.info("Datos de ejemplo inicializados para actividades especiales");
    }

    // ============ MÉTODOS AUXILIARES ============

    public Map<Integer, MascotaAdopcion> getMascotasAdopcion() {
        return new HashMap<>(mascotasAdopcion);
    }

    public Map<Integer, JornadaVacunacion> getJornadasVacunacion() {
        return new HashMap<>(jornadasVacunacion);
    }

    public Map<Integer, ClienteFrecuente> getClientesFrecuentes() {
        return new HashMap<>(clientesFrecuentes);
    }

    public void limpiarDatos() {
        mascotasAdopcion.clear();
        jornadasVacunacion.clear();
        clientesFrecuentes.clear();
        LOG.info("Datos de actividades especiales limpiados");
    }

    // Métodos añadidos desde versión integrada

    
    
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
                case 0 -> {
                    return;
                }
                default -> System.out.println("Opción no válida");
            }

            ConsoleUtils.pause();
        }
    }

    private void mostrarResumenActividades() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║               RESUMEN DE ACTIVIDADES ESPECIALES             ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        // Resumen de adopciones
        long totalMascotasAdopcion = mascotasAdopcion.size();
        long adoptadas = mascotasAdopcion.values().stream().filter(MascotaAdopcion::isAdoptada).count();
        long disponibles = totalMascotasAdopcion - adoptadas;

        System.out.println("\n🏠 ADOPCIONES:");
        System.out.println("   • Total registradas: " + totalMascotasAdopcion);
        System.out.println("   • Adoptadas: " + adoptadas);
        System.out.println("   • Disponibles: " + disponibles);
        if (totalMascotasAdopcion > 0) {
            double porcentajeAdopcion = (adoptadas * 100.0) / totalMascotasAdopcion;
            System.out.printf("   • Tasa de adopción: %.1f%%\n", porcentajeAdopcion);
        }

        // Resumen de jornadas de vacunación
        long totalJornadas = jornadasVacunacion.size();
        long jornadasActivas = jornadasVacunacion.values().stream().filter(JornadaVacunacion::isActiva).count();
        int totalVacunaciones = jornadasVacunacion.values().stream()
                .mapToInt(j -> j.getRegistros().size()).sum();

        System.out.println("\n💉 JORNADAS DE VACUNACIÓN:");
        System.out.println("   • Total de jornadas: " + totalJornadas);
        System.out.println("   • Jornadas activas: " + jornadasActivas);
        System.out.println("   • Total vacunaciones: " + totalVacunaciones);

        // Resumen del club
        long totalClientes = clientesFrecuentes.size();
        int totalPuntos = clientesFrecuentes.values().stream()
                .mapToInt(ClienteFrecuente::getPuntosAcumulados).sum();
        BigDecimal totalGastado = clientesFrecuentes.values().stream()
                .map(ClienteFrecuente::getTotalGastado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        System.out.println("\n⭐ CLUB DE CLIENTES FRECUENTES:");
        System.out.println("   • Total clientes: " + totalClientes);
        System.out.println("   • Puntos en circulación: " + totalPuntos);
        System.out.printf("   • Ventas del club: $%.2f\n", totalGastado);

        if (totalClientes > 0) {
            // Top clientes
            ClienteFrecuente topCliente = clientesFrecuentes.values().stream()
                    .max(Comparator.comparing(ClienteFrecuente::getPuntosAcumulados))
                    .orElse(null);
            if (topCliente != null) {
                System.out.println("   • Top cliente: " + topCliente.getNombreCompleto() +
                        " (" + topCliente.getPuntosAcumulados() + " puntos)");
            }
        }

        System.out.println("\n📊 ESTADO GENERAL:");
        int actividadesTotales = (int) (totalMascotasAdopcion + totalJornadas + totalClientes);
        if (actividadesTotales > 0) {
            System.out.println("   ✅ Sistema de actividades especiales operativo");
            System.out.println("   📈 Total de registros: " + actividadesTotales);
        } else {
            System.out.println("   ℹ️ No hay actividades registradas");
        }
    }

}
