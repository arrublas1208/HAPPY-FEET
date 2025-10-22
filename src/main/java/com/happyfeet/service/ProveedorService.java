package com.happyfeet.service;

import com.happyfeet.model.entities.Proveedor;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de proveedores del inventario.
 * Parte integral del módulo de Inventario y Farmacia según requerimientos.
 */
public interface ProveedorService {

    /**
     * Crear un nuevo proveedor
     */
    Proveedor crearProveedor(Proveedor proveedor);

    /**
     * Actualizar datos de un proveedor existente
     */
    Proveedor actualizarProveedor(Integer id, Proveedor cambios);

    /**
     * Eliminar un proveedor (marcarlo como inactivo)
     */
    void eliminarProveedor(Integer id);

    /**
     * Buscar proveedor por ID
     */
    Optional<Proveedor> buscarPorId(Integer id);

    /**
     * Listar todos los proveedores activos
     */
    List<Proveedor> listarProveedoresActivos();

    /**
     * Listar todos los proveedores (activos e inactivos)
     */
    List<Proveedor> listarTodos();

    /**
     * Buscar proveedores por nombre
     */
    List<Proveedor> buscarPorNombre(String nombre);

    /**
     * Buscar proveedores por tipo
     */
    List<Proveedor> buscarPorTipo(Proveedor.TipoProveedor tipo);

    /**
     * Buscar proveedores por ciudad
     */
    List<Proveedor> buscarPorCiudad(String ciudad);

    /**
     * Verificar si existe un proveedor con el NIT dado
     */
    boolean existePorNit(String nit);

    /**
     * Activar/desactivar proveedor
     */
    void cambiarEstado(Integer id, boolean activo);
}