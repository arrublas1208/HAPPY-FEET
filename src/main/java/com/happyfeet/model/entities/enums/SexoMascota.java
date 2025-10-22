package com.happyfeet.model.entities.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public enum SexoMascota {

    MACHO("M", "Macho", "Masculino") {
        @Override
        public String getPronombre() {
            return "él";
        }

        @Override
        public String getPosesivo() {
            return "su";
        }

        @Override
        public boolean esCompatibleConEspecie(TipoEspecie especie) {
            // Todos los sexos son compatibles con todas las especies
            return true;
        }

        @Override
        public String getTerminologiaEspecifica(TipoEspecie especie) {
            switch (especie) {
                case CANINO: return "Macho";
                case FELINO: return "Macho";
                case AVE: return "Macho";
                case REPTIL: return "Macho";
                case ROEDOR: return "Macho";
                case EQUINO: return "Padrillo";
                case BOVINO: return "Toro";
                case PORCINO: return "Verraco";
                case OVINO: return "Carnero";
                case CAPRINO: return "Macho cabrío";
                default: return "Macho";
            }
        }
    },

    HEMBRA("H", "Hembra", "Femenino") {
        @Override
        public String getPronombre() {
            return "ella";
        }

        @Override
        public String getPosesivo() {
            return "su";
        }

        @Override
        public boolean esCompatibleConEspecie(TipoEspecie especie) {
            return true;
        }

        @Override
        public String getTerminologiaEspecifica(TipoEspecie especie) {
            switch (especie) {
                case CANINO: return "Hembra";
                case FELINO: return "Hembra";
                case AVE: return "Hembra";
                case REPTIL: return "Hembra";
                case ROEDOR: return "Hembra";
                case EQUINO: return "Yegua";
                case BOVINO: return "Vaca";
                case PORCINO: return "Cerda";
                case OVINO: return "Oveja";
                case CAPRINO: return "Cabra";
                default: return "Hembra";
            }
        }
    },
    INDETERMINADO("I", "Indeterminado", "Desconocido") {
        @Override
        public String getPronombre() {
            return "elle"; // Pronombre neutro
        }

        @Override
        public String getPosesivo() {
            return "su";
        }

        @Override
        public boolean esCompatibleConEspecie(TipoEspecie especie) {
            return true;
        }

        @Override
        public String getTerminologiaEspecifica(TipoEspecie especie) {
            return "Indeterminado";
        }
    },

    NO_ESPECIFICADO("N", "No Especificado", "No registrado") {
        @Override
        public String getPronombre() {
            return "el/la";
        }

        @Override
        public String getPosesivo() {
            return "su";
        }

        @Override
        public boolean esCompatibleConEspecie(TipoEspecie especie) {
            return true;
        }

        @Override
        public String getTerminologiaEspecifica(TipoEspecie especie) {
            return "No especificado";
        }
    };

    // ============ ATRIBUTOS Y CONSTRUCTOR ============

    private final String codigo;
    private final String nombre;
    private final String descripcion;

    SexoMascota(String codigo, String nombre, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // ============ MÉTODOS ABSTRACTOS ============

    public abstract String getPronombre();

    public abstract String getPosesivo();

    public abstract boolean esCompatibleConEspecie(TipoEspecie especie);

    public abstract String getTerminologiaEspecifica(TipoEspecie especie);

    // ============ MÉTODOS CONCRETOS COMUNES ============

    public boolean esDefinido() {
        return this == MACHO || this == HEMBRA;
    }

    public boolean esMacho() {
        return this == MACHO;
    }

    public boolean esHembra() {
        return this == HEMBRA;
    }

    public boolean esDesconocido() {
        return this == INDETERMINADO || this == NO_ESPECIFICADO;
    }


    public String getArticulo() {
        switch (this) {
            case MACHO: return "el";
            case HEMBRA: return "la";
            default: return "el/la";
        }
    }


    public String getTitulo() {
        switch (this) {
            case MACHO: return "Sr.";
            case HEMBRA: return "Sra.";
            default: return "";
        }
    }


    public String getDescripcionCompleta(TipoEspecie especie) {
        if (especie != null) {
            return String.format("%s (%s)", nombre, getTerminologiaEspecifica(especie));
        }
        return String.format("%s - %s", nombre, descripcion);
    }


    public boolean puedeReproducirseCon(SexoMascota otroSexo) {
        if (this == MACHO && otroSexo == HEMBRA) return true;
        if (this == HEMBRA && otroSexo == MACHO) return true;
        return false;
    }

    // ============ MÉTODOS ESTÁTICOS DE UTILIDAD ============


    public static Optional<SexoMascota> porCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return Optional.empty();
        }

        return Arrays.stream(values())
                .filter(sexo -> sexo.codigo.equalsIgnoreCase(codigo.trim()))
                .findFirst();
    }


    public static Optional<SexoMascota> porNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return Optional.empty();
        }

        String nombreNormalizado = nombre.trim().toUpperCase();
        return Arrays.stream(values())
                .filter(sexo -> sexo.nombre.toUpperCase().equals(nombreNormalizado))
                .findFirst();
    }


    public static SexoMascota fromString(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return NO_ESPECIFICADO;
        }

        String valorNormalizado = valor.trim().toUpperCase();

        switch (valorNormalizado) {
            case "M": case "MACHO": case "MALE": case "MASculino":
            case "HOMBRE": case "BOY": case "VARÓN":
                return MACHO;

            case "H": case "HEMBRA": case "FEMALE": case "FEMENINO":
            case "MUJER": case "GIRL": case "HEMBR":
                return HEMBRA;

            case "I": case "INDETERMINADO": case "UNKNOWN": case "DESCONOCIDO":
            case "HERMAFRODITA": case "INTERSEX":
                return INDETERMINADO;

            case "N": case "NO ESPECIFICADO": case "NOT SPECIFIED":
            case "SIN ESPECIFICAR": case "":
                return NO_ESPECIFICADO;

            default:
                // Búsqueda flexible por contenido
                if (valorNormalizado.contains("MACH") || valorNormalizado.contains("MALE")) {
                    return MACHO;
                } else if (valorNormalizado.contains("HEMB") || valorNormalizado.contains("FEM")) {
                    return HEMBRA;
                } else {
                    throw new IllegalArgumentException("No se puede convertir a SexoMascota: " + valor);
                }
        }
    }


    public static List<SexoMascota> getSexosDefinidos() {
        return Arrays.stream(values())
                .filter(SexoMascota::esDefinido)
                .collect(Collectors.toList());
    }


    public static List<SexoMascota> getSexosCompatibles(TipoEspecie especie) {
        return Arrays.stream(values())
                .filter(sexo -> sexo.esCompatibleConEspecie(especie))
                .collect(Collectors.toList());
    }


    public static boolean esValido(String valor) {
        try {
            fromString(valor);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }


    public static SexoMascota getDefault() {
        return NO_ESPECIFICADO;
    }

    // ============ ENUM DE TIPOS DE ESPECIE ============


    public enum TipoEspecie {
        CANINO("Canino", "Perro", "Mamífero"),
        FELINO("Felino", "Gato", "Mamífero"),
        AVE("Ave", "Pájaro", "Ave"),
        REPTIL("Reptil", "Reptil", "Reptil"),
        ROEDOR("Roedor", "Ratón", "Mamífero"),
        EQUINO("Equino", "Caballo", "Mamífero"),
        BOVINO("Bovino", "Vaca", "Mamífero"),
        PORCINO("Porcino", "Cerdo", "Mamífero"),
        OVINO("Ovino", "Oveja", "Mamífero"),
        CAPRINO("Caprino", "Cabra", "Mamífero"),
        EXOTICO("Exótico", "Animal exótico", "Variado");

        private final String nombre;
        private final String ejemplo;
        private final String tipo;

        TipoEspecie(String nombre, String ejemplo, String tipo) {
            this.nombre = nombre;
            this.ejemplo = ejemplo;
            this.tipo = tipo;
        }

        public String getNombre() { return nombre; }
        public String getEjemplo() { return ejemplo; }
        public String getTipo() { return tipo; }


        public boolean esMamifero() {
            return tipo.equals("Mamífero");
        }

        public static TipoEspecie porNombre(String nombre) {
            for (TipoEspecie especie : values()) {
                if (especie.nombre.equalsIgnoreCase(nombre)) {
                    return especie;
                }
            }
            return EXOTICO;
        }
    }

    // ============ GETTERS BÁSICOS ============

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getDisplayName() {
        return nombre;
    }


    public String paraBaseDeDatos() {
        return codigo;
    }

    // ============ MÉTODOS SOBRESCRITOS ============

    @Override
    public String toString() {
        return String.format("%s (%s) - %s", nombre, codigo, descripcion);
    }


    public String toUIString() {
        return String.format("🔤 %s", nombre);
    }


    public String toDocumentoMedico() {
        switch (this) {
            case MACHO: return "Sexo: Masculino";
            case HEMBRA: return "Sexo: Femenino";
            case INDETERMINADO: return "Sexo: Indeterminado";
            case NO_ESPECIFICADO: return "Sexo: No especificado";
            default: return "Sexo: " + nombre;
        }
    }
}