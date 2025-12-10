package com.dpardo.strike.domain;

public class Liga {
    private int id;
    private String nombre;
    private String tipo;
    private String idPais; // Guardamos el código FIFA del país asociado

    // Constructor vacío
    public Liga() {
    }

    // Constructor completo
    public Liga(int id, String nombre, String tipo, String idPais) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.idPais = idPais;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getIdPais() { return idPais; }
    public void setIdPais(String idPais) { this.idPais = idPais; }

    @Override
    public String toString() {
        return nombre;
    }
}