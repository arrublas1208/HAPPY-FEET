package com.happyfeet.controller;

import com.happyfeet.model.entities.Dueno;
import com.happyfeet.model.entities.Factura;
import com.happyfeet.model.entities.Inventario;
import com.happyfeet.model.entities.Servicio;
import com.happyfeet.service.DuenoService;
import com.happyfeet.service.FacturaService;
import com.happyfeet.service.InventarioService;
import com.happyfeet.repository.InventarioRepository;
import com.happyfeet.repository.FacturaRepository;
import com.happyfeet.util.FileLogger;
import com.happyfeet.util.InvoiceGenerator;
import com.happyfeet.view.ConsoleUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class FacturaController {

    private static final FileLogger LOG = FileLogger.getInstance();

    private final FacturaService facturaService;
    private final DuenoService duenoService;
    private final InventarioService inventarioService;
    private final InventarioRepository inventarioRepository;

    public FacturaController(FacturaService facturaService,
                             DuenoService duenoService,
                             InventarioService inventarioService,
                             InventarioRepository inventarioRepository) {
        this.facturaService = facturaService;
        this.duenoService = duenoService;
        this.inventarioService = inventarioService;
        this.inventarioRepository = inventarioRepository;
        LOG.info("FacturaController inicializado");
    }

    // ============ OPERACIONES PRINCIPALES ============


    public void generarYMostrarFactura(Integer consultaId) {
        Factura factura = facturaService.generarFacturaPorConsulta(consultaId);
        String facturaTexto = InvoiceGenerator.generarFacturaTextoPlano(factura);
        System.out.println("\n--- FACTURA ---\n" + facturaTexto);
    }


    public void crearFactura() {
        try {
            // Seleccionar dueño
            int duenoId = ConsoleUtils.readInt("ID del dueño");
            Optional<Dueno> duenoOpt = duenoService.buscarPorId((long) duenoId);

            if (duenoOpt.isEmpty()) {
                System.out.println("No se encontró dueño con ID: " + duenoId);
                return;
            }

            Dueno dueno = duenoOpt.get();
            System.out.println("Cliente: " + dueno.getNombreCompleto());

            // Crear factura base
            Factura nuevaFactura = Factura.builder()
                    .withDueno(dueno)
                    .withFechaEmision(LocalDateTime.now())
                    .build();

            // Agregar items interactivamente
            agregarItemsAFactura(nuevaFactura);

            // Aplicar descuento si es necesario
            if (ConsoleUtils.confirm("¿Aplicar descuento?")) {
                BigDecimal descuento = new BigDecimal(ConsoleUtils.readDouble("Monto del descuento"));
                nuevaFactura.aplicarDescuento(descuento);
            }

            // Guardar factura
            Factura facturaCreada = facturaService.crearFactura(nuevaFactura);

            LOG.info("Factura creada: " + facturaCreada.getNumeroFactura() + " para " + dueno.getNombreCompleto());
            System.out.println("\n=== FACTURA CREADA ===");
            System.out.println(facturaCreada.generarDetalle());

        } catch (Exception e) {
            LOG.error("Error creando factura", e);
            System.err.println("Error al crear factura: " + e.getMessage());
        }
    }

    private void agregarItemsAFactura(Factura factura) {
        System.out.println("\n=== AGREGAR ITEMS A LA FACTURA ===");
        boolean continuar = true;

        while (continuar) {
            int opcion = ConsoleUtils.menu("Tipo de item",
                    "Agregar Servicio",
                    "Agregar Producto",
                    "Finalizar factura"
            );

            switch (opcion) {
                case 1 -> agregarServicioAFactura(factura);
                case 2 -> agregarProductoAFactura(factura);
                case 0, 3 -> continuar = false;
            }

            if (continuar && factura.tieneItems()) {
                System.out.printf("Subtotal actual: $%.2f\n", factura.getSubtotal());
                if (!ConsoleUtils.confirm("¿Agregar más items?")) {
                    continuar = false;
                }
            }
        }

        if (!factura.tieneItems()) {
            System.out.println("⚠️ La factura no tiene items. Agregue al menos uno.");
        }
    }

    private void agregarServicioAFactura(Factura factura) {
        try {
            // Crear servicio simulado - en implementación real vendría de ServicioRepository
            System.out.println("=== SERVICIOS DISPONIBLES ===");
            System.out.println("1. Consulta General - $50,000");
            System.out.println("2. Vacunación - $35,000");
            System.out.println("3. Cirugía Menor - $150,000");
            System.out.println("4. Baño y Peluquería - $25,000");

            int opcionServicio = ConsoleUtils.readInt("Seleccione servicio");

            Servicio servicio = new Servicio();
            switch (opcionServicio) {
                case 1 -> {
                    servicio.setId(1);
                    servicio.setCodigo("CONS001");
                    servicio.setNombre("Consulta General");
                    servicio.setPrecio(new BigDecimal("50000"));
                }
                case 2 -> {
                    servicio.setId(2);
                    servicio.setCodigo("VAC001");
                    servicio.setNombre("Vacunación");
                    servicio.setPrecio(new BigDecimal("35000"));
                }
                case 3 -> {
                    servicio.setId(3);
                    servicio.setCodigo("CIR001");
                    servicio.setNombre("Cirugía Menor");
                    servicio.setPrecio(new BigDecimal("150000"));
                }
                case 4 -> {
                    servicio.setId(4);
                    servicio.setCodigo("EST001");
                    servicio.setNombre("Baño y Peluquería");
                    servicio.setPrecio(new BigDecimal("25000"));
                }
                default -> {
                    System.out.println("Servicio no válido");
                    return;
                }
            }

            factura.agregarServicio(servicio);
            System.out.println("Servicio agregado: " + servicio.getNombre());

        } catch (Exception e) {
            LOG.error("Error agregando servicio a factura", e);
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void agregarProductoAFactura(Factura factura) {
        try {
            // Mostrar productos disponibles
            List<Inventario> productos = inventarioRepository.findAll().stream()
                    .filter(p -> p.getCantidadStock() > 0 && !p.estaVencido())
                    .toList();

            if (productos.isEmpty()) {
                System.out.println("No hay productos disponibles");
                return;
            }

            System.out.println("=== PRODUCTOS DISPONIBLES ===");
            for (int i = 0; i < productos.size(); i++) {
                Inventario p = productos.get(i);
                System.out.printf("%d. %s - Stock: %d - $%.2f\n",
                        i + 1, p.getNombreProducto(), p.getCantidadStock(), p.getPrecioVenta());
            }

            int opcion = ConsoleUtils.readInt("Seleccione producto (número)");
            if (opcion < 1 || opcion > productos.size()) {
                System.out.println("Opción no válida");
                return;
            }

            Inventario producto = productos.get(opcion - 1);
            int cantidadMaxima = producto.getCantidadStock();

            System.out.printf("Producto: %s - Stock disponible: %d\n",
                    producto.getNombreProducto(), cantidadMaxima);

            int cantidad = ConsoleUtils.readInt("Cantidad a facturar");
            if (cantidad <= 0 || cantidad > cantidadMaxima) {
                System.out.println("Cantidad no válida. Debe ser entre 1 y " + cantidadMaxima);
                return;
            }

            // Verificar stock antes de agregar
            if (!producto.tieneStock(cantidad)) {
                System.out.println("Stock insuficiente");
                return;
            }

            // Agregar producto a factura
            factura.agregarProducto(producto, BigDecimal.valueOf(cantidad));

            // Descontar del inventario
            inventarioService.descontarStock(producto.getId(), cantidad);

            System.out.printf("Producto agregado: %s x %d\n", producto.getNombreProducto(), cantidad);

        } catch (Exception e) {
            LOG.error("Error agregando producto a factura", e);
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void listarFacturas() {
        try {
            List<Factura> facturas = facturaService.listarTodas();

            if (facturas.isEmpty()) {
                System.out.println("No hay facturas registradas");
                return;
            }

            System.out.println("\n=== LISTA DE FACTURAS ===");
            System.out.printf("%-15s %-20s %-12s %-10s %-12s\n",
                    "NÚMERO", "CLIENTE", "FECHA", "TOTAL", "ESTADO");
            System.out.println("-".repeat(75));

            for (Factura factura : facturas) {
                String cliente = factura.getDueno() != null ?
                        factura.getDueno().getNombreCompleto() : "Cliente ID: " + factura.getDuenoId();

                System.out.printf("%-15s %-20s %-12s $%-9.2f %-12s\n",
                        factura.getNumeroFactura(),
                        cliente.length() > 20 ? cliente.substring(0, 17) + "..." : cliente,
                        factura.getFechaEmision().toLocalDate(),
                        factura.getTotal(),
                        factura.getEstado().getNombre());
            }

        } catch (Exception e) {
            LOG.error("Error listando facturas", e);
            System.err.println("Error al listar facturas: " + e.getMessage());
        }
    }

    public void buscarFactura() {
        try {
            String numeroFactura = ConsoleUtils.readNonEmpty("Número de factura");

            // Buscar por número (simulación - en implementación real sería por número)
            Optional<Factura> facturaOpt = facturaService.listarTodas().stream()
                    .filter(f -> f.getNumeroFactura().equalsIgnoreCase(numeroFactura))
                    .findFirst();

            if (facturaOpt.isEmpty()) {
                System.out.println("No se encontró factura con número: " + numeroFactura);
                return;
            }

            Factura factura = facturaOpt.get();
            mostrarDetalleFactura(factura);

        } catch (Exception e) {
            LOG.error("Error buscando factura", e);
            System.err.println("Error en búsqueda: " + e.getMessage());
        }
    }

    public void mostrarDetalleFactura(Factura factura) {
        System.out.println("\n" + factura.generarDetalle());

        if (factura.getHistorialEstados().size() > 1) {
            System.out.println("=== HISTORIAL DE ESTADOS ===");
            factura.getHistorialEstados().forEach(System.out::println);
        }
    }

    public void pagarFactura() {
        try {
            int facturaId = ConsoleUtils.readInt("ID de la factura");
            Optional<Factura> facturaOpt = facturaService.obtenerPorId(facturaId);

            if (facturaOpt.isEmpty()) {
                System.out.println("No se encontró factura con ID: " + facturaId);
                return;
            }

            Factura factura = facturaOpt.get();

            if (factura.estaPagada()) {
                System.out.println("La factura ya está pagada");
                return;
            }

            System.out.println("Factura: " + factura.getNumeroFactura());
            System.out.printf("Total a pagar: $%.2f\n", factura.getTotal());

            // Seleccionar forma de pago
            System.out.println("=== FORMAS DE PAGO ===");
            Factura.FormaPago[] formasPago = Factura.FormaPago.values();
            for (int i = 0; i < formasPago.length; i++) {
                System.out.printf("%d. %s\n", i + 1, formasPago[i].getNombre());
            }

            int opcionPago = ConsoleUtils.readInt("Seleccione forma de pago");
            if (opcionPago < 1 || opcionPago > formasPago.length) {
                System.out.println("Forma de pago no válida");
                return;
            }

            Factura.FormaPago formaPago = formasPago[opcionPago - 1];

            if (ConsoleUtils.confirm("¿Confirma el pago?")) {
                factura.marcarComoPagada(formaPago, "Sistema");
                facturaService.crearFactura(factura); // Actualizar

                LOG.info("Factura pagada: " + factura.getNumeroFactura() + " - " + formaPago.getNombre());
                System.out.println("✅ Factura pagada exitosamente con " + formaPago.getNombre());
            }

        } catch (Exception e) {
            LOG.error("Error procesando pago", e);
            System.err.println("Error al procesar pago: " + e.getMessage());
        }
    }

    public void cancelarFactura() {
        try {
            int facturaId = ConsoleUtils.readInt("ID de la factura");
            Optional<Factura> facturaOpt = facturaService.obtenerPorId(facturaId);

            if (facturaOpt.isEmpty()) {
                System.out.println("No se encontró factura con ID: " + facturaId);
                return;
            }

            Factura factura = facturaOpt.get();

            if (factura.getEstado() != Factura.FacturaEstado.PENDIENTE) {
                System.out.println("Solo se pueden cancelar facturas pendientes");
                return;
            }

            System.out.println("Factura: " + factura.getNumeroFactura());
            String motivo = ConsoleUtils.readOptional("Motivo de cancelación (opcional)");

            if (ConsoleUtils.confirm("¿Confirma la cancelación?")) {
                factura.cancelar(motivo.isEmpty() ? "Cancelada por usuario" : motivo, "Sistema");
                facturaService.crearFactura(factura); // Actualizar

                LOG.info("Factura cancelada: " + factura.getNumeroFactura());
                System.out.println("✅ Factura cancelada exitosamente");
            }

        } catch (Exception e) {
            LOG.error("Error cancelando factura", e);
            System.err.println("Error al cancelar factura: " + e.getMessage());
        }
    }

    // ============ REPORTES ============

    public void generarReporteVentas() {
        try {
            List<Factura> facturas = facturaService.listarTodas();

            if (facturas.isEmpty()) {
                System.out.println("No hay facturas para generar reporte");
                return;
            }

            System.out.println("\n=== REPORTE DE VENTAS ===");

            // Totales generales
            BigDecimal totalVentas = facturas.stream()
                    .filter(Factura::estaPagada)
                    .map(Factura::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalServicios = facturas.stream()
                    .filter(Factura::estaPagada)
                    .map(Factura::getTotalServicios)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalProductos = facturas.stream()
                    .filter(Factura::estaPagada)
                    .map(Factura::getTotalProductos)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            long facturasPagadas = facturas.stream().filter(Factura::estaPagada).count();
            long facturasPendientes = facturas.stream()
                    .filter(f -> f.getEstado() == Factura.FacturaEstado.PENDIENTE).count();

            System.out.printf("Total de facturas: %d\n", facturas.size());
            System.out.printf("Facturas pagadas: %d\n", facturasPagadas);
            System.out.printf("Facturas pendientes: %d\n", facturasPendientes);
            System.out.printf("Total en ventas: $%.2f\n", totalVentas);
            System.out.printf("Total servicios: $%.2f\n", totalServicios);
            System.out.printf("Total productos: $%.2f\n", totalProductos);

            if (facturasPagadas > 0) {
                BigDecimal ticketPromedio = totalVentas.divide(BigDecimal.valueOf(facturasPagadas), 2, BigDecimal.ROUND_HALF_UP);
                System.out.printf("Ticket promedio: $%.2f\n", ticketPromedio);
            }

        } catch (Exception e) {
            LOG.error("Error generando reporte de ventas", e);
            System.err.println("Error generando reporte: " + e.getMessage());
        }
    }

    public void mostrarFacturasVencidas() {
        try {
            List<Factura> facturasVencidas = facturaService.listarTodas().stream()
                    .filter(Factura::estaVencida)
                    .toList();

            if (facturasVencidas.isEmpty()) {
                System.out.println("No hay facturas vencidas");
                return;
            }

            System.out.println("\n=== FACTURAS VENCIDAS ===");
            System.out.printf("%-15s %-20s %-12s %-10s %-5s\n",
                    "NÚMERO", "CLIENTE", "VENCIMIENTO", "TOTAL", "DÍAS");
            System.out.println("-".repeat(70));

            for (Factura factura : facturasVencidas) {
                String cliente = factura.getDueno() != null ?
                        factura.getDueno().getNombreCompleto() : "Cliente ID: " + factura.getDuenoId();

                System.out.printf("%-15s %-20s %-12s $%-9.2f %d\n",
                        factura.getNumeroFactura(),
                        cliente.length() > 20 ? cliente.substring(0, 17) + "..." : cliente,
                        factura.getFechaVencimiento(),
                        factura.getTotal(),
                        Math.abs(factura.getDiasVencimiento()));
            }

        } catch (Exception e) {
            LOG.error("Error mostrando facturas vencidas", e);
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Getters para testing
    public FacturaService getFacturaService() {
        return facturaService;
    }

    public DuenoService getDuenoService() {
        return duenoService;
    }

    // Métodos añadidos desde versión integrada

    
    
    public void run() {
        while (true) {
            int opcion = ConsoleUtils.menu("GESTIÓN DE FACTURAS Y REPORTES",
                    "Crear nueva factura",
                    "Listar todas las facturas",
                    "Buscar factura por número",
                    "Procesar pago de factura",
                    "Cancelar factura",
                    "Ver facturas vencidas",
                    "Generar reporte de ventas"
            );

            switch (opcion) {
                case 1 -> crearFactura();
                case 2 -> listarFacturas();
                case 3 -> buscarFactura();
                case 4 -> pagarFactura();
                case 5 -> cancelarFactura();
                case 6 -> mostrarFacturasVencidas();
                case 7 -> generarReporteVentas();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Opción no válida");
            }

            ConsoleUtils.pause();
        }
    }


    private String generarFacturaTextoPlano(Factura factura) {
        StringBuilder sb = new StringBuilder();

        // Encabezado
        sb.append("╔══════════════════════════════════════════════════════════════╗\n");
        sb.append("║                    HAPPY FEET VETERINARIA                   ║\n");
        sb.append("║                      FACTURA DE VENTA                       ║\n");
        sb.append("╚══════════════════════════════════════════════════════════════╝\n\n");

        // Información de la clínica
        sb.append("CLÍNICA VETERINARIA HAPPY FEET\n");
        sb.append("Dirección: Calle Principal #123\n");
        sb.append("Teléfono: (555) 123-4567\n");
        sb.append("Email: info@happyfeet.com\n");
        sb.append("NIT: 900.123.456-7\n\n");

        // Información de la factura
        sb.append("FACTURA No: ").append(factura.getNumeroFactura()).append("\n");
        sb.append("Fecha: ").append(factura.getFechaEmision().toLocalDate()).append("\n");
        sb.append("Hora: ").append(factura.getFechaEmision().toLocalTime().toString().substring(0, 8)).append("\n\n");

        // Información del cliente
        sb.append("CLIENTE:\n");
        if (factura.getDueno() != null) {
            sb.append("Nombre: ").append(factura.getDueno().getNombreCompleto()).append("\n");
            sb.append("Documento: ").append(factura.getDueno().getDocumentoIdentidad()).append("\n");
            sb.append("Teléfono: ").append(factura.getDueno().getTelefono()).append("\n");
        } else {
            sb.append("Cliente ID: ").append(factura.getDuenoId()).append("\n");
        }
        sb.append("\n");

        // Detalle de items
        sb.append("DETALLE DE LA FACTURA:\n");
        sb.append("-".repeat(65)).append("\n");
        sb.append(String.format("%-30s %5s %10s %12s\n", "DESCRIPCIÓN", "CANT", "PRECIO", "SUBTOTAL"));
        sb.append("-".repeat(65)).append("\n");

        for (Factura.ItemFactura item : factura.getItems()) {
            sb.append(String.format("%-30s %5.0f %10.2f %12.2f\n",
                truncateString(item.getDescripcion(), 30),
                item.getCantidad(),
                item.getPrecioUnitario(),
                item.getSubtotal()));
        }

        sb.append("-".repeat(65)).append("\n");

        // Totales
        sb.append(String.format("%47s %12.2f\n", "SUBTOTAL:", factura.getSubtotal()));

        if (factura.getDescuento().compareTo(BigDecimal.ZERO) > 0) {
            sb.append(String.format("%47s %12.2f\n", "DESCUENTO:", factura.getDescuento()));
        }

        BigDecimal iva = factura.getSubtotal().subtract(factura.getDescuento()).multiply(new BigDecimal("0.19"));
        sb.append(String.format("%47s %12.2f\n", "IVA (19%):", iva));
        sb.append(String.format("%47s %12.2f\n", "TOTAL:", factura.getTotal()));

        sb.append("\n");
        sb.append("Estado: ").append(factura.getEstado().getNombre()).append("\n");

        if (factura.getFormaPago() != null) {
            sb.append("Forma de pago: ").append(factura.getFormaPago().getNombre()).append("\n");
        }

        sb.append("\n");
        sb.append("¡Gracias por confiar en Happy Feet Veterinaria!\n");
        sb.append("El cuidado de su mascota es nuestra prioridad\n\n");

        sb.append("🤖 Generado con [Claude Code](https://claude.com/claude-code)\n");
        sb.append("Co-Authored-By: Claude <noreply@anthropic.com>\n");

        return sb.toString();
    }

    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }

}
