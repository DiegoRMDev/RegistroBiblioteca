/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.registro.model.entities;

/**
 * Representa la tabla 'dbo.PrestamoDetalle' en la base de datos.
 * Contiene los libros y cantidades de un préstamo.
 */
public class PrestamoDetalle {
    private int detalleId;
    private int cantidad;
    
     private Libro libro;// El libro prestado

    public PrestamoDetalle() {
    }

    public int getDetalleId() {
        return detalleId;
    }

    public void setDetalleId(int detalleId) {
        this.detalleId = detalleId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }
     
    @Override
    public String toString() {
        return cantidad + " x " + (libro != null ? libro.getTitulo() : "Libro Desconocido");
    }
}
