package com.happyfeet.service;

import java.util.List;
import java.util.Map;

public interface ReporteService {
    void generarReporteVentasMensuales(String tipo, Map<String, Object> datos);
    byte[] generarReporteInventarioPdf(String idReporte);
    byte[] generarReporteInventarioExcel(String idReporte);
    Map<String, Object> generarReporteInventarioDatos(String idReporte);
    List<String> obtenerServiciosMasSolicitados();
    List<String> obtenerDesempenoVeterinario();
    List<String> obtenerEstadoInventario();
    List<String> obtenerFacturacionPorPeriodo(String periodo);
}
