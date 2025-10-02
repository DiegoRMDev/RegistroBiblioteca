/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.registro.service;

import com.biblioteca.registro.model.dao.CategoriaDAO;
import com.biblioteca.registro.model.dao.LibroDAO;
import com.biblioteca.registro.model.entities.Libro;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio encargado de la lógica de negocio para la gestión de Libros.
 * Orquesta las llamadas al LibroDAO y CategoriaDAO.
 */
public class LibroService {
    private static final Logger logger = LoggerFactory.getLogger(LibroService.class);
    private final LibroDAO libroDAO;
    private final CategoriaDAO categoriaDAO; 

    public LibroService() {
        this.libroDAO = new LibroDAO();
        this.categoriaDAO = new CategoriaDAO(); // Necesario para gestionar datos relacionados (ej. combos en la vista)
    }

    // =========================================================================
    // 1. REGISTRO DE LIBRO
    // =========================================================================

    /**
     * Valida y registra un nuevo libro, incluyendo sus autores, de forma transaccional.
     * @param libro El objeto Libro a registrar.
     * @return true si el registro fue exitoso, false en caso contrario.
     */
    public boolean registrarLibro(Libro libro) {
        // Regla de Negocio 1: El ISBN debe ser proporcionado
        if (libro.getIsbn() == null || libro.getIsbn().trim().isEmpty()) {
            logger.error("Error de Negocio: El ISBN no puede estar vacío.");
            return false;
        }
        
        // Regla de Negocio 2: Debe tener al menos una categoría asignada
        if (libro.getCategoria() == null || libro.getCategoria().getCategoriaId() <= 0) {
            logger.error("Error de Negocio: El libro debe tener una categoría válida.");
            return false;
        }
        
        // Regla de Negocio 3: Debe tener al menos un autor asociado
        if (libro.getAutores() == null || libro.getAutores().isEmpty()) {
            logger.error("Error de Negocio: El libro debe tener al menos un autor.");
            return false;
        }

        // Delegar la operación transaccional al DAO
        return libroDAO.insertar(libro);
    }

    // =========================================================================
    // 2. CONSULTA DE LIBROS
    // =========================================================================

    /**
     * Recupera todos los libros con sus autores y categorías asociadas.
     * @return Lista de objetos Libro completamente poblados.
     */
    public List<Libro> obtenerTodosLosLibros() {
        return libroDAO.findAll();
    }
      // Aquí se incluirían otros métodos (actualizar, eliminar, buscar por ISBN, etc.)
    
}
