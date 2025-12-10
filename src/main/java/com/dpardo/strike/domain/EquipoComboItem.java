package com.dpardo.strike.domain;

public class EquipoComboItem {
    private int id;
    private String nombre;

    public EquipoComboItem(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre; // Esto es lo que muestra el ComboBox
    }
}