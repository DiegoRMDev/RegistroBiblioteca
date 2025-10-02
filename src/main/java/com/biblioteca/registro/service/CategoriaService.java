/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.registro.service;

import com.biblioteca.registro.model.dao.CategoriaDAO;
import com.biblioteca.registro.model.entities.Categoria;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio encargado de la lógica de negocio para la gestión de Categorías.
 * Actúa principalmente como intermediario del CategoriaDAO para la UI.
 */

public class CategoriaService {
     private static final Logger logger = LoggerFactory.getLogger(CategoriaService.class);
    private final CategoriaDAO categoriaDAO; 

    public CategoriaService() {
        this.categoriaDAO = new CategoriaDAO();
    }

    // =========================================================================
    // 1. CONSULTA DE CATEGORÍAS
    // =========================================================================

    /**
     * Recupera la lista completa de todas las categorías.
     * @return Lista de objetos Categoria.
     */
    public List<Categoria> obtenerTodasLasCategorias() {
        return categoriaDAO.findAll();
    }
    
    // Aquí se podrían agregar métodos para insertar, actualizar o eliminar categorías
    // si el requerimiento funcional lo solicita, aplicando validaciones mínimas (ej. nombre no vacío).
}
