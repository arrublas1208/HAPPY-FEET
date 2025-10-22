package com.happyfeet.repository;

/**
 * Excepción de acceso a datos para no enmascarar errores SQL en repositorios.
 * Es unchecked para no contaminar firmas y permitir propagación hasta la capa UI.
 */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message) {
        super(message);
    }
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}