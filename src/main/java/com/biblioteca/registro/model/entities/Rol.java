/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.registro.model.entities;

/**
 * Representa la tabla 'dbo.Roles' en la base de datos.
 */

public class Rol {
    
    private int rolId;
    private String nombreRol;

    public Rol() {
    }

    public Rol(int rolId, String nombreRol) {
        this.rolId = rolId;
        this.nombreRol = nombreRol;
    }

    public int getRolId() {
        return rolId;
    }

    public void setRolId(int rolId) {
        this.rolId = rolId;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }
    
    // Opcional: toString para facilitar la depuración
    @Override
    public String toString() {
        return "Rol{" +
                "rolId=" + rolId +
                ", nombreRol='" + nombreRol + '\'' +
                '}';
    }
    
}
