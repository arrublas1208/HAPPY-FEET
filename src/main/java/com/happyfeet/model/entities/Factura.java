package com.happyfeet.model.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Factura {
    private Integer id;
    private String numeroFactura;
    private Integer duenoId;
    private LocalDateTime fechaEmision;
    private LocalDate fechaVencimiento;
    private BigDecimal subtotal;
    private BigDecimal impuestos;
    private BigDecimal descuento;
    private BigDecimal total;
    private FacturaEstado estado;
    private FormaPago formaPago;
    private String observaciones;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // Referencias a entidades relacionadas
    private Dueno dueno;
    private LocalDateTime fechaPago; // Para registrar cuando fue pagada

    // Items de la factura (Composite Pattern)
    private final List<ItemFactura> items;

    // Historial de estados
    private final List<CambioEstadoFactura> historialEstados;

    // Constantes de negocio
    private static final BigDecimal IVA = new BigDecimal("0.19"); // 19%
    private static final int DIAS_VENCIMIENTO = 30;


    public enum FacturaEstado {
        PENDIENTE("Pendiente", "Factura generada, pendiente de pago") {
            @Override
            public boolean puedeTransicionarA(FacturaEstado nuevoEstado) {
                return nuevoEstado == PAGADA || nuevoEstado == CANCELADA || nuevoEstado == VENCIDA;
            }

            @Override
            public boolean permiteModificacion() {
                return true;
            }
        },

        PAGADA("Pagada", "Factura pagada completamente") {
            @Override
            public boolean puedeTransicionarA(FacturaEstado nuevoEstado) {
                return false; // Estado final
            }

            @Override
            public boolean permiteModificacion() {
                return false;
            }
        },

        CANCELADA("Cancelada", "Factura cancelada") {
            @Override
            public boolean puedeTransicionarA(FacturaEstado nuevoEstado) {
                return false; // Estado final
            }

            @Override
            public boolean permiteModificacion() {
                return false;
            }
        },

        VENCIDA("Vencida", "Factura vencida, pendiente de regularización") {
            @Override
            public boolean puedeTransicionarA(FacturaEstado nuevoEstado) {
                return nuevoEstado == PAGADA || nuevoEstado == CANCELADA;
            }

            @Override
            public boolean permiteModificacion() {
                return false;
            }
        };

        private final String nombre;
        private final String descripcion;

        FacturaEstado(String nombre, String descripcion) {
            this.nombre = nombre;
            this.descripcion = descripcion;
        }

        public abstract boolean puedeTransicionarA(FacturaEstado nuevoEstado);
        public abstract boolean permiteModificacion();

        public String getNombre() { return nombre; }
        public String getDescripcion() { return descripcion; }
    }

    /**
     * ENUM de formas de pago.
     */
    public enum FormaPago {
        EFECTIVO("Efectivo", "Pago en efectivo"),
        TARJETA_DEBITO("Tarjeta Débito", "Pago con tarjeta de débito"),
        TARJETA_CREDITO("Tarjeta Crédito", "Pago con tarjeta de crédito"),
        TRANSFERENCIA("Transferencia", "Transferencia bancaria"),
        PUNTOS("Puntos", "Pago con puntos de cliente frecuente");

        private final String nombre;
        private final String descripcion;

        FormaPago(String nombre, String descripcion) {
            this.nombre = nombre;
            this.descripcion = descripcion;
        }

        public String getNombre() { return nombre; }
        public String getDescripcion() { return descripcion; }
    }


    public static class ItemFactura {
        private Integer id;
        private TipoItem tipoItem;
        private Integer servicioId;
        private Integer productoId;
        private String descripcion;
        private BigDecimal cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal descuentoItem;
        private BigDecimal subtotal;

        // Referencias a entidades
        private Servicio servicio;
        private Inventario producto;

        public enum TipoItem {
            SERVICIO("Servicio", "Servicio veterinario"),
            PRODUCTO("Producto", "Producto o medicamento");

            private final String nombre;
            private final String descripcion;

            TipoItem(String nombre, String descripcion) {
                this.nombre = nombre;
                this.descripcion = descripcion;
            }

            public String getNombre() { return nombre; }
            public String getDescripcion() { return descripcion; }
        }

        // Builder Pattern para ItemFactura
        public static class Builder {
            private final ItemFactura item;

            public Builder(TipoItem tipoItem) {
                item = new ItemFactura();
                item.tipoItem = Objects.requireNonNull(tipoItem, "Tipo de item es requerido");
                item.cantidad = BigDecimal.ONE;
                item.descuentoItem = BigDecimal.ZERO;
            }

            public Builder withServicio(Servicio servicio) {
                item.servicio = Objects.requireNonNull(servicio, "Servicio no puede ser nulo");
                item.servicioId = servicio.getId();
                item.descripcion = servicio.getNombre();
                item.precioUnitario = servicio.getPrecio();
                return this;
            }

            public Builder withProducto(Inventario producto, BigDecimal cantidad) {
                item.producto = Objects.requireNonNull(producto, "Producto no puede ser nulo");
                item.productoId = producto.getId();
                item.descripcion = producto.getNombreProducto();
                item.precioUnitario = producto.getPrecioVenta();
                item.cantidad = Objects.requireNonNull(cantidad, "Cantidad es requerida");
                return this;
            }

            public Builder withDescripcion(String descripcion) {
                item.descripcion = descripcion;
                return this;
            }

            public Builder withPrecioUnitario(BigDecimal precio) {
                item.precioUnitario = Objects.requireNonNull(precio, "Precio unitario es requerido");
                if (precio.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Precio no puede ser negativo");
                }
                return this;
            }

            public Builder withCantidad(BigDecimal cantidad) {
                item.cantidad = Objects.requireNonNull(cantidad, "Cantidad es requerida");
                if (cantidad.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Cantidad debe ser mayor a cero");
                }
                return this;
            }

            public Builder withDescuentoItem(BigDecimal descuento) {
                item.descuentoItem = descuento != null ? descuento : BigDecimal.ZERO;
                if (item.descuentoItem.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Descuento no puede ser negativo");
                }
                return this;
            }

            public ItemFactura build() {
                // Validaciones finales
                if (item.descripcion == null || item.descripcion.trim().isEmpty()) {
                    throw new IllegalStateException("Descripción del item es requerida");
                }
                if (item.precioUnitario == null) {
                    throw new IllegalStateException("Precio unitario es requerido");
                }
                if (item.cantidad == null) {
                    throw new IllegalStateException("Cantidad es requerida");
                }

                // Calcular subtotal
                item.calcularSubtotal();

                return item;
            }
        }

        private void calcularSubtotal() {
            BigDecimal totalSinDescuento = precioUnitario.multiply(cantidad);
            this.subtotal = totalSinDescuento.subtract(descuentoItem);

            if (this.subtotal.compareTo(BigDecimal.ZERO) < 0) {
                this.subtotal = BigDecimal.ZERO;
            }
        }

        // Getters y setters
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public TipoItem getTipoItem() { return tipoItem; }
        public void setTipoItem(TipoItem tipoItem) { this.tipoItem = tipoItem; }

        public Integer getServicioId() { return servicioId; }
        public void setServicioId(Integer servicioId) { this.servicioId = servicioId; }

        public Integer getProductoId() { return productoId; }
        public void setProductoId(Integer productoId) { this.productoId = productoId; }

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) {
            this.descripcion = Objects.requireNonNull(descripcion);
        }

        public BigDecimal getCantidad() { return cantidad; }
        public void setCantidad(BigDecimal cantidad) {
            this.cantidad = Objects.requireNonNull(cantidad);
            calcularSubtotal();
        }

        public BigDecimal getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(BigDecimal precioUnitario) {
            this.precioUnitario = Objects.requireNonNull(precioUnitario);
            calcularSubtotal();
        }

        public BigDecimal getDescuentoItem() { return descuentoItem; }
        public void setDescuentoItem(BigDecimal descuentoItem) {
            this.descuentoItem = descuentoItem != null ? descuentoItem : BigDecimal.ZERO;
            calcularSubtotal();
        }

        public BigDecimal getSubtotal() { return subtotal; }

        public Servicio getServicio() { return servicio; }
        public void setServicio(Servicio servicio) {
            this.servicio = servicio;
            if (servicio != null) {
                this.servicioId = servicio.getId();
            }
        }

        public Inventario getProducto() { return producto; }
        public void setProducto(Inventario producto) {
            this.producto = producto;
            if (producto != null) {
                this.productoId = producto.getId();
            }
        }

        @Override
        public String toString() {
            return String.format("%s: %s - Cant: %s x $%s = $%s",
                    tipoItem.getNombre(), descripcion, cantidad, precioUnitario, subtotal);
        }
    }

    public static class CambioEstadoFactura {
        private final FacturaEstado estadoAnterior;
        private final FacturaEstado estadoNuevo;
        private final LocalDateTime fechaCambio;
        private final String motivo;
        private final String usuario;

        public CambioEstadoFactura(FacturaEstado estadoAnterior, FacturaEstado estadoNuevo,
                                   String motivo, String usuario) {
            this.estadoAnterior = estadoAnterior;
            this.estadoNuevo = estadoNuevo;
            this.fechaCambio = LocalDateTime.now();
            this.motivo = motivo;
            this.usuario = usuario;
        }

        // Getters
        public FacturaEstado getEstadoAnterior() { return estadoAnterior; }
        public FacturaEstado getEstadoNuevo() { return estadoNuevo; }
        public LocalDateTime getFechaCambio() { return fechaCambio; }
        public String getMotivo() { return motivo; }
        public String getUsuario() { return usuario; }

        @Override
        public String toString() {
            return String.format("%s -> %s | %s | %s",
                    estadoAnterior.getNombre(), estadoNuevo.getNombre(),
                    fechaCambio, motivo);
        }
    }

    public Factura() {
        this.items = new ArrayList<>();
        this.historialEstados = new ArrayList<>();
        this.fechaEmision = LocalDateTime.now();
        this.fechaVencimiento = LocalDate.now().plusDays(DIAS_VENCIMIENTO);
        this.subtotal = BigDecimal.ZERO;
        this.impuestos = BigDecimal.ZERO;
        this.descuento = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
        this.estado = FacturaEstado.PENDIENTE;
        this.formaPago = FormaPago.EFECTIVO;
        this.fechaCreacion = LocalDateTime.now();

        registrarCambioEstado(null, FacturaEstado.PENDIENTE, "Creación de factura", "Sistema");
    }

    public static class Builder {
        private final Factura factura;

        public Builder() {
            this.factura = new Factura();
        }

        public Builder withDuenoId(Integer duenoId) {
            factura.duenoId = Objects.requireNonNull(duenoId, "ID del dueño es requerido");
            return this;
        }

        public Builder withDueno(Dueno dueno) {
            factura.dueno = Objects.requireNonNull(dueno, "Dueño no puede ser nulo");
            factura.duenoId = dueno.getId();
            return this;
        }

        public Builder withFechaEmision(LocalDateTime fechaEmision) {
            if (fechaEmision != null && fechaEmision.isAfter(LocalDateTime.now())) {
                throw new IllegalArgumentException("Fecha de emisión no puede ser futura");
            }
            factura.fechaEmision = fechaEmision != null ? fechaEmision : LocalDateTime.now();
            return this;
        }

        public Builder withObservaciones(String observaciones) {
            factura.observaciones = observaciones;
            return this;
        }

        public Factura build() {
            // Validaciones finales
            if (factura.duenoId == null) {
                throw new IllegalStateException("Dueño es requerido para la factura");
            }

            // Generar número de factura
            factura.numeroFactura = generarNumeroFactura();

            return factura;
        }

        private String generarNumeroFactura() {
            return "FACT-" + System.currentTimeMillis() + "-" +
                    (factura.duenoId != null ? factura.duenoId : "0");
        }
    }

    public void agregarItem(ItemFactura item) {
        Objects.requireNonNull(item, "Item no puede ser nulo");
        items.add(item);
        recalcularTotales();
    }

    public void agregarServicio(Servicio servicio) {
        ItemFactura item = new ItemFactura.Builder(ItemFactura.TipoItem.SERVICIO)
                .withServicio(servicio)
                .build();
        agregarItem(item);
    }

    public void agregarProducto(Inventario producto, BigDecimal cantidad) {
        ItemFactura item = new ItemFactura.Builder(ItemFactura.TipoItem.PRODUCTO)
                .withProducto(producto, cantidad)
                .build();
        agregarItem(item);
    }

    public boolean removerItem(int indice) {
        if (indice >= 0 && indice < items.size()) {
            items.remove(indice);
            recalcularTotales();
            return true;
        }
        return false;
    }

    public void limpiarItems() {
        items.clear();
        recalcularTotales();
    }

    // ============ MÉTODOS DE CÁLCULO ============

    private void recalcularTotales() {
        calcularSubtotal();
        calcularImpuestos();
        calcularTotal();
    }

    private void calcularSubtotal() {
        this.subtotal = items.stream()
                .map(ItemFactura::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void calcularImpuestos() {
        // Los servicios veterinarios están exentos de IVA en muchos países
        // Solo los productos llevan impuestos
        BigDecimal baseImponible = items.stream()
                .filter(item -> item.getTipoItem() == ItemFactura.TipoItem.PRODUCTO)
                .map(ItemFactura::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.impuestos = baseImponible.multiply(IVA)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void calcularTotal() {
        BigDecimal totalAntesDescuento = subtotal.add(impuestos);
        this.total = totalAntesDescuento.subtract(descuento)
                .setScale(2, RoundingMode.HALF_UP);

        if (this.total.compareTo(BigDecimal.ZERO) < 0) {
            this.total = BigDecimal.ZERO;
        }
    }

    public void aplicarDescuento(BigDecimal descuento) {
        this.descuento = descuento != null ? descuento.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        if (this.descuento.compareTo(BigDecimal.ZERO) < 0) {
            this.descuento = BigDecimal.ZERO;
        }
        recalcularTotales();
    }

    public void aplicarDescuentoPorcentual(BigDecimal porcentaje) {
        if (porcentaje != null && porcentaje.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal descuentoCalculado = subtotal.multiply(porcentaje)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            aplicarDescuento(descuentoCalculado);
        }
    }

    // ============ MÉTODOS DE GESTIÓN DE ESTADOS ============

    public void cambiarEstado(FacturaEstado nuevoEstado, String motivo, String usuario) {
        Objects.requireNonNull(nuevoEstado, "Nuevo estado no puede ser nulo");
        Objects.requireNonNull(motivo, "Motivo no puede ser nulo");
        Objects.requireNonNull(usuario, "Usuario no puede ser nulo");

        // Idempotencia: si el estado solicitado es el mismo que el actual, no hacer nada
        if (this.estado == nuevoEstado) {
            return;
        }

        // Mantener validaciones para transiciones reales
        if (!estado.puedeTransicionarA(nuevoEstado)) {
            throw new IllegalStateException(
                    String.format("Transición inválida: %s -> %s", estado.getNombre(), nuevoEstado.getNombre()));
        }

        FacturaEstado estadoAnterior = this.estado;
        this.estado = nuevoEstado;
        this.fechaActualizacion = LocalDateTime.now();

        registrarCambioEstado(estadoAnterior, nuevoEstado, motivo, usuario);
    }

    public void marcarComoPagada(FormaPago formaPago, String usuario) {
        this.formaPago = formaPago != null ? formaPago : FormaPago.EFECTIVO;
        cambiarEstado(FacturaEstado.PAGADA, "Factura pagada", usuario);
    }

    public void cancelar(String motivo, String usuario) {
        cambiarEstado(FacturaEstado.CANCELADA,
                motivo != null ? "Cancelada: " + motivo : "Factura cancelada",
                usuario);
    }

    public void marcarComoVencida(String usuario) {
        if (fechaVencimiento.isBefore(LocalDate.now())) {
            cambiarEstado(FacturaEstado.VENCIDA, "Factura vencida", usuario);
        }
    }

    private void registrarCambioEstado(FacturaEstado estadoAnterior, FacturaEstado estadoNuevo,
                                       String motivo, String usuario) {
        CambioEstadoFactura cambio = new CambioEstadoFactura(estadoAnterior, estadoNuevo, motivo, usuario);
        historialEstados.add(cambio);
    }

    // ============ MÉTODOS DE VALIDACIÓN Y CONSULTA ============

    public boolean estaVencida() {
        return fechaVencimiento.isBefore(LocalDate.now()) && estado == FacturaEstado.PENDIENTE;
    }

    public boolean puedeSerModificada() {
        return estado.permiteModificacion();
    }

    public boolean estaPagada() {
        return estado == FacturaEstado.PAGADA;
    }

    public BigDecimal getSaldoPendiente() {
        return estaPagada() ? BigDecimal.ZERO : total;
    }

    public long getDiasVencimiento() {
        return LocalDate.now().until(fechaVencimiento).getDays();
    }

    public boolean tieneItems() {
        return !items.isEmpty();
    }

    public BigDecimal getTotalProductos() {
        return items.stream()
                .filter(item -> item.getTipoItem() == ItemFactura.TipoItem.PRODUCTO)
                .map(ItemFactura::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalServicios() {
        return items.stream()
                .filter(item -> item.getTipoItem() == ItemFactura.TipoItem.SERVICIO)
                .map(ItemFactura::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    // ============ GETTERS Y SETTERS ============

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = Objects.requireNonNull(numeroFactura);
    }

    public Integer getDuenoId() { return duenoId; }
    public void setDuenoId(Integer duenoId) {
        this.duenoId = Objects.requireNonNull(duenoId);
    }

    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = Objects.requireNonNull(fechaEmision);
    }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = Objects.requireNonNull(fechaVencimiento);
    }

    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getImpuestos() { return impuestos; }
    public BigDecimal getDescuento() { return descuento; }
    public BigDecimal getTotal() { return total; }

    public FacturaEstado getEstado() { return estado; }
    public FormaPago getFormaPago() { return formaPago; }
    public void setFormaPago(FormaPago formaPago) {
        this.formaPago = formaPago != null ? formaPago : FormaPago.EFECTIVO;
    }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Dueno getDueno() { return dueno; }
    public void setDueno(Dueno dueno) {
        this.dueno = dueno;
        if (dueno != null) {
            this.duenoId = dueno.getId();
        }
    }

    public List<ItemFactura> getItems() { return List.copyOf(items); }
    public List<CambioEstadoFactura> getHistorialEstados() { return List.copyOf(historialEstados); }

    // ============ MÉTODOS SOBRESCRITOS ============

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Factura factura = (Factura) obj;
        return Objects.equals(numeroFactura, factura.numeroFactura);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroFactura);
    }

    @Override
    public String toString() {
        return String.format("""
            🧾 FACTURA #%s
            Estado: %s
            Fecha: %s
            Dueño: %s
            Subtotal: $%,.2f
            Impuestos: $%,.2f
            Descuento: $%,.2f
            TOTAL: $%,.2f
            Items: %d
            """,
                numeroFactura,
                estado.getNombre(),
                fechaEmision.toLocalDate(),
                dueno != null ? dueno.getNombreCompleto() : "Dueño ID: " + duenoId,
                subtotal, impuestos, descuento, total,
                items.size()
        );
    }

    // ============ MÉTODOS ESTÁTICOS ============

    public static Builder builder() {
        return new Builder();
    }

     // Genera un resumen detallado de la factura para impresión.

    public String generarDetalle() {
        StringBuilder sb = new StringBuilder();
        sb.append("=").append("=".repeat(50)).append("\n");
        sb.append("FACTURA #").append(numeroFactura).append("\n");
        sb.append("=").append("=".repeat(50)).append("\n");
        sb.append("Fecha: ").append(fechaEmision.toLocalDate()).append("\n");
        sb.append("Dueño: ").append(dueno != null ? dueno.getNombreCompleto() : "N/A").append("\n");
        sb.append("Estado: ").append(estado.getNombre()).append("\n");
        sb.append("-".repeat(52)).append("\n");

        // Items
        for (int i = 0; i < items.size(); i++) {
            ItemFactura item = items.get(i);
            sb.append(String.format("%2d. %-30s %6.2f x $%8.2f = $%10.2f\n",
                    i + 1,
                    item.getDescripcion().length() > 30 ? item.getDescripcion().substring(0, 27) + "..." : item.getDescripcion(),
                    item.getCantidad(), item.getPrecioUnitario(), item.getSubtotal()));
        }

        sb.append("-".repeat(52)).append("\n");
        sb.append(String.format("SUBTOTAL: $%,.2f\n", subtotal));
        sb.append(String.format("IMPUESTOS: $%,.2f\n", impuestos));
        sb.append(String.format("DESCUENTO: $%,.2f\n", descuento));
        sb.append("=").append("=".repeat(50)).append("\n");
        sb.append(String.format("TOTAL: $%,.2f\n", total));
        sb.append("=").append("=".repeat(50)).append("\n");

        return sb.toString();
    }
}