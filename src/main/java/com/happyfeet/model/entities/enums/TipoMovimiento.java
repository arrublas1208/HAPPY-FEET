package com.happyfeet.model.entities.enums;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public enum TipoMovimiento {

    ENTRADA_COMPRA("EC", "Entrada por Compra", "Compra a proveedor", true, false) {
        @Override
        public boolean requiereAutorizacionEspecial() {
            return false;
        }

        @Override
        public boolean afectaCostoPromedio() {
            return true;
        }

        @Override
        public BigDecimal getLimiteCantidad() {
            return new BigDecimal("10000"); // Límite alto para compras
        }

        @Override
        public List<String> getDocumentosRequeridos() {
            return Arrays.asList("FACTURA", "REMISIÓN");
        }

        @Override
        public String getDescripcionCompleta() {
            return "Ingreso de productos al inventario por compra a proveedor";
        }
    },


    ENTRADA_DONACION("ED", "Entrada por Donación", "Donación o muestra", true, false) {
        @Override
        public boolean requiereAutorizacionEspecial() {
            return true; // Requiere autorización de gerencia
        }

        @Override
        public boolean afectaCostoPromedio() {
            return false; // No afecta costo promedio
        }

        @Override
        public BigDecimal getLimiteCantidad() {
            return new BigDecimal("100"); // Límite bajo para donaciones
        }

        @Override
        public List<String> getDocumentosRequeridos() {
            return Arrays.asList("CARTA_DONACION", "ACTA_RECEPCIÓN");
        }

        @Override
        public String getDescripcionCompleta() {
            return "Ingreso de productos por donación o muestra gratuita";
        }
    },

    ENTRADA_DEVOLUCION("EDV", "Entrada por Devolución", "Devolución de cliente", true, false) {
        @Override
        public boolean requiereAutorizacionEspecial() {
            return true; // Requiere validación de calidad
        }

        @Override
        public boolean afectaCostoPromedio() {
            return false;
        }

        @Override
        public BigDecimal getLimiteCantidad() {
            return new BigDecimal("50"); // Límite moderado
        }

        @Override
        public List<String> getDocumentosRequeridos() {
            return Arrays.asList("NOTA_CREDITO", "FORMULARIO_DEVOLUCION");
        }

        @Override
        public String getDescripcionCompleta() {
            return "Reingreso de productos por devolución de cliente";
        }
    },

    SALIDA_VENTA("SV", "Salida por Venta", "Venta a cliente", false, true) {
        @Override
        public boolean requiereAutorizacionEspecial() {
            return false;
        }

        @Override
        public boolean afectaCostoPromedio() {
            return false;
        }

        @Override
        public BigDecimal getLimiteCantidad() {
            return new BigDecimal("1000"); // Límite para ventas normales
        }

        @Override
        public List<String> getDocumentosRequeridos() {
            return Arrays.asList("FACTURA", "RECIBO");
        }

        @Override
        public String getDescripcionCompleta() {
            return "Egreso de productos por venta a cliente";
        }
    },

    SALIDA_MUESTRA("SM", "Salida para Muestra", "Muestra médica", false, true) {
        @Override
        public boolean requiereAutorizacionEspecial() {
            return true; // Requiere autorización médica
        }

        @Override
        public boolean afectaCostoPromedio() {
            return false;
        }

        @Override
        public BigDecimal getLimiteCantidad() {
            return new BigDecimal("10"); // Límite bajo para muestras
        }

        @Override
        public List<String> getDocumentosRequeridos() {
            return Arrays.asList("FORMULARIO_MUESTRA", "AUTORIZACION_MÉDICA");
        }

        @Override
        public String getDescripcionCompleta() {
            return "Egreso de productos para muestras médicas o uso interno";
        }
    },

    SALIDA_DANO("SD", "Salida por Daño", "Pérdida o daño", false, true) {
        @Override
        public boolean requiereAutorizacionEspecial() {
            return true; // Requiere investigación y aprobación
        }

        @Override
        public boolean afectaCostoPromedio() {
            return false;
        }

        @Override
        public BigDecimal getLimiteCantidad() {
            return new BigDecimal("100"); // Límite para pérdidas
        }

        @Override
        public List<String> getDocumentosRequeridos() {
            return Arrays.asList("INFORME_DANO", "ACTA_DESTRUCCION");
        }

        @Override
        public String getDescripcionCompleta() {
            return "Baja de productos por daño, pérdida o destrucción";
        }
    },

    AJUSTE_POSITIVO("AP", "Ajuste Positivo", "Corrección de inventario", true, false) {
        @Override
        public boolean requiereAutorizacionEspecial() {
            return true; // Requiere auditoría
        }

        @Override
        public boolean afectaCostoPromedio() {
            return false;
        }

        @Override
        public BigDecimal getLimiteCantidad() {
            return new BigDecimal("500"); // Límite para ajustes
        }

        @Override
        public List<String> getDocumentosRequeridos() {
            return Arrays.asList("INFORME_AUDITORIA", "JUSTIFICACION_AJUSTE");
        }

        @Override
        public String getDescripcionCompleta() {
            return "Ajuste positivo por error de inventario o conteo";
        }
    },

    AJUSTE_NEGATIVO("AN", "Ajuste Negativo", "Corrección de inventario", false, true) {
        @Override
        public boolean requiereAutorizacionEspecial() {
            return true; // Requiere auditoría
        }

        @Override
        public boolean afectaCostoPromedio() {
            return false;
        }

        @Override
        public BigDecimal getLimiteCantidad() {
            return new BigDecimal("500"); // Límite para ajustes
        }

        @Override
        public List<String> getDocumentosRequeridos() {
            return Arrays.asList("INFORME_AUDITORIA", "JUSTIFICACION_AJUSTE");
        }

        @Override
        public String getDescripcionCompleta() {
            return "Ajuste negativo por error de inventario o conteo";
        }
    },

    VENCIMIENTO("VEN", "Vencimiento", "Producto vencido", false, true) {
        @Override
        public boolean requiereAutorizacionEspecial() {
            return true; // Requiere control de calidad
        }

        @Override
        public boolean afectaCostoPromedio() {
            return false;
        }

        @Override
        public BigDecimal getLimiteCantidad() {
            return BigDecimal.ZERO; // Sin límite, depende del stock vencido
        }

        @Override
        public List<String> getDocumentosRequeridos() {
            return Arrays.asList("INFORME_VENCIMIENTO", "ACTA_DESTRUCCION");
        }

        @Override
        public String getDescripcionCompleta() {
            return "Baja de productos por fecha de vencimiento cumplida";
        }
    },

    SALIDA_TRANSFERENCIA("ST", "Salida por Transferencia", "Transferencia entre sucursales", false, true) {
        @Override
        public boolean requiereAutorizacionEspecial() {
            return true; // Requiere autorización de logística
        }

        @Override
        public boolean afectaCostoPromedio() {
            return false;
        }

        @Override
        public BigDecimal getLimiteCantidad() {
            return new BigDecimal("5000"); // Límite alto para transferencias
        }

        @Override
        public List<String> getDocumentosRequeridos() {
            return Arrays.asList("ORDEN_TRANSFERENCIA", "GUIA_REMISION");
        }

        @Override
        public String getDescripcionCompleta() {
            return "Salida de productos por transferencia a otra sucursal";
        }
    },

    ENTRADA_TRANSFERENCIA("ET", "Entrada por Transferencia", "Transferencia entre sucursales", true, false) {
        @Override
        public boolean requiereAutorizacionEspecial() {
            return false; // La autorización está en la salida
        }

        @Override
        public boolean afectaCostoPromedio() {
            return false;
        }

        @Override
        public BigDecimal getLimiteCantidad() {
            return new BigDecimal("5000"); // Límite alto para transferencias
        }

        @Override
        public List<String> getDocumentosRequeridos() {
            return Arrays.asList("GUIA_REMISION", "ACTA_RECEPCION");
        }

        @Override
        public String getDescripcionCompleta() {
            return "Entrada de productos por transferencia de otra sucursal";
        }
    };

    // ============ ATRIBUTOS Y CONSTRUCTOR ============

    private final String codigo;
    private final String nombre;
    private final String descripcion;
    private final boolean aumentaStock;
    private final boolean disminuyeStock;

    TipoMovimiento(String codigo, String nombre, String descripcion,
                   boolean aumentaStock, boolean disminuyeStock) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.aumentaStock = aumentaStock;
        this.disminuyeStock = disminuyeStock;
    }

    // ============ MÉTODOS ABSTRACTOS ============

    public abstract boolean requiereAutorizacionEspecial();
    public abstract boolean afectaCostoPromedio();

    public abstract BigDecimal getLimiteCantidad();

    public abstract List<String> getDocumentosRequeridos();

    public abstract String getDescripcionCompleta();

    // ============ MÉTODOS CONCRETOS COMUNES ============


    public boolean aumentaStock() {
        return aumentaStock;
    }

    public boolean disminuyeStock() {
        return disminuyeStock;
    }

    public boolean esEntrada() {
        return aumentaStock && !disminuyeStock;
    }

    public boolean esSalida() {
        return !aumentaStock && disminuyeStock;
    }

    public boolean esAjuste() {
        return this == AJUSTE_POSITIVO || this == AJUSTE_NEGATIVO;
    }

    public boolean tieneImpactoFinanciero() {
        return this == ENTRADA_COMPRA || this == SALIDA_VENTA ||
                this == SALIDA_DANO || this == VENCIMIENTO;
    }

    public boolean requiereControlCalidad() {
        return this == ENTRADA_DEVOLUCION || this == VENCIMIENTO ||
                this == SALIDA_DANO;
    }

    public boolean validarCantidad(BigDecimal cantidad) {
        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        BigDecimal limite = getLimiteCantidad();
        if (limite.compareTo(BigDecimal.ZERO) == 0) {
            return true; // Sin límite
        }

        return cantidad.compareTo(limite) <= 0;
    }

    public int getFactorStock() {
        if (aumentaStock) return 1;
        if (disminuyeStock) return -1;
        return 0;
    }
    public CategoriaMovimiento getCategoria() {
        if (esEntrada()) return CategoriaMovimiento.ENTRADA;
        if (esSalida()) return CategoriaMovimiento.SALIDA;
        if (esAjuste()) return CategoriaMovimiento.AJUSTE;
        return CategoriaMovimiento.OTRO;
    }

    public String generarMensajeMovimiento(BigDecimal cantidad, String producto) {
        String accion = aumentaStock ? "Ingresaron" : "Egresaron";
        return String.format("%s %.2f unidades de %s por %s",
                accion, cantidad, producto, nombre.toLowerCase());
    }

    // ============ ENUM DE CATEGORÍAS ============

    public enum CategoriaMovimiento {
        ENTRADA("Entrada", "Movimientos que aumentan inventario", "🟢"),
        SALIDA("Salida", "Movimientos que disminuyen inventario", "🔴"),
        AJUSTE("Ajuste", "Correcciones de inventario", "🟡"),
        OTRO("Otro", "Otros tipos de movimiento", "⚫");

        private final String nombre;
        private final String descripcion;
        private final String emoji;

        CategoriaMovimiento(String nombre, String descripcion, String emoji) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.emoji = emoji;
        }

        public String getNombre() { return nombre; }
        public String getDescripcion() { return descripcion; }
        public String getEmoji() { return emoji; }

        public String getDisplayName() {
            return emoji + " " + nombre;
        }
    }

    // ============ MÉTODOS ESTÁTICOS DE UTILIDAD ============

    public static Optional<TipoMovimiento> porCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return Optional.empty();
        }

        return Arrays.stream(values())
                .filter(tipo -> tipo.codigo.equalsIgnoreCase(codigo.trim()))
                .findFirst();
    }

    public static Optional<TipoMovimiento> porNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return Optional.empty();
        }

        String nombreNormalizado = nombre.trim().toUpperCase();
        return Arrays.stream(values())
                .filter(tipo -> tipo.nombre.toUpperCase().equals(nombreNormalizado))
                .findFirst();
    }

    public static TipoMovimiento fromString(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Valor no puede ser nulo o vacío");
        }

        String valorNormalizado = valor.trim().toUpperCase();

        // Búsqueda por código
        Optional<TipoMovimiento> porCodigo = porCodigo(valorNormalizado);
        if (porCodigo.isPresent()) {
            return porCodigo.get();
        }

        // Búsqueda por nombre o variaciones
        switch (valorNormalizado) {
            case "ENTRADA": case "COMPRA": case "INGRESO":
                return ENTRADA_COMPRA;

            case "DONACION": case "DONACIÓN": case "MUESTRA GRATUITA":
                return ENTRADA_DONACION;

            case "DEVOLUCION": case "DEVOLUCIÓN": case "RETORNO":
                return ENTRADA_DEVOLUCION;

            case "VENTA": case "SALIDA": case "EGRESO":
                return SALIDA_VENTA;

            case "MUESTRA": case "MUESTRA MEDICA": case "USO INTERNO":
                return SALIDA_MUESTRA;

            case "DAÑO": case "DANO": case "PERDIDA": case "PÉRDIDA":
                return SALIDA_DANO;

            case "AJUSTE POSITIVO": case "AJUSTE+": case "CORRECCION POSITIVA":
                return AJUSTE_POSITIVO;

            case "AJUSTE NEGATIVO": case "AJUSTE-": case "CORRECCION NEGATIVA":
                return AJUSTE_NEGATIVO;

            case "VENCIMIENTO": case "CADUCIDAD": case "EXPIRACION":
                return VENCIMIENTO;

            case "TRANSFERENCIA SALIDA": case "TRANSFER OUT":
                return SALIDA_TRANSFERENCIA;

            case "TRANSFERENCIA ENTRADA": case "TRANSFER IN":
                return ENTRADA_TRANSFERENCIA;

            default:
                // Búsqueda flexible por contenido
                if (valorNormalizado.contains("ENTRADA") || valorNormalizado.contains("INGRESO")) {
                    return ENTRADA_COMPRA;
                } else if (valorNormalizado.contains("SALIDA") || valorNormalizado.contains("EGRESO")) {
                    return SALIDA_VENTA;
                } else if (valorNormalizado.contains("AJUSTE")) {
                    return AJUSTE_POSITIVO;
                } else {
                    throw new IllegalArgumentException("Tipo de movimiento no reconocido: " + valor);
                }
        }
    }

    public static List<TipoMovimiento> getPorCategoria(CategoriaMovimiento categoria) {
        return Arrays.stream(values())
                .filter(tipo -> tipo.getCategoria() == categoria)
                .collect(Collectors.toList());
    }

    public static List<TipoMovimiento> getMovimientosConAutorizacion() {
        return Arrays.stream(values())
                .filter(TipoMovimiento::requiereAutorizacionEspecial)
                .collect(Collectors.toList());
    }

    public static List<TipoMovimiento> getMovimientosQueAfectanCosto() {
        return Arrays.stream(values())
                .filter(TipoMovimiento::afectaCostoPromedio)
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

    public static List<TipoMovimiento> getEntradas() {
        return Arrays.stream(values())
                .filter(TipoMovimiento::esEntrada)
                .collect(Collectors.toList());
    }

    public static List<TipoMovimiento> getSalidas() {
        return Arrays.stream(values())
                .filter(TipoMovimiento::esSalida)
                .collect(Collectors.toList());
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
        String emoji = aumentaStock ? "📥" : disminuyeStock ? "📤" : "🔄";
        return String.format("%s %s", emoji, nombre);
    }

    public String toReporteString() {
        return String.format("[%s] %s - %s", codigo, nombre, descripcion);
    }

    public String toContabilidadString() {
        String tipo = aumentaStock ? "ENTRADA" : disminuyeStock ? "SALIDA" : "AJUSTE";
        return String.format("%s - %s (%s)", tipo, nombre, codigo);
    }
}
