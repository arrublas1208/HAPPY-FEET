package com.happyfeet.service.impl;

import com.happyfeet.model.entities.Dueno;
import com.happyfeet.model.entities.Factura;
import com.happyfeet.model.entities.Inventario;
import com.happyfeet.model.entities.Servicio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Façade de construcción de Factura que envuelve el builder interno de la entidad
 * y agrega soporte para un patrón Composite de items. Usa BigDecimal para cantidades y precios.
 */
public class FacturaBuilder {

    private final Factura.Builder core;
    private final List<FacturaItemComponent> components = new ArrayList<>();

    private FacturaBuilder() {
        this.core = new Factura.Builder();
    }

    public static FacturaBuilder begin() {
        return new FacturaBuilder();
    }

    // Delegación de propiedades principales de la factura
    public FacturaBuilder withDuenoId(Integer duenoId) {
        core.withDuenoId(duenoId);
        return this;
    }

    public FacturaBuilder withDueno(Dueno dueno) {
        core.withDueno(dueno);
        return this;
    }

    public FacturaBuilder withFechaEmision(LocalDateTime fecha) {
        core.withFechaEmision(fecha);
        return this;
    }

    public FacturaBuilder withObservaciones(String observaciones) {
        core.withObservaciones(observaciones);
        return this;
    }

    // API de items (Composite)
    public FacturaBuilder addServicio(Servicio servicio) {
        components.add(new ServicioLeaf(servicio));
        return this;
    }

    public FacturaBuilder addProducto(Inventario producto, BigDecimal cantidad) {
        components.add(new ProductoLeaf(producto, cantidad));
        return this;
    }

    public FacturaBuilder addComponent(FacturaItemComponent component) {
        components.add(Objects.requireNonNull(component, "component no puede ser null"));
        return this;
    }

    public FacturaBuilder addComponents(List<FacturaItemComponent> comps) {
        if (comps != null) {
            for (FacturaItemComponent c : comps) {
                addComponent(c);
            }
        }
        return this;
    }

    public Factura build() {
        Factura factura = core.build();
        // Materializar todos los componentes como items concretos y agregarlos
        for (FacturaItemComponent c : components) {
            for (Factura.ItemFactura item : c.materialize()) {
                factura.agregarItem(item); // este método recalcula los totales de forma interna
            }
        }
        return factura;
    }

    // =================== Composite ===================
    public interface FacturaItemComponent {
        List<Factura.ItemFactura> materialize();
    }

    public static class ServicioLeaf implements FacturaItemComponent {
        private final Servicio servicio;

        public ServicioLeaf(Servicio servicio) {
            this.servicio = Objects.requireNonNull(servicio, "servicio no puede ser null");
        }

        @Override
        public List<Factura.ItemFactura> materialize() {
            Factura.ItemFactura item = new Factura.ItemFactura.Builder(Factura.ItemFactura.TipoItem.SERVICIO)
                    .withServicio(servicio)
                    .build();
            return Collections.singletonList(item);
        }
    }

    public static class ProductoLeaf implements FacturaItemComponent {
        private final Inventario producto;
        private final BigDecimal cantidad;

        public ProductoLeaf(Inventario producto, BigDecimal cantidad) {
            this.producto = Objects.requireNonNull(producto, "producto no puede ser null");
            this.cantidad = Objects.requireNonNull(cantidad, "cantidad no puede ser null");
        }

        @Override
        public List<Factura.ItemFactura> materialize() {
            Factura.ItemFactura item = new Factura.ItemFactura.Builder(Factura.ItemFactura.TipoItem.PRODUCTO)
                    .withProducto(producto, cantidad)
                    .build();
            return Collections.singletonList(item);
        }
    }

    public static class GrupoComposite implements FacturaItemComponent {
        private final List<FacturaItemComponent> hijos = new ArrayList<>();

        public GrupoComposite add(FacturaItemComponent... components) {
            if (components != null) {
                for (FacturaItemComponent c : components) {
                    if (c != null) hijos.add(c);
                }
            }
            return this;
        }

        @Override
        public List<Factura.ItemFactura> materialize() {
            List<Factura.ItemFactura> items = new ArrayList<>();
            for (FacturaItemComponent c : hijos) {
                items.addAll(c.materialize());
            }
            return items;
        }
    }
}
