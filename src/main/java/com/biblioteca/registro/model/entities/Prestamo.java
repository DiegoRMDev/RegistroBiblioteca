/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.registro.model.entities;

import java.sql.Timestamp;
import java.util.List;
/**
 * Representa la tabla 'dbo.Prestamos' en la base de datos.
 * Contiene la cabecera del préstamo.
 */
public class Prestamo {
    private int prestamoId;
    private Timestamp fechaPrestamo;
    private Timestamp fechaDevolucionPrevista;
    private Timestamp fechaDevolucionReal;
    private String estado;
    
    // 2. Relaciones (Claves Foráneas)
    private Lector lector;
    private Trabajador trabajador;
    
    // 3. Relación de cabecera-detalle
    private List<PrestamoDetalle> detalles; 

    public Prestamo() {
    }

    public int getPrestamoId() {
        return prestamoId;
    }

    public void setPrestamoId(int prestamoId) {
        this.prestamoId = prestamoId;
    }

    public Timestamp getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(Timestamp fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public Timestamp getFechaDevolucionPrevista() {
        return fechaDevolucionPrevista;
    }

    public void setFechaDevolucionPrevista(Timestamp fechaDevolucionPrevista) {
        this.fechaDevolucionPrevista = fechaDevolucionPrevista;
    }

    public Timestamp getFechaDevolucionReal() {
        return fechaDevolucionReal;
    }

    public void setFechaDevolucionReal(Timestamp fechaDevolucionReal) {
        this.fechaDevolucionReal = fechaDevolucionReal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Lector getLector() {
        return lector;
    }

    public void setLector(Lector lector) {
        this.lector = lector;
    }

    public Trabajador getTrabajador() {
        return trabajador;
    }

    public void setTrabajador(Trabajador trabajador) {
        this.trabajador = trabajador;
    }

    public List<PrestamoDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<PrestamoDetalle> detalles) {
        this.detalles = detalles;
    }
    
    @Override
    public String toString() {
        String dev = fechaDevolucionReal != null ? fechaDevolucionReal.toString() : "PENDIENTE";
        return "Préstamo #" + prestamoId + " (" + estado + ") - Dev. " + dev;
    }
    
}
