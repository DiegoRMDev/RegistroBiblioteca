/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.registro.model.entities;


public class Categoria {
    
    private int categoriaId;
    private String nombre;
    private String descripcion;

    public Categoria() {
    }

    public Categoria(int categoriaId, String nombre, String descripcion) {
        this.categoriaId = categoriaId;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

     // Opcional: toString para depuración
    @Override
    public String toString() {
        return nombre; // Útil si quieres que un ComboBox muestre el nombre directamente
    }
}
