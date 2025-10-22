package com.happyfeet.model.entities.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Enumeración que representa los estados de una cita médica.
 * Implementa State Pattern para gestión avanzada de transiciones de estado.
 */
public enum CitaEstado {

    PROGRAMADA(1, "Programada", "🟡") {
        // Métodos abstractos implementados - SIN @Override
        public boolean puedeTransicionarA(CitaEstado nuevoEstado) {
            return nuevoEstado == CONFIRMADA ||
                    nuevoEstado == CANCELADA ||
                    nuevoEstado == NO_ASISTIO;
        }

        public String getDescripcionCompleta() {
            return "Cita programada pendiente de confirmación por el cliente";
        }

        public boolean esModificable() {
            return true;
        }

        public boolean esFinal() {
            return false;
        }

        public List<CitaEstado> getSiguientesEstadosValidos() {
            return Arrays.asList(CONFIRMADA, CANCELADA, NO_ASISTIO);
        }
    },

    CONFIRMADA(2, "Confirmada", "🟢") {
        public boolean puedeTransicionarA(CitaEstado nuevoEstado) {
            return nuevoEstado == EN_CURSO ||
                    nuevoEstado == CANCELADA;
        }

        public String getDescripcionCompleta() {
            return "Cita confirmada por el cliente, lista para ser atendida";
        }

        public boolean esModificable() {
            return true;
        }

        public boolean esFinal() {
            return false;
        }

        public List<CitaEstado> getSiguientesEstadosValidos() {
            return Arrays.asList(EN_CURSO, CANCELADA);
        }
    },

    EN_CURSO(3, "En Curso", "🔵") {
        public boolean puedeTransicionarA(CitaEstado nuevoEstado) {
            return nuevoEstado == FINALIZADA ||
                    nuevoEstado == CANCELADA;
        }

        public String getDescripcionCompleta() {
            return "Cita en progreso, el veterinario está atendiendo a la mascota";
        }

        public boolean esModificable() {
            return false;
        }

        public boolean esFinal() {
            return false;
        }

        public List<CitaEstado> getSiguientesEstadosValidos() {
            return Arrays.asList(FINALIZADA, CANCELADA);
        }
    },

    FINALIZADA(4, "Finalizada", "✅") {
        public boolean puedeTransicionarA(CitaEstado nuevoEstado) {
            return false;
        }

        public String getDescripcionCompleta() {
            return "Cita completada exitosamente, se generó historial médico";
        }

        public boolean esModificable() {
            return false;
        }

        public boolean esFinal() {
            return true;
        }

        public List<CitaEstado> getSiguientesEstadosValidos() {
            return List.of();
        }
    },

    CANCELADA(5, "Cancelada", "❌") {
        public boolean puedeTransicionarA(CitaEstado nuevoEstado) {
            return false;
        }

        public String getDescripcionCompleta() {
            return "Cita cancelada por el cliente o por la veterinaria";
        }

        public boolean esModificable() {
            return false;
        }

        public boolean esFinal() {
            return true;
        }

        public List<CitaEstado> getSiguientesEstadosValidos() {
            return List.of();
        }
    },

    NO_ASISTIO(6, "No Asistió", "⏰") {
        public boolean puedeTransicionarA(CitaEstado nuevoEstado) {
            return false;
        }

        public String getDescripcionCompleta() {
            return "Cliente no se presentó a la cita programada";
        }

        public boolean esModificable() {
            return false;
        }

        public boolean esFinal() {
            return true;
        }

        public List<CitaEstado> getSiguientesEstadosValidos() {
            return List.of();
        }
    },

    REPROGRAMADA(7, "Reprogramada", "🔄") {
        public boolean puedeTransicionarA(CitaEstado nuevoEstado) {
            return nuevoEstado == PROGRAMADA ||
                    nuevoEstado == CANCELADA;
        }

        public String getDescripcionCompleta() {
            return "Cita reprogramada por solicitud del cliente o de la veterinaria";
        }

        public boolean esModificable() {
            return true;
        }

        public boolean esFinal() {
            return false;
        }

        public List<CitaEstado> getSiguientesEstadosValidos() {
            return Arrays.asList(PROGRAMADA, CANCELADA);
        }
    };

    private final int id;
    private final String nombre;
    private final String emoji;

    CitaEstado(int id, String nombre, String emoji) {
        this.id = id;
        this.nombre = nombre;
        this.emoji = emoji;
    }

    // ============ MÉTODOS ABSTRACTOS ============


    public abstract boolean puedeTransicionarA(CitaEstado nuevoEstado);


    public abstract String getDescripcionCompleta();


    public abstract boolean esModificable();


    public abstract boolean esFinal();


    public abstract List<CitaEstado> getSiguientesEstadosValidos();


    public Optional<String> validarTransicion(CitaEstado nuevoEstado) {
        if (nuevoEstado == null) {
            return Optional.of("El nuevo estado no puede ser nulo");
        }

        if (this == nuevoEstado) {
            return Optional.of("La cita ya se encuentra en el estado: " + this.nombre);
        }

        if (!this.puedeTransicionarA(nuevoEstado)) {
            return Optional.of(String.format(
                    "Transición inválida: %s → %s. Transiciones permitidas: %s",
                    this.nombre, nuevoEstado.nombre,
                    this.getSiguientesEstadosValidos().stream()
                            .map(estado -> estado.nombre)
                            .collect(Collectors.joining(", "))
            ));
        }

        return Optional.empty();
    }

    public boolean estaActiva() {
        return !this.esFinal() || this == EN_CURSO;
    }

    public boolean requiereAtencionInmediata() {
        return this == EN_CURSO || this == CONFIRMADA;
    }

    public boolean puedeSerCancelada() {
        return this == PROGRAMADA || this == CONFIRMADA || this == REPROGRAMADA;
    }



    public static Optional<CitaEstado> porId(int id) {
        return Arrays.stream(values())
                .filter(estado -> estado.id == id)
                .findFirst();
    }

    public static Optional<CitaEstado> porNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return Optional.empty();
        }

        return Arrays.stream(values())
                .filter(estado -> estado.nombre.equalsIgnoreCase(nombre.trim()))
                .findFirst();
    }

    public static List<CitaEstado> getEstadosActivos() {
        return Arrays.stream(values())
                .filter(estado -> !estado.esFinal())
                .collect(Collectors.toList());
    }

    public static List<CitaEstado> getEstadosFinales() {
        return Arrays.stream(values())
                .filter(CitaEstado::esFinal)
                .collect(Collectors.toList());
    }

    public static List<CitaEstado> getEstadosModificables() {
        return Arrays.stream(values())
                .filter(CitaEstado::esModificable)
                .collect(Collectors.toList());
    }

    public static CitaEstado getEstadoInicial() {
        return PROGRAMADA;
    }


    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmoji() { return emoji; }
    public String getDisplayName() { return emoji + " " + nombre; }

    @Override
    public String toString() {
        return String.format("%s (ID: %d) - %s", nombre, id, getDescripcionCompleta());
    }
}
