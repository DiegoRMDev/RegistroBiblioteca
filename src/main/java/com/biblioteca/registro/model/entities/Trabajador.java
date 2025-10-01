/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.registro.model.entities;

import java.sql.Timestamp;

public class Trabajador {
    // 1. Atributos
    private int trabajadorId;
    private String nombre;
    private String usuarioLogin;
    
    // Almacenados como array de bytes (VARBINARY en SQL Server)
    private byte[] contrasenaHash;
    private byte[] salt;
    
    // Relación con la entidad Rol
    private Rol rol;
    private Timestamp fechaRegistro;

    public Trabajador() {
    }

    public Trabajador(int trabajadorId, String nombre, String usuarioLogin, byte[] contrasenaHash, byte[] salt, Rol rol, Timestamp fechaRegistro) {
        this.trabajadorId = trabajadorId;
        this.nombre = nombre;
        this.usuarioLogin = usuarioLogin;
        this.contrasenaHash = contrasenaHash;
        this.salt = salt;
        this.rol = rol;
        this.fechaRegistro = fechaRegistro;
    }

    public int getTrabajadorId() {
        return trabajadorId;
    }

    public void setTrabajadorId(int trabajadorId) {
        this.trabajadorId = trabajadorId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuarioLogin() {
        return usuarioLogin;
    }

    public void setUsuarioLogin(String usuarioLogin) {
        this.usuarioLogin = usuarioLogin;
    }

    public byte[] getContrasenaHash() {
        return contrasenaHash;
    }

    public void setContrasenaHash(byte[] contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    
    @Override
    public String toString() {
        return nombre + " (" + usuarioLogin + ")";
    }
}
