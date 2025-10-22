package com.happyfeet.model.entities;

public enum Sexo {
    MACHO("Macho"),
    HEMBRA("Hembra");

    private final String nombre;

    Sexo(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public String getName() {
        return nombre;
    }
}