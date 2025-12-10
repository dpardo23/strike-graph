package com.dpardo.strike.domain;

public class SexoComboItem {
    private char codigo;
    private String descripcion;

    public SexoComboItem(char codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public char getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}