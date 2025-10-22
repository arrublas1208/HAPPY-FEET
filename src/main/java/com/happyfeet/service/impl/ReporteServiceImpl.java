package com.happyfeet.service.impl;

import com.happyfeet.repository.FacturaRepository;
import com.happyfeet.repository.HistorialMedicoRepository;
import com.happyfeet.repository.InventarioRepository;
import com.happyfeet.service.LoggerManager;
import com.happyfeet.service.ReporteService;
import com.happyfeet.model.entities.Inventario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReporteServiceImpl implements ReporteService {

    private final LoggerManager logger;
    private final HistorialMedicoRepository historialRepo;
    private final FacturaRepository facturaRepo;
    private final InventarioRepository inventarioRepo;

    public ReporteServiceImpl(LoggerManager logger,
                             HistorialMedicoRepository historialRepo,
                             FacturaRepository facturaRepo,
                             InventarioRepository inventarioRepo) {
        this.logger = logger;
        this.historialRepo = historialRepo;
        this.facturaRepo = facturaRepo;
        this.inventarioRepo = inventarioRepo;
    }

    @Override
    public void generarReporteVentasMensuales(String tipo, Map<String, Object> datos) {
        if (logger != null) {
            logger.logInfo("Generando reporte de ventas mensuales. Tipo=" + tipo);
        }
        // Implementación mínima: solo validaciones básicas y logging
        if (tipo == null || tipo.trim().isEmpty()) {
            if (logger != null) logger.logWarning("Tipo de reporte no especificado, usando 'RESUMEN'.");
            tipo = "RESUMEN";
        }
        if (datos == null) {
            if (logger != null) logger.logWarning("Datos nulos recibidos para reporte; se usará un mapa vacío.");
            datos = Collections.emptyMap();
        }
        if (logger != null) logger.logInfo("Reporte de ventas '" + tipo + "' generado con " + datos.size() + " entradas.");
    }

    @Override
    public byte[] generarReporteInventarioPdf(String idReporte) {
        if (logger != null) logger.logInfo("Generando reporte de inventario PDF: " + idReporte);
        // Implementación mínima: retornar contenido vacío
        return new byte[0];
    }

    @Override
    public byte[] generarReporteInventarioExcel(String idReporte) {
        if (logger != null) logger.logInfo("Generando reporte de inventario Excel: " + idReporte);
        // Implementación mínima: retornar contenido vacío
        return new byte[0];
    }

    @Override
    public Map<String, Object> generarReporteInventarioDatos(String idReporte) {
        if (logger != null) logger.logInfo("Generando datos de inventario para reporte: " + idReporte);
        // Implementación mínima: retornar mapa con metadatos básicos
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("idReporte", idReporte);
        resultado.put("totalItems", 0);
        resultado.put("generado", true);
        return resultado;
    }

    @Override
    public List<String> obtenerServiciosMasSolicitados() {
        if (logger != null) logger.logInfo("Obteniendo servicios más solicitados desde BD");

        try {
            // Obtener datos desde el repositorio
            List<Map<String, Object>> resultados = historialRepo.obtenerServiciosMasSolicitados(10);

            if (resultados == null || resultados.isEmpty()) {
                return Arrays.asList("No hay datos disponibles de servicios");
            }

            // Convertir resultados a formato String usando programación funcional
            return resultados.stream()
                    .map(row -> {
                        String tipoEvento = (String) row.get("tipo_evento_medico");
                        Long cantidad = ((Number) row.get("cantidad")).longValue();
                        return String.format("%s: %d consultas", tipoEvento, cantidad);
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            if (logger != null) logger.logError("Error obteniendo servicios más solicitados: " + e.getMessage());
            return Arrays.asList("Error al obtener datos de servicios");
        }
    }

    @Override
    public List<String> obtenerDesempenoVeterinario() {
        if (logger != null) logger.logInfo("Obteniendo desempeño veterinario desde BD");

        try {
            // Obtener datos desde el repositorio
            List<Map<String, Object>> resultados = historialRepo.obtenerDesempenoVeterinarios();

            if (resultados == null || resultados.isEmpty()) {
                return Arrays.asList("No hay datos disponibles de veterinarios");
            }

            // Convertir resultados a formato String usando programación funcional
            return resultados.stream()
                    .map(row -> {
                        Integer vetId = (Integer) row.get("veterinario_id");
                        Long totalConsultas = ((Number) row.get("total_consultas")).longValue();
                        return String.format("Veterinario ID %d: %d consultas realizadas", vetId, totalConsultas);
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            if (logger != null) logger.logError("Error obteniendo desempeño veterinario: " + e.getMessage());
            return Arrays.asList("Error al obtener datos de veterinarios");
        }
    }

    @Override
    public List<String> obtenerEstadoInventario() {
        if (logger != null) logger.logInfo("Obteniendo estado del inventario desde BD");

        try {
            // Obtener todos los productos del inventario
            List<Inventario> productos = inventarioRepo.findAll();

            if (productos == null || productos.isEmpty()) {
                return Arrays.asList("No hay productos en el inventario");
            }

            // Calcular estadísticas usando programación funcional
            long totalProductos = productos.size();

            long stockNormal = productos.stream()
                    .filter(p -> p.getCantidadStock() > p.getStockMinimo())
                    .count();

            long stockBajo = productos.stream()
                    .filter(p -> p.getCantidadStock() <= p.getStockMinimo() && p.getCantidadStock() > 0)
                    .count();

            long agotado = productos.stream()
                    .filter(p -> p.getCantidadStock() == 0)
                    .count();

            long proximosVencer = productos.stream()
                    .filter(p -> p.getFechaVencimiento() != null)
                    .filter(p -> p.getFechaVencimiento().isBefore(LocalDate.now().plusDays(30)))
                    .filter(p -> p.getFechaVencimiento().isAfter(LocalDate.now()))
                    .count();

            // Crear lista de resultados
            List<String> resultado = new ArrayList<>();
            resultado.add(String.format("Total de productos: %d", totalProductos));
            resultado.add(String.format("Stock normal: %d (%.1f%%)", stockNormal, (stockNormal * 100.0 / totalProductos)));
            resultado.add(String.format("Stock bajo: %d (%.1f%%)", stockBajo, (stockBajo * 100.0 / totalProductos)));
            resultado.add(String.format("Agotados: %d (%.1f%%)", agotado, (agotado * 100.0 / totalProductos)));
            resultado.add(String.format("ALERTA - Proximos a vencer (30 dias): %d", proximosVencer));

            return resultado;

        } catch (Exception e) {
            if (logger != null) logger.logError("Error obteniendo estado del inventario: " + e.getMessage());
            return Arrays.asList("Error al obtener datos del inventario");
        }
    }

    @Override
    public List<String> obtenerFacturacionPorPeriodo(String periodo) {
        if (logger != null) logger.logInfo("Obteniendo facturación para periodo: " + periodo);

        try {
            // Parsear el período (formato: "MM-YYYY" o "YYYY")
            LocalDateTime inicio, fin;

            if (periodo.matches("\\d{2}-\\d{4}")) {
                // Formato mes-año
                String[] partes = periodo.split("-");
                int mes = Integer.parseInt(partes[0]);
                int anio = Integer.parseInt(partes[1]);
                inicio = LocalDateTime.of(anio, mes, 1, 0, 0);
                fin = inicio.plusMonths(1).minusSeconds(1);
            } else if (periodo.matches("\\d{4}")) {
                // Formato año
                int anio = Integer.parseInt(periodo);
                inicio = LocalDateTime.of(anio, 1, 1, 0, 0);
                fin = LocalDateTime.of(anio, 12, 31, 23, 59, 59);
            } else {
                // Por defecto: mes actual
                inicio = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
                fin = inicio.plusMonths(1).minusSeconds(1);
            }

            // Obtener datos desde el repositorio
            Map<String, Object> datos = facturaRepo.obtenerFacturacionPorPeriodo(inicio, fin);

            if (datos == null || datos.isEmpty()) {
                return Arrays.asList("No hay facturas en el período " + periodo);
            }

            // Formatear resultados
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            List<String> resultado = new ArrayList<>();

            resultado.add(String.format("Período: %s al %s", inicio.format(formatter), fin.format(formatter)));
            resultado.add(String.format("Total facturado: $%s",
                    formatearMonto((BigDecimal) datos.getOrDefault("total", BigDecimal.ZERO))));
            resultado.add(String.format("Cantidad de facturas: %d",
                    ((Number) datos.getOrDefault("cantidad", 0)).intValue()));
            resultado.add(String.format("Factura promedio: $%s",
                    formatearMonto((BigDecimal) datos.getOrDefault("promedio", BigDecimal.ZERO))));

            return resultado;

        } catch (Exception e) {
            if (logger != null) logger.logError("Error obteniendo facturación: " + e.getMessage());
            return Arrays.asList("Error al obtener datos de facturación para período: " + periodo);
        }
    }

    /**
     * Formatea un monto BigDecimal a formato de moneda
     */
    private String formatearMonto(BigDecimal monto) {
        if (monto == null) return "0.00";
        return String.format("%,.2f", monto);
    }
}
