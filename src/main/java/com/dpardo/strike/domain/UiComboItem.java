package com.dpardo.strike.domain;

public class UiComboItem {
    private int id;
    private String codigoComponente;
    private String descripcion;

    public UiComboItem(int id, String codigoComponente, String descripcion) {
        this.id = id;
        this.codigoComponente = codigoComponente;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public String getCodigoComponente() {
        return codigoComponente;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return codigoComponente + " - " + descripcion;
    }
}