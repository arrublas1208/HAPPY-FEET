package com.happyfeet.view;

import com.happyfeet.model.entities.Inventario;
import com.happyfeet.service.InventarioService;

import java.util.List;
import java.util.Scanner;

public class InventarioView {

    private final InventarioService inventarioService;

    public InventarioView(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    public void mostrarMenu() {
        Scanner scanner = new Scanner(System.in);
        int opcion;
        do {
            System.out.println("\n--- Menú Inventario y Farmacia ---");
            System.out.println("1. Ver productos por debajo del stock mínimo");
            System.out.println("2. Ver productos próximos a vencer");
            System.out.println("3. Registrar proveedor");
            System.out.println("4. Consultar proveedores");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1 -> mostrarProductosBajoStock();
                case 2 -> mostrarProductosProximosAVencer();
                case 3 -> registrarProveedor();
                case 4 -> consultarProveedores();
                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción inválida.");
            }
        } while (opcion != 0);
    }

    private void mostrarProductosBajoStock() {
        List<Inventario> productos = inventarioService.obtenerProductosBajoStockMinimo();
        System.out.println("\nProductos por debajo del stock mínimo:");
        productos.forEach(p -> System.out.println(p.getNombreProducto() + " - Stock: " + p.getCantidadStock()));
    }

    private void mostrarProductosProximosAVencer() {
        List<Inventario> productos = inventarioService.obtenerProductosProximosAVencer();
        System.out.println("\nProductos próximos a vencer:");
        productos.forEach(p -> System.out.println(p.getNombreProducto() + " - Vence: " + p.getFechaVencimiento()));
    }

    private void registrarProveedor() {
        // Implementa aquí la lógica para registrar proveedor usando ProveedorService
        System.out.println("Funcionalidad de registro de proveedor (pendiente de implementación).");
    }

    private void consultarProveedores() {
        // Implementa aquí la lógica para consultar proveedores usando ProveedorService
        System.out.println("Funcionalidad de consulta de proveedores (pendiente de implementación).");
    }
}