package com.dpardo.strike.domain;

public class Pais {
    private String codFifa;
    private String nombre;
    private String continente;

    public Pais(String codFifa, String nombre, String continente) {
        this.codFifa = codFifa;
        this.nombre = nombre;
        this.continente = continente;
    }

    public String getCodFifa() {
        return codFifa;
    }

    public void setCodFifa(String codFifa) {
        this.codFifa = codFifa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContinente() {
        return continente;
    }

    public void setContinente(String continente) {
        this.continente = continente;
    }
}