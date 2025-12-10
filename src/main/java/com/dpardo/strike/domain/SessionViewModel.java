package com.dpardo.strike.domain;

import java.sql.Timestamp;

public class SessionViewModel {
    private int pid;
    private String nombreUsuario;
    private String correo;
    private Timestamp fecCreacionUsuario;
    private String nombreRol;
    private String codComponenteUi;
    private String direccionIp;
    private int puerto;
    private Timestamp fechaAsignacionRol;
    private boolean rolActivo;

    public SessionViewModel(int pid, String nombreUsuario, String correo, Timestamp fecCreacionUsuario,
                            String nombreRol, String codComponenteUi, String direccionIp, int puerto,
                            Timestamp fechaAsignacionRol, boolean rolActivo) {
        this.pid = pid;
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.fecCreacionUsuario = fecCreacionUsuario;
        this.nombreRol = nombreRol;
        this.codComponenteUi = codComponenteUi;
        this.direccionIp = direccionIp;
        this.puerto = puerto;
        this.fechaAsignacionRol = fechaAsignacionRol;
        this.rolActivo = rolActivo;
    }

    // Getters necesarios para que el TableView de JavaFX pueda leer las propiedades
    // (JavaFX usa reflexión buscando getNombrePropiedad)

    public int getPid() { return pid; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getCorreo() { return correo; }
    public Timestamp getFecCreacionUsuario() { return fecCreacionUsuario; }
    public String getNombreRol() { return nombreRol; }
    public String getCodComponenteUi() { return codComponenteUi; }
    public String getDireccionIp() { return direccionIp; }
    public int getPuerto() { return puerto; }
    public Timestamp getFechaAsignacionRol() { return fechaAsignacionRol; }
    public boolean isRolActivo() { return rolActivo; }
}