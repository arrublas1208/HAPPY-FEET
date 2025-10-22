package com.happyfeet.model.entities.enums;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public enum Urgencia {

    BAJA(1, "Baja", "🟢", "Verde", "Consulta programada sin complicaciones") {
        // Métodos abstractos implementados - SIN @Override
        public int getTiempoMaximoEsperaMinutos() {
            return 120;
        }

        public boolean requiereRecursosEspeciales() {
            return false;
        }

        public boolean puedeSerProgramada() {
            return true;
        }
    },

    MEDIA(2, "Media", "🟡", "Amarillo", "Situación que requiere atención prioritaria") {
        public int getTiempoMaximoEsperaMinutos() {
            return 45;
        }

        public boolean requiereRecursosEspeciales() {
            return false;
        }

        public boolean puedeSerProgramada() {
            return true;
        }
    },

    ALTA(3, "Alta", "🔴", "Rojo", "Emergencia que requiere atención inmediata") {
        public int getTiempoMaximoEsperaMinutos() {
            return 15;
        }

        public boolean requiereRecursosEspeciales() {
            return true;
        }

        public boolean puedeSerProgramada() {
            return false;
        }
    },

    CRITICA(4, "Crítica", "⚫", "Rojo Intenso", "Emergencia crítica de vida o muerte") {
        public int getTiempoMaximoEsperaMinutos() {
            return 5;
        }

        public boolean requiereRecursosEspeciales() {
            return true;
        }

        public boolean puedeSerProgramada() {
            return false;
        }
    };

    private final int nivel;
    private final String nombre;
    private final String emoji;
    private final String color;
    private final String descripcion;

    Urgencia(int nivel, String nombre, String emoji, String color, String descripcion) {
        this.nivel = nivel;
        this.nombre = nombre;
        this.emoji = emoji;
        this.color = color;
        this.descripcion = descripcion;
    }

    public abstract int getTiempoMaximoEsperaMinutos();
    public abstract boolean requiereRecursosEspeciales();
    public abstract boolean puedeSerProgramada();


    public boolean esMasUrgenteQue(Urgencia otra) {
        return this.nivel > otra.nivel;
    }

    public boolean esMenosUrgenteQue(Urgencia otra) {
        return this.nivel < otra.nivel;
    }

    public boolean esAlMenosTanUrgenteComo(Urgencia otra) {
        return this.nivel >= otra.nivel;
    }

    public boolean esCritica() {
        return this == ALTA || this == CRITICA;
    }

    public boolean requiereAtencionInmediata() {
        return this.getTiempoMaximoEsperaMinutos() <= 30;
    }

    public Urgencia escalar() {
        return porNivel(this.nivel + 1).orElse(this);
    }

    public Urgencia desescalar() {
        return porNivel(this.nivel - 1).orElse(this);
    }

    public int diferenciaDeNivelesCon(Urgencia otra) {
        return this.nivel - otra.nivel;
    }


    public static Optional<Urgencia> porNivel(int nivel) {
        return Arrays.stream(values())
                .filter(urgencia -> urgencia.nivel == nivel)
                .findFirst();
    }

    public static Optional<Urgencia> porNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return Optional.empty();
        }

        return Arrays.stream(values())
                .filter(urgencia -> urgencia.nombre.equalsIgnoreCase(nombre.trim()))
                .findFirst();
    }

    public static List<Urgencia> getUrgenciasOrdenadas() {
        return Arrays.stream(values())
                .sorted(Comparator.comparingInt(Urgencia::getNivel))
                .collect(Collectors.toList());
    }

    public static List<Urgencia> getUrgenciasProgramables() {
        return Arrays.stream(values())
                .filter(Urgencia::puedeSerProgramada)
                .collect(Collectors.toList());
    }

    public static List<Urgencia> getUrgenciasCriticas() {
        return Arrays.stream(values())
                .filter(Urgencia::esCritica)
                .collect(Collectors.toList());
    }

    public static Urgencia getUrgenciaPorDefecto() {
        return BAJA;
    }

    public static Urgencia calcularUrgenciaDesdeSintomas(String sintomas) {
        if (sintomas == null || sintomas.trim().isEmpty()) {
            return BAJA;
        }

        String sintomasLower = sintomas.toLowerCase();

        if (sintomasLower.matches(".*\\b(sangrado|convulsión|inconsciente|paro|asfixia)\\b.*")) {
            return CRITICA;
        } else if (sintomasLower.matches(".*\\b(dolor fuerte|fractura|vómito|diarrea|fiebre alta)\\b.*")) {
            return ALTA;
        } else if (sintomasLower.matches(".*\\b(tos|estornudo|cojera|pérdida apetito)\\b.*")) {
            return MEDIA;
        } else {
            return BAJA;
        }
    }


    public int getNivel() { return nivel; }
    public String getNombre() { return nombre; }
    public String getEmoji() { return emoji; }
    public String getColor() { return color; }
    public String getDescripcion() { return descripcion; }

    public String getDisplayName() {
        return String.format("%s %s (Nivel %d)", emoji, nombre, nivel);
    }

    public String getInfoCompleta() {
        return String.format("%s - %s. Tiempo máximo de espera: %d minutos. %s",
                getDisplayName(), descripcion, getTiempoMaximoEsperaMinutos(),
                requiereRecursosEspeciales() ? "Requiere recursos especiales." : "");
    }

    @Override
    public String toString() {
        return getInfoCompleta();
    }
}