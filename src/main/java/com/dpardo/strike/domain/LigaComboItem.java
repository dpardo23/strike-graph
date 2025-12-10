package com.dpardo.strike.domain;

public class LigaComboItem {
    private int id;
    private String nombre;

    public LigaComboItem(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return nombre;
    }
}