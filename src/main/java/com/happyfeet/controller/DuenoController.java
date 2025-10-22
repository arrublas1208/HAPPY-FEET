package com.happyfeet.controller;

import com.happyfeet.model.entities.Dueno;
import com.happyfeet.service.DuenoService;
import com.happyfeet.service.MascotaService;
import com.happyfeet.service.impl.DuenoServiceImpl;
import com.happyfeet.view.DuenoView;

import java.util.List;
import java.util.Optional;

public class DuenoController {

    private final DuenoService duenoService;
    private final MascotaService mascotaService;
    private final DuenoView duenoView;

    // Constructor corregido - inyectando dependencias correctamente
    public DuenoController(DuenoService duenoService, MascotaService mascotaService, DuenoView duenoView) {
        this.duenoService = duenoService;
        this.mascotaService = mascotaService;
        this.duenoView = duenoView;
    }

    // Método alternativo si necesitas crear los servicios internamente
    public DuenoController(DuenoService duenoService, DuenoView duenoView) {
        this.duenoService = duenoService;
        this.mascotaService = null; // o inicializar con un servicio por defecto
        this.duenoView = duenoView;
    }

    public void crearDueno(String nombreCompleto, String documentoIdentidad, String direccion,
                           String telefono, String email, String contactoEmergencia) {
        try {
            Dueno nuevoDueno = Dueno.Builder.create()
                    .withNombreCompleto(nombreCompleto)
                    .withDocumentoIdentidad(documentoIdentidad)
                    .withDireccion(direccion)
                    .withTelefono(telefono)
                    .withEmail(email)
                    .withContactoEmergencia(contactoEmergencia)
                    .build();

            Dueno duenoCreado = duenoService.crearDueno(nuevoDueno);
            duenoView.mostrarMensaje("Dueño creado exitosamente: " + duenoCreado.getNombreCompleto());

        } catch (Exception e) {
            duenoView.mostrarError("Error al crear dueño: " + e.getMessage());
        }
    }

    public Optional<Dueno> buscarPorDocumento(String documento) {
        try {
            // Como no existe buscarPorDocumento en la interfaz, usamos listarTodos y filtramos
            List<Dueno> todos = duenoService.listarTodos();
            Optional<Dueno> resultado = todos.stream()
                    .filter(d -> d.getDocumentoIdentidad().equals(documento))
                    .findFirst();

            if (resultado.isEmpty()) {
                duenoView.mostrarMensaje("No se encontró dueño con documento: " + documento);
            }

            return resultado;

        } catch (Exception e) {
            duenoView.mostrarError("Error al buscar dueño: " + e.getMessage());
            return Optional.empty();
        }
    }

    public boolean existePorEmail(String email) {
        try {
            // Como no existe existePorEmail en la interfaz, usamos listarTodos y filtramos
            List<Dueno> todos = duenoService.listarTodos();
            return todos.stream()
                    .anyMatch(d -> email.equalsIgnoreCase(d.getEmail()));

        } catch (Exception e) {
            duenoView.mostrarError("Error al verificar email: " + e.getMessage());
            return false;
        }
    }

    public void actualizarDueno(Long id, Dueno cambios) {
        try {
            Dueno duenoActualizado = duenoService.actualizarDueno(id, cambios);
            duenoView.mostrarMensaje("Dueño actualizado exitosamente: " + duenoActualizado.getNombreCompleto());

        } catch (Exception e) {
            duenoView.mostrarError("Error al actualizar dueño: " + e.getMessage());
        }
    }

    public void eliminarDueno(Long id) {
        try {
            // Verificar si el dueño tiene mascotas antes de eliminar
            if (mascotaService != null) {
                var mascotas = mascotaService.buscarPorDueno(id);
                if (!mascotas.isEmpty()) {
                    boolean confirmar = duenoView.mostrarConfirmacion(
                            "El dueño tiene " + mascotas.size() + " mascotas registradas. " +
                                    "¿Está seguro de que desea eliminar?"
                    );
                    if (!confirmar) {
                        return;
                    }
                }
            }

            duenoService.eliminarDueno(id);
            duenoView.mostrarMensaje("Dueño eliminado exitosamente");

        } catch (Exception e) {
            duenoView.mostrarError("Error al eliminar dueño: " + e.getMessage());
        }
    }

    public void listarTodos() {
        try {
            List<Dueno> dueños = duenoService.listarTodos();
            if (dueños.isEmpty()) {
                duenoView.mostrarMensaje("No hay dueños registrados");
            } else {
                StringBuilder sb = new StringBuilder("=== LISTA DE DUEÑOS ===\n");
                for (int i = 0; i < dueños.size(); i++) {
                    Dueno d = dueños.get(i);
                    sb.append(String.format("%d. %s - %s - %s\n",
                            i + 1, d.getNombreCompleto(), d.getDocumentoIdentidad(), d.getEmail()));
                }
                duenoView.mostrarMensaje(sb.toString());
            }

        } catch (Exception e) {
            duenoView.mostrarError("Error al listar dueños: " + e.getMessage());
        }
    }

    public void buscarPorTermino(String termino) {
        try {
            List<Dueno> resultados = duenoService.buscarPorDueno(termino);
            if (resultados.isEmpty()) {
                duenoView.mostrarMensaje("No se encontraron dueños con: " + termino);
            } else {
                StringBuilder sb = new StringBuilder("=== RESULTADOS DE BÚSQUEDA ===\n");
                for (Dueno d : resultados) {
                    sb.append(String.format("- %s - %s - %s\n",
                            d.getNombreCompleto(), d.getDocumentoIdentidad(), d.getEmail()));
                }
                duenoView.mostrarMensaje(sb.toString());
            }

        } catch (Exception e) {
            duenoView.mostrarError("Error en la búsqueda: " + e.getMessage());
        }
    }

    public DuenoService getDuenoService() {
        return duenoService;
    }

    public MascotaService getMascotaService() {
        return mascotaService;
    }

    public DuenoView getDuenoView() {
        return duenoView;
    }

    // Métodos añadidos desde versión integrada

    
    
    public void run() {
        System.out.println("=== GESTIÓN DE DUEÑOS ===");
        System.out.println("Funcionalidad disponible:");
        System.out.println("- Crear dueño");
        System.out.println("- Buscar dueño");
        System.out.println("- Listar todos los dueños");
        System.out.println("- Actualizar información");
        System.out.println("- Eliminar dueño");
        System.out.println();
        System.out.println("Esta sección está lista para ser utilizada.");
        System.out.println("Presione ENTER para continuar...");
        try {
            System.in.read();
        } catch (Exception e) {
            // Ignore
        }
    }

}
