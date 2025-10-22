package com.happyfeet.model.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class HistorialMedico {
    private Integer id;
    private Integer mascotaId;
    private Long citaId;
    private Integer eventoMedicoId;
    private LocalDateTime fechaEvento;
    private Integer veterinarioId;
    private BigDecimal temperatura;
    private Integer frecuenciaCardiaca;
    private Integer frecuenciaRespiratoria;
    private BigDecimal peso;
    private String sintomas;
    private String diagnostico;
    private String tratamientoPrescrito;
    private String medicamentosRecetados;
    private String observaciones;
    private String recomendaciones;
    private LocalDate fechaProximoControl;
    private Boolean requiereSeguimiento;
    private LocalDateTime fechaRegistro;

    // Referencias a entidades relacionadas
    private Mascota mascota;
    private Cita cita;
    private Veterinario veterinario;
    private EventoMedico eventoMedico;

    // Lista de tratamientos asociados (Composite Pattern)
    private final List<Tratamiento> tratamientos;

    // Constantes médicas
    private static final BigDecimal TEMPERATURA_NORMAL_MIN = new BigDecimal("37.5");
    private static final BigDecimal TEMPERATURA_NORMAL_MAX = new BigDecimal("39.2");
    private static final int FC_NORMAL_MIN = 60;
    private static final int FC_NORMAL_MAX = 140;
    private static final int FR_NORMAL_MIN = 10;
    private static final int FR_NORMAL_MAX = 30;


    public enum TipoEventoMedico {
        CONSULTA("Consulta", "Consulta médica general", false) {
            @Override
            public boolean requiereDiagnostico() {
                return true;
            }

            @Override
            public boolean permiteTratamientosMultiples() {
                return true;
            }
        },

        VACUNACION("Vacunación", "Aplicación de vacuna", true) {
            @Override
            public boolean requiereDiagnostico() {
                return false;
            }

            @Override
            public boolean permiteTratamientosMultiples() {
                return false;
            }
        },

        CIRUGIA("Cirugía", "Procedimiento quirúrgico", true) {
            @Override
            public boolean requiereDiagnostico() {
                return true;
            }

            @Override
            public boolean permiteTratamientosMultiples() {
                return true;
            }
        },

        DESPARASITACION("Desparasitación", "Tratamiento antiparasitario", false) {
            @Override
            public boolean requiereDiagnostico() {
                return false;
            }

            @Override
            public boolean permiteTratamientosMultiples() {
                return false;
            }
        },

        URGENCIA("Urgencia", "Atención de emergencia", true) {
            @Override
            public boolean requiereDiagnostico() {
                return true;
            }

            @Override
            public boolean permiteTratamientosMultiples() {
                return true;
            }
        },

        CONTROL("Control", "Control de seguimiento", false) {
            @Override
            public boolean requiereDiagnostico() {
                return false;
            }

            @Override
            public boolean permiteTratamientosMultiples() {
                return true;
            }
        },

        ESTERILIZACION("Esterilización", "Procedimiento de esterilización", true) {
            @Override
            public boolean requiereDiagnostico() {
                return true;
            }

            @Override
            public boolean permiteTratamientosMultiples() {
                return true;
            }
        };

        private final String nombre;
        private final String descripcion;
        private final boolean esProcedimiento;

        TipoEventoMedico(String nombre, String descripcion, boolean esProcedimiento) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.esProcedimiento = esProcedimiento;
        }

        public abstract boolean requiereDiagnostico();
        public abstract boolean permiteTratamientosMultiples();

        public String getNombre() { return nombre; }
        public String getDescripcion() { return descripcion; }
        public boolean esProcedimiento() { return esProcedimiento; }

        public static TipoEventoMedico porNombre(String nombre) {
            for (TipoEventoMedico tipo : values()) {
                if (tipo.nombre.equalsIgnoreCase(nombre)) {
                    return tipo;
                }
            }
            throw new IllegalArgumentException("Tipo de evento médico no válido: " + nombre);
        }
    }

    /**
     * Clase para representar tratamientos médicos (Composite Pattern).
     */
    public static class Tratamiento {
        private Integer id;
        private String medicamento;
        private String dosis;
        private String frecuencia;
        private Integer duracionDias;
        private String viaAdministracion;
        private String indicaciones;
        private boolean completado;
        private LocalDate fechaInicio;
        private LocalDate fechaFin;
        private String observaciones;

        // Builder Pattern para Tratamiento
        public static class Builder {
            private final Tratamiento tratamiento;

            public Builder(String medicamento, String dosis) {
                tratamiento = new Tratamiento();
                tratamiento.medicamento = Objects.requireNonNull(medicamento);
                tratamiento.dosis = Objects.requireNonNull(dosis);
                tratamiento.frecuencia = "Cada 24 horas";
                tratamiento.duracionDias = 7;
                tratamiento.viaAdministracion = "Oral";
                tratamiento.completado = false;
                tratamiento.fechaInicio = LocalDate.now();
            }

            public Builder withFrecuencia(String frecuencia) {
                tratamiento.frecuencia = frecuencia;
                return this;
            }

            public Builder withDuracionDias(Integer duracionDias) {
                tratamiento.duracionDias = duracionDias;
                return this;
            }

            public Builder withViaAdministracion(String viaAdministracion) {
                tratamiento.viaAdministracion = viaAdministracion;
                return this;
            }

            public Builder withIndicaciones(String indicaciones) {
                tratamiento.indicaciones = indicaciones;
                return this;
            }

            public Builder withFechaInicio(LocalDate fechaInicio) {
                tratamiento.fechaInicio = fechaInicio;
                return this;
            }

            public Tratamiento build() {
                // Validaciones
                if (tratamiento.medicamento.trim().isEmpty()) {
                    throw new IllegalStateException("Medicamento es requerido");
                }
                if (tratamiento.dosis.trim().isEmpty()) {
                    throw new IllegalStateException("Dosis es requerida");
                }
                if (tratamiento.duracionDias <= 0) {
                    throw new IllegalStateException("Duración debe ser positiva");
                }

                // Calcular fecha fin
                if (tratamiento.fechaInicio != null) {
                    tratamiento.fechaFin = tratamiento.fechaInicio.plusDays(tratamiento.duracionDias);
                }

                return tratamiento;
            }
        }

        // Métodos de negocio
        public boolean estaVigente() {
            if (completado) return false;
            if (fechaInicio == null) return false;
            LocalDate hoy = LocalDate.now();
            return !hoy.isBefore(fechaInicio) && (fechaFin == null || !hoy.isAfter(fechaFin));
        }

        public boolean estaVencido() {
            return fechaFin != null && LocalDate.now().isAfter(fechaFin) && !completado;
        }

        public long getDiasRestantes() {
            if (fechaFin == null) return 0;
            return LocalDate.now().until(fechaFin).getDays();
        }

        public void marcarComoCompletado() {
            this.completado = true;
        }

        // Getters y setters
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public String getMedicamento() { return medicamento; }
        public void setMedicamento(String medicamento) { this.medicamento = medicamento; }

        public String getDosis() { return dosis; }
        public void setDosis(String dosis) { this.dosis = dosis; }

        public String getFrecuencia() { return frecuencia; }
        public void setFrecuencia(String frecuencia) { this.frecuencia = frecuencia; }

        public Integer getDuracionDias() { return duracionDias; }
        public void setDuracionDias(Integer duracionDias) {
            this.duracionDias = duracionDias;
            if (fechaInicio != null) {
                this.fechaFin = fechaInicio.plusDays(duracionDias);
            }
        }

        public String getViaAdministracion() { return viaAdministracion; }
        public void setViaAdministracion(String viaAdministracion) { this.viaAdministracion = viaAdministracion; }

        public String getIndicaciones() { return indicaciones; }
        public void setIndicaciones(String indicaciones) { this.indicaciones = indicaciones; }

        public boolean isCompletado() { return completado; }
        public void setCompletado(boolean completado) { this.completado = completado; }

        public LocalDate getFechaInicio() { return fechaInicio; }
        public void setFechaInicio(LocalDate fechaInicio) {
            this.fechaInicio = fechaInicio;
            if (duracionDias != null) {
                this.fechaFin = fechaInicio.plusDays(duracionDias);
            }
        }

        public LocalDate getFechaFin() { return fechaFin; }
        public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

        public String getObservaciones() { return observaciones; }
        public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

        @Override
        public String toString() {
            return String.format("Tratamiento: %s - %s - %s", medicamento, dosis, frecuencia);
        }
    }

    public static class EventoMedico {
        private Integer id;
        private TipoEventoMedico tipo;
        private String nombre;
        private String descripcion;
        private boolean requiereDiagnostico;
        private boolean activo;

        public EventoMedico(TipoEventoMedico tipo, String nombre) {
            this.tipo = Objects.requireNonNull(tipo);
            this.nombre = Objects.requireNonNull(nombre);
            this.requiereDiagnostico = tipo.requiereDiagnostico();
            this.activo = true;
        }

        // Getters y setters
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public TipoEventoMedico getTipo() { return tipo; }
        public void setTipo(TipoEventoMedico tipo) { this.tipo = tipo; }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

        public boolean isRequiereDiagnostico() { return requiereDiagnostico; }
        public boolean isActivo() { return activo; }
        public void setActivo(boolean activo) { this.activo = activo; }
    }

    // Constructor privado para Builder
    private HistorialMedico() {
        this.tratamientos = new ArrayList<>();
        this.fechaEvento = LocalDateTime.now();
        this.fechaRegistro = LocalDateTime.now();
        this.requiereSeguimiento = false;
    }

    public static class Builder {
        private final HistorialMedico historial;

        public Builder() {
            this.historial = new HistorialMedico();
        }

        public Builder withMascotaId(Integer mascotaId) {
            historial.mascotaId = Objects.requireNonNull(mascotaId);
            return this;
        }

        public Builder withMascota(Mascota mascota) {
            historial.mascota = Objects.requireNonNull(mascota);
            historial.mascotaId = mascota.getId();
            return this;
        }

        public Builder withCitaId(Long citaId) {
            historial.citaId = citaId;
            return this;
        }

        public Builder withCita(Cita cita) {
            historial.cita = cita;
            if (cita != null) {
                historial.citaId = cita.getId();
            }
            return this;
        }

        public Builder withVeterinarioId(Integer veterinarioId) {
            historial.veterinarioId = Objects.requireNonNull(veterinarioId);
            return this;
        }

        public Builder withVeterinario(Veterinario veterinario) {
            historial.veterinario = Objects.requireNonNull(veterinario);
            historial.veterinarioId = veterinario.getId();
            return this;
        }

        public Builder withEventoMedico(TipoEventoMedico tipoEvento, String nombreEvento) {
            historial.eventoMedico = new EventoMedico(tipoEvento, nombreEvento);
            return this;
        }

        public Builder withFechaEvento(LocalDateTime fechaEvento) {
            historial.fechaEvento = fechaEvento != null ? fechaEvento : LocalDateTime.now();
            return this;
        }

        public Builder withSignosVitales(BigDecimal temperatura, Integer frecuenciaCardiaca,
                                         Integer frecuenciaRespiratoria, BigDecimal peso) {
            historial.temperatura = temperatura;
            historial.frecuenciaCardiaca = frecuenciaCardiaca;
            historial.frecuenciaRespiratoria = frecuenciaRespiratoria;
            historial.peso = peso;
            return this;
        }

        public Builder withSintomas(String sintomas) {
            historial.sintomas = sintomas;
            return this;
        }

        public Builder withDiagnostico(String diagnostico) {
            historial.diagnostico = diagnostico;
            return this;
        }

        public Builder withTratamientoPrescrito(String tratamiento) {
            historial.tratamientoPrescrito = tratamiento;
            return this;
        }

        public HistorialMedico build() {
            // Validaciones finales
            if (historial.mascotaId == null) {
                throw new IllegalStateException("Mascota es requerida");
            }
            if (historial.veterinarioId == null) {
                throw new IllegalStateException("Veterinario es requerido");
            }
            if (historial.eventoMedico == null) {
                throw new IllegalStateException("Evento médico es requerido");
            }

            // Validar diagnóstico si es requerido
            if (historial.eventoMedico.isRequiereDiagnostico() &&
                    (historial.diagnostico == null || historial.diagnostico.trim().isEmpty())) {
                throw new IllegalStateException("Diagnóstico es requerido para " +
                        historial.eventoMedico.getTipo().getNombre());
            }

            return historial;
        }
    }

    // ============ MÉTODOS DE NEGOCIO PRINCIPALES ============


    public void agregarTratamiento(Tratamiento tratamiento) {
        Objects.requireNonNull(tratamiento, "Tratamiento no puede ser nulo");

        // Validar si el tipo de evento permite múltiples tratamientos
        if (eventoMedico != null && !eventoMedico.getTipo().permiteTratamientosMultiples()
                && !tratamientos.isEmpty()) {
            throw new IllegalStateException(
                    "El evento médico " + eventoMedico.getTipo().getNombre() +
                            " no permite múltiples tratamientos");
        }

        tratamientos.add(tratamiento);
    }

    public void agregarTratamiento(String medicamento, String dosis, String frecuencia,
                                   Integer duracionDias) {
        Tratamiento tratamiento = new Tratamiento.Builder(medicamento, dosis)
                .withFrecuencia(frecuencia)
                .withDuracionDias(duracionDias)
                .build();
        agregarTratamiento(tratamiento);
    }


    public boolean completarTratamiento(int indice) {
        if (indice >= 0 && indice < tratamientos.size()) {
            tratamientos.get(indice).marcarComoCompletado();
            return true;
        }
        return false;
    }

    public EstadoSignosVitales verificarSignosVitales() {
        List<String> alertas = new ArrayList<>();

        if (temperatura != null) {
            if (temperatura.compareTo(TEMPERATURA_NORMAL_MIN) < 0) {
                alertas.add("Temperatura baja: " + temperatura + "°C");
            } else if (temperatura.compareTo(TEMPERATURA_NORMAL_MAX) > 0) {
                alertas.add("Fiebre: " + temperatura + "°C");
            }
        }

        if (frecuenciaCardiaca != null) {
            if (frecuenciaCardiaca < FC_NORMAL_MIN) {
                alertas.add("Bradicardia: " + frecuenciaCardiaca + " lpm");
            } else if (frecuenciaCardiaca > FC_NORMAL_MAX) {
                alertas.add("Taquicardia: " + frecuenciaCardiaca + " lpm");
            }
        }

        if (frecuenciaRespiratoria != null) {
            if (frecuenciaRespiratoria < FR_NORMAL_MIN) {
                alertas.add("Bradipnea: " + frecuenciaRespiratoria + " rpm");
            } else if (frecuenciaRespiratoria > FR_NORMAL_MAX) {
                alertas.add("Taquipnea: " + frecuenciaRespiratoria + " rpm");
            }
        }

        return new EstadoSignosVitales(alertas.isEmpty(), alertas);
    }


    public boolean tieneTratamientosPendientes() {
        return tratamientos.stream().anyMatch(t -> !t.isCompletado() && t.estaVigente());
    }


    public List<Tratamiento> getTratamientosVigentes() {
        return tratamientos.stream()
                .filter(Tratamiento::estaVigente)
                .collect(Collectors.toList());
    }


    public boolean requiereControlProximo() {
        return requiereSeguimiento && fechaProximoControl != null &&
                fechaProximoControl.isBefore(LocalDate.now().plusWeeks(2));
    }

    /**
     * Calcula la edad de la mascota al momento del evento.
     */
    public String calcularEdadMascotaEnEvento() {
        if (mascota == null || mascota.getFechaNacimiento() == null) {
            return "Edad no disponible";
        }

        long meses = mascota.getFechaNacimiento().until(fechaEvento.toLocalDate()).toTotalMonths();
        if (meses < 12) {
            return meses + " meses";
        } else {
            long años = meses / 12;
            long mesesRestantes = meses % 12;
            return años + " años" + (mesesRestantes > 0 ? " y " + mesesRestantes + " meses" : "");
        }
    }

    // ============ CLASES AUXILIARES ============


    public static class EstadoSignosVitales {
        private final boolean normales;
        private final List<String> alertas;

        public EstadoSignosVitales(boolean normales, List<String> alertas) {
            this.normales = normales;
            this.alertas = List.copyOf(alertas);
        }

        public boolean sonNormales() { return normales; }
        public List<String> getAlertas() { return alertas; }
        public boolean tieneAlertas() { return !alertas.isEmpty(); }
    }

    // ============ GETTERS Y SETTERS ============

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getMascotaId() { return mascotaId; }
    public void setMascotaId(Integer mascotaId) { this.mascotaId = mascotaId; }

    public Long getCitaId() { return citaId; }
    public void setCitaId(Long citaId) { this.citaId = citaId; }

    public Integer getEventoMedicoId() { return eventoMedicoId; }
    public void setEventoMedicoId(Integer eventoMedicoId) { this.eventoMedicoId = eventoMedicoId; }

    public LocalDateTime getFechaEvento() { return fechaEvento; }
    public void setFechaEvento(LocalDateTime fechaEvento) { this.fechaEvento = fechaEvento; }

    public Integer getVeterinarioId() { return veterinarioId; }
    public void setVeterinarioId(Integer veterinarioId) { this.veterinarioId = veterinarioId; }

    public BigDecimal getTemperatura() { return temperatura; }
    public void setTemperatura(BigDecimal temperatura) { this.temperatura = temperatura; }

    public Integer getFrecuenciaCardiaca() { return frecuenciaCardiaca; }
    public void setFrecuenciaCardiaca(Integer frecuenciaCardiaca) { this.frecuenciaCardiaca = frecuenciaCardiaca; }

    public Integer getFrecuenciaRespiratoria() { return frecuenciaRespiratoria; }
    public void setFrecuenciaRespiratoria(Integer frecuenciaRespiratoria) { this.frecuenciaRespiratoria = frecuenciaRespiratoria; }

    public BigDecimal getPeso() { return peso; }
    public void setPeso(BigDecimal peso) { this.peso = peso; }

    public String getSintomas() { return sintomas; }
    public void setSintomas(String sintomas) { this.sintomas = sintomas; }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }

    public String getTratamientoPrescrito() { return tratamientoPrescrito; }
    public void setTratamientoPrescrito(String tratamientoPrescrito) { this.tratamientoPrescrito = tratamientoPrescrito; }

    public String getMedicamentosRecetados() { return medicamentosRecetados; }
    public void setMedicamentosRecetados(String medicamentosRecetados) { this.medicamentosRecetados = medicamentosRecetados; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getRecomendaciones() { return recomendaciones; }
    public void setRecomendaciones(String recomendaciones) { this.recomendaciones = recomendaciones; }

    public LocalDate getFechaProximoControl() { return fechaProximoControl; }
    public void setFechaProximoControl(LocalDate fechaProximoControl) {
        this.fechaProximoControl = fechaProximoControl;
        this.requiereSeguimiento = fechaProximoControl != null;
    }

    public Boolean getRequiereSeguimiento() { return requiereSeguimiento; }
    public void setRequiereSeguimiento(Boolean requiereSeguimiento) {
        this.requiereSeguimiento = requiereSeguimiento;
        if (!requiereSeguimiento) {
            this.fechaProximoControl = null;
        }
    }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Mascota getMascota() { return mascota; }
    public void setMascota(Mascota mascota) {
        this.mascota = mascota;
        if (mascota != null) {
            this.mascotaId = mascota.getId();
        }
    }

    public Cita getCita() { return cita; }
    public void setCita(Cita cita) {
        this.cita = cita;
        if (cita != null) {
            this.citaId = cita.getId();
        }
    }

    public Veterinario getVeterinario() { return veterinario; }
    public void setVeterinario(Veterinario veterinario) {
        this.veterinario = veterinario;
        if (veterinario != null) {
            this.veterinarioId = veterinario.getId();
        }
    }

    public EventoMedico getEventoMedico() { return eventoMedico; }
    public void setEventoMedico(EventoMedico eventoMedico) { this.eventoMedico = eventoMedico; }

    public List<Tratamiento> getTratamientos() { return List.copyOf(tratamientos); }

    // ============ MÉTODOS SOBRESCRITOS ============

    @Override
    public String toString() {
        return String.format("""
            HistorialMedico{
                id=%d, mascota=%s, evento=%s, 
                fecha=%s, veterinario=%s
            }""",
                id,
                mascota != null ? mascota.getNombre() : "MascotaID:" + mascotaId,
                eventoMedico != null ? eventoMedico.getTipo().getNombre() : "N/A",
                fechaEvento.toLocalDate(),
                veterinario != null ? veterinario.getNombreCompleto() : "VetID:" + veterinarioId
        );
    }

    public String generarReporteMedico() {
        StringBuilder sb = new StringBuilder();
        sb.append("=").append("=".repeat(60)).append("\n");
        sb.append("REPORTE MÉDICO - ").append(fechaEvento.toLocalDate()).append("\n");
        sb.append("=").append("=".repeat(60)).append("\n");

        // Información básica
        sb.append("Mascota: ").append(mascota != null ? mascota.getNombre() : "N/A").append("\n");
        sb.append("Edad en el evento: ").append(calcularEdadMascotaEnEvento()).append("\n");
        sb.append("Veterinario: ").append(veterinario != null ? veterinario.getNombreCompleto() : "N/A").append("\n");
        sb.append("Tipo de evento: ").append(eventoMedico != null ? eventoMedico.getTipo().getNombre() : "N/A").append("\n");
        sb.append("\n");

        // Signos vitales
        sb.append("SIGNOS VITALES:\n");
        sb.append("- Temperatura: ").append(temperatura != null ? temperatura + "°C" : "No registrada").append("\n");
        sb.append("- Frecuencia cardíaca: ").append(frecuenciaCardiaca != null ? frecuenciaCardiaca + " lpm" : "No registrada").append("\n");
        sb.append("- Frecuencia respiratoria: ").append(frecuenciaRespiratoria != null ? frecuenciaRespiratoria + " rpm" : "No registrada").append("\n");
        sb.append("- Peso: ").append(peso != null ? peso + " kg" : "No registrado").append("\n");
        sb.append("\n");

        // Diagnóstico y tratamiento
        if (diagnostico != null) {
            sb.append("DIAGNÓSTICO:\n").append(diagnostico).append("\n\n");
        }

        if (tratamientoPrescrito != null) {
            sb.append("TRATAMIENTO PRESCRITO:\n").append(tratamientoPrescrito).append("\n\n");
        }

        // Tratamientos detallados
        if (!tratamientos.isEmpty()) {
            sb.append("TRATAMIENTOS DETALLADOS:\n");
            for (int i = 0; i < tratamientos.size(); i++) {
                Tratamiento t = tratamientos.get(i);
                sb.append(String.format("%d. %s - %s - %s\n", i + 1, t.getMedicamento(), t.getDosis(), t.getFrecuencia()));
            }
            sb.append("\n");
        }

        // Recomendaciones
        if (recomendaciones != null) {
            sb.append("RECOMENDACIONES:\n").append(recomendaciones).append("\n");
        }

        if (fechaProximoControl != null) {
            sb.append("\nPRÓXIMO CONTROL: ").append(fechaProximoControl).append("\n");
        }

        sb.append("=").append("=".repeat(60)).append("\n");
        return sb.toString();
    }

    // ============ MÉTODOS ESTÁTICOS Y FACTORY METHODS ============

    public static Builder builder() {
        return new Builder();
    }

    // Factory para mapeo de persistencia (salta validaciones del Builder)
    public static HistorialMedico newForPersistence() {
        return new HistorialMedico();
    }


    public static HistorialMedico crearConsultaGeneral(Mascota mascota, Veterinario veterinario,
                                                       String sintomas, String diagnostico) {
        return new Builder()
                .withMascota(mascota)
                .withVeterinario(veterinario)
                .withEventoMedico(TipoEventoMedico.CONSULTA, "Consulta General")
                .withSintomas(sintomas)
                .withDiagnostico(diagnostico)
                .build();
    }


    public static HistorialMedico crearVacunacion(Mascota mascota, Veterinario veterinario,
                                                  String vacuna, BigDecimal peso) {
        return new Builder()
                .withMascota(mascota)
                .withVeterinario(veterinario)
                .withEventoMedico(TipoEventoMedico.VACUNACION, "Vacunación: " + vacuna)
                .withSignosVitales(null, null, null, peso)
                .build();
    }
}
