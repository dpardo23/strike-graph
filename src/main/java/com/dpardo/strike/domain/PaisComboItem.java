package com.dpardo.strike.domain;

public class PaisComboItem {
    private String codFifa;
    private String nombre;

    public PaisComboItem(String codFifa, String nombre) {
        this.codFifa = codFifa;
        this.nombre = nombre;
    }

    public String getCodFifa() {
        return codFifa;
    }

    @Override
    public String toString() {
        return nombre;
    }
}