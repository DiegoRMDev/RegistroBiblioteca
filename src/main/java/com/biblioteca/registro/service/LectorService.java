/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.registro.service;

import com.biblioteca.registro.model.dao.LectorDAO;
import com.biblioteca.registro.model.entities.Lector;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio encargado de la lógica de negocio para la gestión de Lectores.
 * Aplica validaciones de negocio antes de la inserción y orquesta las llamadas al LectorDAO.
 */

public class LectorService {
     private static final Logger logger = LoggerFactory.getLogger(LectorService.class);
    private final LectorDAO lectorDAO; 

    public LectorService() {
        this.lectorDAO = new LectorDAO();
    }

    // =========================================================================
    // 1. REGISTRO DE LECTOR
    // =========================================================================

    /**
     * Valida las reglas de negocio y registra un nuevo lector.
     * @param lector El objeto Lector a registrar.
     * @return true si el registro fue exitoso, false en caso contrario.
     */
    public boolean registrarLector(Lector lector) {
        // Regla de Negocio 1: El DNI debe ser obligatorio y tener el formato correcto (8 dígitos).
        if (lector.getDni() == null || lector.getDni().trim().length() != 8 || !lector.getDni().matches("\\d{8}")) {
            logger.error("Error de Negocio: El DNI es obligatorio y debe contener exactamente 8 dígitos numéricos.");
            return false;
        }
        
        // Regla de Negocio 2: El Nombre es obligatorio.
        if (lector.getNombre() == null || lector.getNombre().trim().isEmpty()) {
            logger.error("Error de Negocio: El Nombre del lector es obligatorio.");
            return false;
        }

        // Delegar la operación al DAO (El DAO manejará la restricción UNIQUE del DNI)
        return lectorDAO.insertar(lector);
    }

    // =========================================================================
    // 2. CONSULTA DE LECTORES
    // =========================================================================

    /**
     * Recupera la lista completa de todos los lectores.
     * @return Lista de objetos Lector.
     */
    public List<Lector> obtenerTodosLosLectores() {
        return lectorDAO.findAll();
    }
    
    // (Aquí se incluirían métodos para buscar por DNI, obtener historial de préstamos, etc., que se añadirán más adelante)
}
