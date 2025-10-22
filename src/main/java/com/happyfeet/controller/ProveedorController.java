package com.happyfeet.controller;

import com.happyfeet.model.entities.Proveedor;
import com.happyfeet.service.ProveedorService;
import com.happyfeet.util.FileLogger;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Controlador para gestión de proveedores del inventario
 */
public class ProveedorController {

    private static final FileLogger LOG = FileLogger.getInstance();
    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
        LOG.info("ProveedorController inicializado");
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            mostrarMenuProveedores();
            try {
                int opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1 -> registrarProveedorInteractivo(scanner);
                    case 2 -> listarProveedoresActivos();
                    case 3 -> buscarProveedorInteractivo(scanner);
                    case 4 -> actualizarProveedorInteractivo(scanner);
                    case 5 -> desactivarProveedorInteractivo(scanner);
                    case 6 -> listarProveedoresPorTipo(scanner);
                    case 7 -> mostrarDetallesProveedorInteractivo(scanner);
                    case 0 -> continuar = false;
                    default -> System.out.println("❌ Opción no válida");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Por favor ingrese un número válido");
            }
        }
    }

    private void mostrarMenuProveedores() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                   GESTIÓN DE PROVEEDORES                    ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.println("║ [1] Registrar nuevo proveedor                               ║");
        System.out.println("║ [2] Listar proveedores activos                              ║");
        System.out.println("║ [3] Buscar proveedor                                        ║");
        System.out.println("║ [4] Actualizar información de proveedor                     ║");
        System.out.println("║ [5] Desactivar proveedor                                    ║");
        System.out.println("║ [6] Listar proveedores por tipo                             ║");
        System.out.println("║ [7] Ver detalles de proveedor                               ║");
        System.out.println("║ [0] Volver al menú principal                                ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.print("Seleccione una opción: ");
    }

    private void registrarProveedorInteractivo(Scanner scanner) {
        try {
            System.out.println("\n=== REGISTRAR NUEVO PROVEEDOR ===");

            System.out.print("Nombre de la empresa: ");
            String nombre = scanner.nextLine().trim();
            if (nombre.isEmpty()) {
                System.out.println("❌ El nombre es obligatorio");
                return;
            }

            System.out.print("Nombre del contacto: ");
            String nombreContacto = scanner.nextLine().trim();

            System.out.print("Teléfono: ");
            String telefono = scanner.nextLine().trim();

            System.out.print("Email: ");
            String email = scanner.nextLine().trim();

            System.out.print("Dirección: ");
            String direccion = scanner.nextLine().trim();

            System.out.print("Ciudad: ");
            String ciudad = scanner.nextLine().trim();

            System.out.print("NIT/RUC: ");
            String nit = scanner.nextLine().trim();

            System.out.println("\nTipos de proveedor disponibles:");
            for (Proveedor.TipoProveedor tipo : Proveedor.TipoProveedor.values()) {
                System.out.println("- " + tipo.name() + ": " + tipo.getDescripcion());
            }
            System.out.print("Seleccione tipo (MEDICAMENTOS/VACUNAS/MATERIAL_MEDICO/ALIMENTOS/GENERAL): ");
            String tipoStr = scanner.nextLine().trim().toUpperCase();

            Proveedor.TipoProveedor tipo;
            try {
                tipo = Proveedor.TipoProveedor.valueOf(tipoStr);
            } catch (IllegalArgumentException e) {
                tipo = Proveedor.TipoProveedor.GENERAL;
                System.out.println("ℹ️ Tipo no válido, se asignó GENERAL por defecto");
            }

            Proveedor nuevoProveedor = Proveedor.builder()
                    .withNombre(nombre)
                    .withNombreContacto(nombreContacto.isEmpty() ? null : nombreContacto)
                    .withTelefono(telefono.isEmpty() ? null : telefono)
                    .withEmail(email.isEmpty() ? null : email)
                    .withDireccion(direccion.isEmpty() ? null : direccion)
                    .withCiudad(ciudad.isEmpty() ? null : ciudad)
                    .withNit(nit.isEmpty() ? null : nit)
                    .withTipo(tipo)
                    .build();

            Proveedor proveedorCreado = proveedorService.crearProveedor(nuevoProveedor);
            System.out.println("✅ Proveedor registrado exitosamente: " + proveedorCreado.getNombre());
            System.out.println("ID asignado: " + proveedorCreado.getId());

        } catch (Exception e) {
            System.out.println("❌ Error al registrar proveedor: " + e.getMessage());
            LOG.error("Error registrando proveedor", e);
        }
    }

    private void listarProveedoresActivos() {
        try {
            List<Proveedor> proveedores = proveedorService.listarProveedoresActivos();

            if (proveedores.isEmpty()) {
                System.out.println("ℹ️ No hay proveedores activos registrados");
                return;
            }

            System.out.println("\n=== PROVEEDORES ACTIVOS ===");
            System.out.printf("%-5s %-30s %-20s %-15s %-20s%n",
                "ID", "EMPRESA", "CONTACTO", "TELÉFONO", "TIPO");
            System.out.println("-".repeat(95));

            for (Proveedor proveedor : proveedores) {
                System.out.printf("%-5d %-30s %-20s %-15s %-20s%n",
                    proveedor.getId(),
                    truncateString(proveedor.getNombre(), 30),
                    truncateString(proveedor.getNombreContacto(), 20),
                    proveedor.getTelefono() != null ? proveedor.getTelefono() : "N/A",
                    proveedor.getTipo().getNombre());
            }

        } catch (Exception e) {
            System.out.println("❌ Error al listar proveedores: " + e.getMessage());
            LOG.error("Error listando proveedores activos", e);
        }
    }

    private void buscarProveedorInteractivo(Scanner scanner) {
        try {
            System.out.println("\n=== BUSCAR PROVEEDOR ===");
            System.out.println("[1] Buscar por ID");
            System.out.println("[2] Buscar por nombre");
            System.out.print("Seleccione opción: ");

            int opcion = Integer.parseInt(scanner.nextLine());
            switch (opcion) {
                case 1 -> {
                    System.out.print("Ingrese ID del proveedor: ");
                    Long id = Long.parseLong(scanner.nextLine());
                    Optional<Proveedor> proveedor = proveedorService.buscarPorId(id.intValue());
                    if (proveedor.isPresent()) {
                        mostrarDetallesProveedor(proveedor.get());
                    } else {
                        System.out.println("❌ Proveedor no encontrado");
                    }
                }
                case 2 -> {
                    System.out.print("Ingrese nombre o parte del nombre: ");
                    String nombre = scanner.nextLine().trim();
                    List<Proveedor> proveedores = proveedorService.buscarPorNombre(nombre);
                    if (proveedores.isEmpty()) {
                        System.out.println("❌ No se encontraron proveedores con ese nombre");
                    } else {
                        System.out.println("\n=== RESULTADOS DE BÚSQUEDA ===");
                        for (Proveedor p : proveedores) {
                            System.out.printf("ID: %d - %s (%s)%n",
                                p.getId(), p.getNombre(), p.getTipo().getNombre());
                        }
                    }
                }
                default -> System.out.println("❌ Opción no válida");
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Formato numérico inválido");
        } catch (Exception e) {
            System.out.println("❌ Error en la búsqueda: " + e.getMessage());
        }
    }

    private void actualizarProveedorInteractivo(Scanner scanner) {
        try {
            System.out.println("\n=== ACTUALIZAR PROVEEDOR ===");
            System.out.print("ID del proveedor a actualizar: ");
            Long id = Long.parseLong(scanner.nextLine());

            Optional<Proveedor> proveedorOpt = proveedorService.buscarPorId(id.intValue());
            if (proveedorOpt.isEmpty()) {
                System.out.println("❌ Proveedor no encontrado");
                return;
            }

            Proveedor proveedor = proveedorOpt.get();
            System.out.println("Proveedor actual: " + proveedor.getNombre());

            System.out.print("Nuevo nombre (actual: " + proveedor.getNombre() + ") [ENTER para mantener]: ");
            String nuevoNombre = scanner.nextLine().trim();
            if (!nuevoNombre.isEmpty()) {
                proveedor.setNombre(nuevoNombre);
            }

            System.out.print("Nuevo contacto (actual: " +
                (proveedor.getNombreContacto() != null ? proveedor.getNombreContacto() : "N/A") +
                ") [ENTER para mantener]: ");
            String nuevoContacto = scanner.nextLine().trim();
            if (!nuevoContacto.isEmpty()) {
                proveedor.setNombreContacto(nuevoContacto);
            }

            System.out.print("Nuevo teléfono (actual: " +
                (proveedor.getTelefono() != null ? proveedor.getTelefono() : "N/A") +
                ") [ENTER para mantener]: ");
            String nuevoTelefono = scanner.nextLine().trim();
            if (!nuevoTelefono.isEmpty()) {
                proveedor.setTelefono(nuevoTelefono);
            }

            System.out.print("Nuevo email (actual: " +
                (proveedor.getEmail() != null ? proveedor.getEmail() : "N/A") +
                ") [ENTER para mantener]: ");
            String nuevoEmail = scanner.nextLine().trim();
            if (!nuevoEmail.isEmpty()) {
                proveedor.setEmail(nuevoEmail);
            }

            Proveedor actualizado = proveedorService.actualizarProveedor(id.intValue(), proveedor);
            System.out.println("✅ Proveedor actualizado exitosamente");

        } catch (NumberFormatException e) {
            System.out.println("❌ Formato numérico inválido");
        } catch (Exception e) {
            System.out.println("❌ Error al actualizar proveedor: " + e.getMessage());
        }
    }

    private void desactivarProveedorInteractivo(Scanner scanner) {
        try {
            System.out.println("\n=== DESACTIVAR PROVEEDOR ===");
            System.out.print("ID del proveedor a desactivar: ");
            Long id = Long.parseLong(scanner.nextLine());

            Optional<Proveedor> proveedorOpt = proveedorService.buscarPorId(id.intValue());
            if (proveedorOpt.isEmpty()) {
                System.out.println("❌ Proveedor no encontrado");
                return;
            }

            Proveedor proveedor = proveedorOpt.get();
            System.out.println("Proveedor: " + proveedor.getNombre());
            System.out.println("Estado actual: " + (proveedor.isActivo() ? "ACTIVO" : "INACTIVO"));

            if (!proveedor.isActivo()) {
                System.out.println("ℹ️ El proveedor ya está inactivo");
                return;
            }

            System.out.print("¿Confirma la desactivación? (s/n): ");
            String confirmacion = scanner.nextLine().trim().toLowerCase();

            if (confirmacion.startsWith("s")) {
                proveedorService.cambiarEstado(id.intValue(), false);
                System.out.println("✅ Proveedor desactivado exitosamente");
            } else {
                System.out.println("ℹ️ Desactivación cancelada");
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Formato numérico inválido");
        } catch (Exception e) {
            System.out.println("❌ Error al desactivar proveedor: " + e.getMessage());
        }
    }

    private void listarProveedoresPorTipo(Scanner scanner) {
        try {
            System.out.println("\n=== LISTAR PROVEEDORES POR TIPO ===");
            System.out.println("Tipos disponibles:");
            for (Proveedor.TipoProveedor tipo : Proveedor.TipoProveedor.values()) {
                System.out.println("- " + tipo.name() + ": " + tipo.getDescripcion());
            }

            System.out.print("Seleccione tipo: ");
            String tipoStr = scanner.nextLine().trim().toUpperCase();

            try {
                Proveedor.TipoProveedor tipo = Proveedor.TipoProveedor.valueOf(tipoStr);
                List<Proveedor> proveedores = proveedorService.buscarPorTipo(tipo);

                if (proveedores.isEmpty()) {
                    System.out.println("ℹ️ No hay proveedores de tipo: " + tipo.getNombre());
                    return;
                }

                System.out.println("\n=== PROVEEDORES DE TIPO: " + tipo.getNombre() + " ===");
                for (Proveedor p : proveedores) {
                    System.out.printf("ID: %d - %s (%s) - %s%n",
                        p.getId(), p.getNombre(),
                        p.getNombreContacto() != null ? p.getNombreContacto() : "Sin contacto",
                        p.isActivo() ? "ACTIVO" : "INACTIVO");
                }

            } catch (IllegalArgumentException e) {
                System.out.println("❌ Tipo de proveedor no válido");
            }

        } catch (Exception e) {
            System.out.println("❌ Error al listar proveedores: " + e.getMessage());
        }
    }

    private void mostrarDetallesProveedorInteractivo(Scanner scanner) {
        try {
            System.out.println("\n=== DETALLES DE PROVEEDOR ===");
            System.out.print("ID del proveedor: ");
            Long id = Long.parseLong(scanner.nextLine());

            Optional<Proveedor> proveedorOpt = proveedorService.buscarPorId(id.intValue());
            if (proveedorOpt.isPresent()) {
                mostrarDetallesProveedor(proveedorOpt.get());
            } else {
                System.out.println("❌ Proveedor no encontrado");
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Formato numérico inválido");
        } catch (Exception e) {
            System.out.println("❌ Error al mostrar detalles: " + e.getMessage());
        }
    }

    private void mostrarDetallesProveedor(Proveedor proveedor) {
        System.out.println("\n=== INFORMACIÓN COMPLETA DEL PROVEEDOR ===");
        System.out.println("ID: " + proveedor.getId());
        System.out.println("Empresa: " + proveedor.getNombre());
        System.out.println("Contacto: " + (proveedor.getNombreContacto() != null ? proveedor.getNombreContacto() : "N/A"));
        System.out.println("Teléfono: " + (proveedor.getTelefono() != null ? proveedor.getTelefono() : "N/A"));
        System.out.println("Email: " + (proveedor.getEmail() != null ? proveedor.getEmail() : "N/A"));
        System.out.println("Dirección: " + (proveedor.getDireccion() != null ? proveedor.getDireccion() : "N/A"));
        System.out.println("Ciudad: " + (proveedor.getCiudad() != null ? proveedor.getCiudad() : "N/A"));
        System.out.println("NIT/RUC: " + (proveedor.getNit() != null ? proveedor.getNit() : "N/A"));
        System.out.println("Tipo: " + proveedor.getTipo().getNombre() + " - " + proveedor.getTipo().getDescripcion());
        System.out.println("Estado: " + (proveedor.isActivo() ? "ACTIVO" : "INACTIVO"));
        System.out.println("Fecha registro: " + proveedor.getFechaRegistro().toLocalDate());
        System.out.println("Última actualización: " + proveedor.getFechaActualizacion().toLocalDate());
    }

    private String truncateString(String str, int maxLength) {
        if (str == null) return "N/A";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }

    // Getters para testing
    public ProveedorService getProveedorService() {
        return proveedorService;
    }
}