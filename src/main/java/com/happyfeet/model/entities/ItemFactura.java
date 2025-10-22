package com.happyfeet.model.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;


public class ItemFactura {
    private Integer id;
    private Integer facturaId;
    private TipoItem tipoItem;
    private Integer servicioId;
    private Integer productoId;
    private String descripcion;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuento;
    private BigDecimal subtotal;
    private LocalDateTime fechaCreacion;

    private Servicio servicio;
    private Inventario producto;

    private static final BigDecimal DESCUENTO_MAXIMO = new BigDecimal("50.00"); // 50%
    private static final int ESCALA_MONETARIA = 2;
    private static final RoundingMode MODO_REDONDEO = RoundingMode.HALF_UP;


    public enum TipoItem {
        SERVICIO("Servicio", "Servicio veterinario o procedimiento médico") {
            @Override
            public boolean validarCantidad(BigDecimal cantidad) {
                // Los servicios generalmente son cantidad 1
                return cantidad != null &&
                        cantidad.compareTo(BigDecimal.ZERO) > 0 &&
                        cantidad.compareTo(new BigDecimal("10")) <= 0; // Máximo 10 unidades
            }

            @Override
            public String getPrefijoDescripcion() {
                return "Servicio: ";
            }
        },

        PRODUCTO("Producto", "Medicamento, insumo o producto vendido") {
            @Override
            public boolean validarCantidad(BigDecimal cantidad) {
                // Los productos pueden tener cantidades variables
                return cantidad != null &&
                        cantidad.compareTo(BigDecimal.ZERO) > 0 &&
                        cantidad.compareTo(new BigDecimal("1000")) <= 0; // Máximo 1000 unidades
            }

            @Override
            public String getPrefijoDescripcion() {
                return "Producto: ";
            }
        };

        private final String nombre;
        private final String descripcion;

        TipoItem(String nombre, String descripcion) {
            this.nombre = nombre;
            this.descripcion = descripcion;
        }

        public abstract boolean validarCantidad(BigDecimal cantidad);


        public abstract String getPrefijoDescripcion();

        public String getNombre() { return nombre; }
        public String getDescripcion() { return descripcion; }


        public static TipoItem porNombre(String nombre) {
            for (TipoItem tipo : values()) {
                if (tipo.nombre.equalsIgnoreCase(nombre)) {
                    return tipo;
                }
            }
            throw new IllegalArgumentException("Tipo de item no válido: " + nombre);
        }
    }

    // Constructor privado para Builder
    public ItemFactura() {
        this.cantidad = BigDecimal.ONE;
        this.precioUnitario = BigDecimal.ZERO;
        this.descuento = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
        this.fechaCreacion = LocalDateTime.now();
    }


    public static class Builder {
        private final ItemFactura item;
        private boolean built = false;

        public Builder(TipoItem tipoItem) {
            this.item = new ItemFactura();
            this.item.tipoItem = Objects.requireNonNull(tipoItem, "Tipo de item es requerido");
        }


        public Builder comoServicio(Servicio servicio) {
            validarNoConstruido();
            this.item.servicio = Objects.requireNonNull(servicio, "Servicio no puede ser nulo");
            this.item.servicioId = servicio.getId();
            this.item.descripcion = servicio.getNombre();
            this.item.precioUnitario = servicio.getPrecio();
            return this;
        }


        public Builder comoProducto(Inventario producto, BigDecimal cantidad) {
            validarNoConstruido();
            this.item.producto = Objects.requireNonNull(producto, "Producto no puede ser nulo");
            this.item.productoId = producto.getId();
            this.item.descripcion = producto.getNombreProducto();
            this.item.precioUnitario = producto.getPrecioVenta();
            withCantidad(cantidad);
            return this;
        }


        public Builder withDescripcion(String descripcion) {
            validarNoConstruido();
            this.item.descripcion = descripcion;
            return this;
        }


        public Builder withPrecioUnitario(BigDecimal precioUnitario) {
            validarNoConstruido();
            this.item.precioUnitario = Objects.requireNonNull(precioUnitario, "Precio unitario es requerido");
            validarPrecio(precioUnitario);
            return this;
        }


        public Builder withCantidad(BigDecimal cantidad) {
            validarNoConstruido();
            this.item.cantidad = Objects.requireNonNull(cantidad, "Cantidad es requerida");
            validarCantidad(cantidad);
            return this;
        }


        public Builder withDescuento(BigDecimal descuento) {
            validarNoConstruido();
            this.item.descuento = descuento != null ? descuento : BigDecimal.ZERO;
            validarDescuento(this.item.descuento);
            return this;
        }


        public Builder withDescuentoPorcentual(BigDecimal porcentaje) {
            validarNoConstruido();
            if (porcentaje != null && porcentaje.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal descuentoCalculado = item.precioUnitario.multiply(porcentaje)
                        .divide(new BigDecimal("100"), ESCALA_MONETARIA, MODO_REDONDEO)
                        .multiply(item.cantidad);
                return withDescuento(descuentoCalculado);
            }
            return this;
        }


        public Builder withFacturaId(Integer facturaId) {
            validarNoConstruido();
            this.item.facturaId = facturaId;
            return this;
        }


        public ItemFactura build() {
            validarNoConstruido();
            built = true;

            // Validaciones finales
            validarIntegridad();
            validarPrecio(item.precioUnitario);
            validarCantidad(item.cantidad);
            validarDescuento(item.descuento);

            // Calcular subtotal
            item.calcularSubtotal();

            // Asegurar descripción
            if (item.descripcion == null || item.descripcion.trim().isEmpty()) {
                item.descripcion = generarDescripcionAutomatica();
            }

            return item;
        }

        private void validarNoConstruido() {
            if (built) {
                throw new IllegalStateException("Builder ya fue utilizado, crear nuevo builder");
            }
        }

        private void validarIntegridad() {
            // Validar que tenga al menos servicio o producto
            if (item.servicio == null && item.producto == null) {
                throw new IllegalStateException("Item debe tener un servicio o producto asociado");
            }

            // Validar que no tenga ambos
            if (item.servicio != null && item.producto != null) {
                throw new IllegalStateException("Item no puede tener both servicio y producto");
            }

            // Validar consistencia de tipo
            if (item.servicio != null && item.tipoItem != TipoItem.SERVICIO) {
                throw new IllegalStateException("Tipo de item debe ser SERVICIO cuando se asocia un servicio");
            }
            if (item.producto != null && item.tipoItem != TipoItem.PRODUCTO) {
                throw new IllegalStateException("Tipo de item debe ser PRODUCTO cuando se asocia un producto");
            }
        }

        private void validarPrecio(BigDecimal precio) {
            if (precio.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Precio no puede ser negativo: " + precio);
            }
            if (precio.compareTo(new BigDecimal("1000000")) > 0) {
                throw new IllegalArgumentException("Precio excesivamente alto: " + precio);
            }
        }

        private void validarCantidad(BigDecimal cantidad) {
            if (!item.tipoItem.validarCantidad(cantidad)) {
                throw new IllegalArgumentException(
                        "Cantidad no válida para " + item.tipoItem.getNombre() + ": " + cantidad);
            }
        }

        private void validarDescuento(BigDecimal descuento) {
            if (descuento.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Descuento no puede ser negativo: " + descuento);
            }

            // Validar que el descuento no sea mayor al precio total
            BigDecimal precioTotal = item.precioUnitario.multiply(item.cantidad);
            if (descuento.compareTo(precioTotal) > 0) {
                throw new IllegalArgumentException(
                        "Descuento no puede ser mayor al precio total: " + descuento + " > " + precioTotal);
            }

            // Validar descuento máximo permitido
            if (descuento.compareTo(DESCUENTO_MAXIMO) > 0 &&
                    descuento.compareTo(precioTotal.multiply(new BigDecimal("0.5"))) > 0) {
                throw new IllegalArgumentException("Descuento excede el máximo permitido: " + descuento);
            }
        }

        private String generarDescripcionAutomatica() {
            if (item.servicio != null) {
                return item.tipoItem.getPrefijoDescripcion() + item.servicio.getNombre();
            } else if (item.producto != null) {
                return item.tipoItem.getPrefijoDescripcion() + item.producto.getNombreProducto();
            } else {
                return "Item sin descripción";
            }
        }
    }

    // ============ MÉTODOS DE NEGOCIO PRINCIPALES ============


    public void calcularSubtotal() {
        BigDecimal totalSinDescuento = precioUnitario.multiply(cantidad)
                .setScale(ESCALA_MONETARIA, MODO_REDONDEO);

        this.subtotal = totalSinDescuento.subtract(descuento)
                .setScale(ESCALA_MONETARIA, MODO_REDONDEO);

        // Asegurar que el subtotal no sea negativo
        if (this.subtotal.compareTo(BigDecimal.ZERO) < 0) {
            this.subtotal = BigDecimal.ZERO;
        }
    }


    public void actualizarPrecio(BigDecimal nuevoPrecio) {
        this.precioUnitario = Objects.requireNonNull(nuevoPrecio, "Nuevo precio es requerido");
        if (nuevoPrecio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Precio no puede ser negativo");
        }
        calcularSubtotal();
    }


    public void actualizarCantidad(BigDecimal nuevaCantidad) {
        if (!tipoItem.validarCantidad(nuevaCantidad)) {
            throw new IllegalArgumentException("Cantidad no válida: " + nuevaCantidad);
        }
        this.cantidad = nuevaCantidad;
        calcularSubtotal();
    }


    public void aplicarDescuento(BigDecimal descuento) {
        this.descuento = descuento != null ? descuento : BigDecimal.ZERO;
        if (this.descuento.compareTo(BigDecimal.ZERO) < 0) {
            this.descuento = BigDecimal.ZERO;
        }
        calcularSubtotal();
    }


    public void aplicarDescuentoPorcentual(BigDecimal porcentaje) {
        if (porcentaje != null && porcentaje.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal descuentoCalculado = precioUnitario.multiply(porcentaje)
                    .divide(new BigDecimal("100"), ESCALA_MONETARIA, MODO_REDONDEO)
                    .multiply(cantidad);
            aplicarDescuento(descuentoCalculado);
        }
    }

    // ============ MÉTODOS DE VALIDACIÓN Y CONSULTA ============

    public boolean tieneDescuento() {
        return descuento.compareTo(BigDecimal.ZERO) > 0;
    }


    public BigDecimal calcularPorcentajeDescuento() {
        BigDecimal totalSinDescuento = precioUnitario.multiply(cantidad);
        if (totalSinDescuento.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return descuento.multiply(new BigDecimal("100"))
                .divide(totalSinDescuento, ESCALA_MONETARIA, MODO_REDONDEO);
    }


    public boolean esServicio() {
        return tipoItem == TipoItem.SERVICIO;
    }


    public boolean esProducto() {
        return tipoItem == TipoItem.PRODUCTO;
    }


    public Integer getEntidadAsociadaId() {
        return esServicio() ? servicioId : productoId;
    }


    public BigDecimal getPrecioTotalSinDescuento() {
        return precioUnitario.multiply(cantidad)
                .setScale(ESCALA_MONETARIA, MODO_REDONDEO);
    }


    public boolean esValido() {
        try {
            validarIntegridad();
            validarPrecio(precioUnitario);
            validarCantidad(cantidad);
            validarDescuento(descuento);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void validarIntegridad() {
        if (tipoItem == null) {
            throw new IllegalStateException("Tipo de item es requerido");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalStateException("Descripción es requerida");
        }
        if (esServicio() && servicioId == null) {
            throw new IllegalStateException("Servicio ID es requerido para items de servicio");
        }
        if (esProducto() && productoId == null) {
            throw new IllegalStateException("Producto ID es requerido para items de producto");
        }
    }

    private void validarPrecio(BigDecimal precio) {
        if (precio == null || precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Precio unitario no válido: " + precio);
        }
    }

    private void validarCantidad(BigDecimal cantidad) {
        if (cantidad == null || !tipoItem.validarCantidad(cantidad)) {
            throw new IllegalStateException("Cantidad no válida: " + cantidad);
        }
    }

    private void validarDescuento(BigDecimal descuento) {
        if (descuento == null || descuento.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Descuento no válido: " + descuento);
        }
    }

    // ============ GETTERS Y SETTERS ============

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getFacturaId() { return facturaId; }
    public void setFacturaId(Integer facturaId) { this.facturaId = facturaId; }

    public TipoItem getTipoItem() { return tipoItem; }
    public void setTipoItem(TipoItem tipoItem) {
        this.tipoItem = Objects.requireNonNull(tipoItem, "Tipo de item es requerido");
    }

    public Integer getServicioId() { return servicioId; }
    public void setServicioId(Integer servicioId) {
        this.servicioId = servicioId;
        if (servicioId != null) {
            this.tipoItem = TipoItem.SERVICIO;
        }
    }

    public Integer getProductoId() { return productoId; }
    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
        if (productoId != null) {
            this.tipoItem = TipoItem.PRODUCTO;
        }
    }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) {
        this.descripcion = Objects.requireNonNull(descripcion, "Descripción es requerida");
        if (descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("Descripción no puede estar vacía");
        }
    }

    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = Objects.requireNonNull(cantidad, "Cantidad es requerida");
        if (!tipoItem.validarCantidad(cantidad)) {
            throw new IllegalArgumentException("Cantidad no válida para " + tipoItem.getNombre());
        }
        calcularSubtotal();
    }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = Objects.requireNonNull(precioUnitario, "Precio unitario es requerido");
        if (precioUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Precio no puede ser negativo");
        }
        calcularSubtotal();
    }

    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento != null ? descuento : BigDecimal.ZERO;
        if (this.descuento.compareTo(BigDecimal.ZERO) < 0) {
            this.descuento = BigDecimal.ZERO;
        }
        calcularSubtotal();
    }

    public BigDecimal getSubtotal() { return subtotal; }
    // No hay setter para subtotal - se calcula automáticamente

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
        if (servicio != null) {
            this.servicioId = servicio.getId();
            this.tipoItem = TipoItem.SERVICIO;
            if (this.descripcion == null) {
                this.descripcion = servicio.getNombre();
            }
            if (this.precioUnitario == null || this.precioUnitario.compareTo(BigDecimal.ZERO) == 0) {
                this.precioUnitario = servicio.getPrecio();
            }
        }
    }

    public Inventario getProducto() { return producto; }
    public void setProducto(Inventario producto) {
        this.producto = producto;
        if (producto != null) {
            this.productoId = producto.getId();
            this.tipoItem = TipoItem.PRODUCTO;
            if (this.descripcion == null) {
                this.descripcion = producto.getNombreProducto();
            }
            if (this.precioUnitario == null || this.precioUnitario.compareTo(BigDecimal.ZERO) == 0) {
                this.precioUnitario = producto.getPrecioVenta();
            }
        }
    }

    // ============ MÉTODOS SOBRESCRITOS ============

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ItemFactura that = (ItemFactura) obj;

        // Dos items son iguales si tienen mismo tipo y misma entidad asociada
        return tipoItem == that.tipoItem &&
                Objects.equals(getEntidadAsociadaId(), that.getEntidadAsociadaId()) &&
                Objects.equals(facturaId, that.facturaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipoItem, getEntidadAsociadaId(), facturaId);
    }

    @Override
    public String toString() {
        return String.format("""
            ItemFactura{
                id=%d, tipo=%s, descripción='%s',
                cantidad=%s, precioUnitario=$%,.2f, 
                descuento=$%,.2f, subtotal=$%,.2f
            }""",
                id, tipoItem.getNombre(), descripcion,
                cantidad, precioUnitario, descuento, subtotal
        );
    }


    public String toDetalleFactura() {
        return String.format("%-40s %6.2f x $%10.2f %s $%10.2f",
                (descripcion.length() > 40 ? descripcion.substring(0, 37) + "..." : descripcion),
                cantidad,
                precioUnitario,
                tieneDescuento() ? String.format("(-$%.2f)", descuento) : "          ",
                subtotal
        );
    }

    // ============ MÉTODOS ESTÁTICOS Y FACTORY METHODS ============

    public static Builder builder(TipoItem tipoItem) {
        return new Builder(tipoItem);
    }

    public static ItemFactura crearServicio(Servicio servicio) {
        return new Builder(TipoItem.SERVICIO)
                .comoServicio(servicio)
                .build();
    }

    public static ItemFactura crearProducto(Inventario producto, BigDecimal cantidad) {
        return new Builder(TipoItem.PRODUCTO)
                .comoProducto(producto, cantidad)
                .build();
    }

    public static ItemFactura crearPersonalizado(TipoItem tipoItem, String descripcion,
                                                 BigDecimal precio, BigDecimal cantidad) {
        return new Builder(tipoItem)
                .withDescripcion(descripcion)
                .withPrecioUnitario(precio)
                .withCantidad(cantidad)
                .build();
    }

    // Métodos añadidos desde versión integrada

    
    
    public void setPrecio(BigDecimal bigDecimal) {
    }

}
