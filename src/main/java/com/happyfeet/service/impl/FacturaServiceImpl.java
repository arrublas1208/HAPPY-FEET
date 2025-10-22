package com.happyfeet.service.impl;

import com.happyfeet.model.entities.Factura;
import com.happyfeet.model.entities.Inventario;
import com.happyfeet.model.entities.Servicio;
import com.happyfeet.repository.FacturaRepository;
import com.happyfeet.service.FacturaService;
import com.happyfeet.service.HistorialMedicoService;
import com.happyfeet.model.entities.HistorialMedico;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FacturaServiceImpl implements FacturaService {

    private final FacturaRepository repository;
    private final HistorialMedicoService historialMedicoService;

    public FacturaServiceImpl(FacturaRepository repository, HistorialMedicoService historialMedicoService) {
        this.repository = Objects.requireNonNull(repository);
        this.historialMedicoService = Objects.requireNonNull(historialMedicoService);
    }

    @Override
    public Factura crearFactura(Factura factura) {
        return repository.save(factura);
    }

    @Override
    public Factura crearFacturaConItems(Factura factura, List<Factura.ItemFactura> items) {
        // Agregar items uno por uno usando el método público
        for (Factura.ItemFactura item : items) {
            factura.agregarItem(item);
        }
        return repository.save(factura);
    }

    @Override
    public Optional<Factura> obtenerPorId(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<Factura> listarTodas() {
        return repository.findAll();
    }

    @Override
    public boolean eliminar(Integer id) {
        return repository.deleteById(id);
    }

    @Override
    public Factura agregarServicio(Factura factura, Servicio servicio) {
        factura.agregarServicio(servicio);
        return repository.save(factura);
    }

    @Override
    public Factura agregarProducto(Factura factura, Inventario producto, BigDecimal cantidad) {
        factura.agregarProducto(producto, cantidad);
        return repository.save(factura);
    }

    @Override
    public Factura recalcularTotales(Factura factura) {
        // El recálculo se hace automáticamente al agregar items
        return repository.save(factura);
    }

    @Override
    public Factura generarFacturaPorConsulta(Integer consultaId) {
        // Buscar el historial médico de la consulta
        Optional<HistorialMedico> historialOpt = historialMedicoService.obtenerPorId(consultaId);
        if (historialOpt.isEmpty()) {
            throw new IllegalArgumentException("Consulta médica no encontrada: " + consultaId);
        }
        HistorialMedico historial = historialOpt.get();

        Factura factura = new Factura();
        factura.setFechaEmision(LocalDateTime.now());
        factura.setNumeroFactura("FACT-" + System.currentTimeMillis());

        // Obtener dueño ID desde la mascota del historial
        if (historial.getMascota() != null) {
            factura.setDuenoId(historial.getMascota().getDuenoId());
        } else {
            // Valor por defecto si no hay mascota asociada
            factura.setDuenoId(1);
        }

        // Agregar servicio de consulta usando Builder
        Factura.ItemFactura itemConsulta = new Factura.ItemFactura.Builder(Factura.ItemFactura.TipoItem.SERVICIO)
                .withDescripcion("Consulta médica: " + (historial.getDiagnostico() != null ? historial.getDiagnostico() : "Consulta general"))
                .withCantidad(BigDecimal.ONE)
                .withPrecioUnitario(new BigDecimal("30000"))
                .build();
        factura.agregarItem(itemConsulta);

        // Agregar medicamentos
        if (historial.getMedicamentosRecetados() != null && !historial.getMedicamentosRecetados().isBlank()) {
            String[] meds = historial.getMedicamentosRecetados().split(",");
            for (String med : meds) {
                Factura.ItemFactura itemMed = new Factura.ItemFactura.Builder(Factura.ItemFactura.TipoItem.PRODUCTO)
                        .withDescripcion("Medicamento: " + med.trim())
                        .withCantidad(BigDecimal.ONE)
                        .withPrecioUnitario(new BigDecimal("15000"))
                        .build();
                factura.agregarItem(itemMed);
            }
        }

        return repository.save(factura);
    }



}