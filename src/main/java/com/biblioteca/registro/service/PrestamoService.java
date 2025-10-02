/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.registro.service;

import com.biblioteca.registro.model.dao.LibroDAO;
import com.biblioteca.registro.model.dao.LectorDAO;
import com.biblioteca.registro.model.dao.PrestamoDAO;
import com.biblioteca.registro.model.entities.Lector;
import com.biblioteca.registro.model.entities.Prestamo;
import com.biblioteca.registro.model.entities.PrestamoDetalle;
import com.biblioteca.registro.model.entities.Trabajador;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio encargado de la lógica de negocio para la gestión de Préstamos y Devoluciones.
 * Contiene validaciones cruciales (stock, límites).
 */

public class PrestamoService {
    private static final Logger logger = LoggerFactory.getLogger(PrestamoService.class);
    private final PrestamoDAO prestamoDAO;
    private final LectorDAO lectorDAO; 
    private final LibroDAO libroDAO; 

    // Regla de Negocio: Máximo 7 días de préstamo por defecto
    private static final int DIAS_MAXIMOS_PRESTAMO = 7;
    // Regla de Negocio: Límite de 5 libros por préstamo
    private static final int MAX_LIBROS_POR_PRESTAMO = 5;

    public PrestamoService() {
        this.prestamoDAO = new PrestamoDAO();
        this.lectorDAO = new LectorDAO();
        this.libroDAO = new LibroDAO();
    }
    
    // =========================================================================
    // 1. UTILIDAD: CÁLCULO DE FECHA
    // =========================================================================
    
    /**
     * Establece la Fecha de Devolución Prevista según la política de la biblioteca (7 días).
     * @return Timestamp de la fecha de devolución.
     */
    public Timestamp establecerFechaDevolucionPrevista() {
        // Obtenemos la fecha actual, sumamos los días y convertimos a Timestamp para SQL
        LocalDateTime fechaPrevista = LocalDateTime.now().plusDays(DIAS_MAXIMOS_PRESTAMO);
        return Timestamp.valueOf(fechaPrevista);
    }

    // =========================================================================
    // 2. REGISTRO DE PRÉSTAMO
    // =========================================================================

    /**
     * Aplica reglas de negocio y registra un préstamo de forma transaccional.
     * @param prestamo El objeto Prestamo con Lector, Trabajador y Detalles (Libros).
     * @return true si el registro fue exitoso, false en caso contrario.
     */
    public boolean registrarPrestamo(Prestamo prestamo) {
        // --- 1. Validaciones Preliminares de Datos ---
        
        if (prestamo == null || prestamo.getLector() == null || prestamo.getTrabajador() == null 
                || prestamo.getDetalles() == null || prestamo.getDetalles().isEmpty()) {
            logger.error("Error de Negocio: Datos incompletos para registrar el préstamo (Lector, Trabajador o Detalles faltantes).");
            return false;
        }

        List<PrestamoDetalle> detalles = prestamo.getDetalles();
        
        // --- 2. Validación de Regla de Negocio (Límite de Cantidad) ---
        
        if (detalles.size() > MAX_LIBROS_POR_PRESTAMO) {
            logger.error("Error de Negocio: Se intenta prestar más del límite permitido ({} libros).", MAX_LIBROS_POR_PRESTAMO);
            return false;
        }

        // --- 3. Validación de Stock y Disponibilidad de cada libro ---
        
        int cantidadTotal = 0;
        for (PrestamoDetalle detalle : detalles) {
            cantidadTotal += detalle.getCantidad();
            
            if (detalle.getLibro() == null || detalle.getLibro().getLibroId() <= 0) {
                 logger.error("Error de Negocio: Un detalle del préstamo no tiene un LibroID válido.");
                 return false;
            }
            
            // Nota: La verificación de stock final se realizará en el trigger de SQL Server.
            // Aquí se podría hacer una verificación inicial, pero confiamos en la atomicidad de la BD.
        }
        
        if (cantidadTotal == 0) {
            logger.error("Error de Negocio: La cantidad total de libros a prestar es cero.");
            return false;
        }
        
        // --- 4. Establecer las Fechas y Estado ---
        prestamo.setFechaDevolucionPrevista(establecerFechaDevolucionPrevista());
        prestamo.setEstado("Pendiente"); 

        // --- 5. Delegar la Inserción Transaccional al DAO ---
        // El PrestamoDAO manejará la inserción de cabecera y detalles en una sola transacción,
        // incluyendo el rollback si el trigger de la BD falla por falta de stock.
        
        boolean exito = prestamoDAO.registrarPrestamo(prestamo);
        
        if (exito) {
            logger.info("Préstamo registrado por el servicio para el Lector ID: {}", prestamo.getLector().getLectorId());
        } else {
            logger.error("Fallo el registro transaccional del préstamo (posiblemente falta de stock o error de BD).");
        }
        
        return exito;
    }

    // =========================================================================
    // 3. REGISTRO DE DEVOLUCIÓN
    // =========================================================================
    
    /**
     * Marca un préstamo como devuelto. El trigger de la BD actualiza stock y aplica multas si es necesario.
     * @param prestamoId El ID del préstamo a devolver.
     * @return true si la operación fue exitosa.
     */
    public boolean registrarDevolucion(int prestamoId) {
        if (prestamoId <= 0) {
            logger.error("Error de Negocio: ID de préstamo inválido para la devolución.");
            return false;
        }
        
        // La lógica de negocio más compleja (cálculo de multa, actualización de stock)
        // está delegada a la base de datos (triggers) para garantizar la consistencia atómica.
        
        return prestamoDAO.registrarDevolucion(prestamoId);
    }
}
