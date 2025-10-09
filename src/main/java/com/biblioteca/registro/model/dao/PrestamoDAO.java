/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.registro.model.dao;

import com.biblioteca.registro.model.entities.Prestamo;
import com.biblioteca.registro.model.entities.PrestamoDetalle;
import com.biblioteca.registro.model.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase DAO para gestionar el acceso a datos de las tablas dbo.Prestamos y dbo.PrestamoDetalle.
 */

public class PrestamoDAO {
     private static final Logger logger = LoggerFactory.getLogger(PrestamoDAO.class);

    // Consulta para insertar la cabecera del Préstamo
    private final String INSERT_PRESTAMO_SQL = "INSERT INTO dbo.Prestamos "
            + "(LectorID, TrabajadorID, FechaDevolucionPrevista, Estado) "
            + "VALUES (?, ?, ?, ?)";
    
    // Consulta para insertar el detalle del Préstamo
    private final String INSERT_DETALLE_SQL = "INSERT INTO dbo.PrestamoDetalle "
            + "(PrestamoID, LibroID, Cantidad) VALUES (?, ?, ?)";
    
    // Consulta para actualizar la devolución (usada para Devolución)
    private final String UPDATE_DEVOLUCION_SQL = "UPDATE dbo.Prestamos "
            + "SET FechaDevolucionReal = ?, Estado = ? WHERE PrestamoID = ?";
    
    
    // =========================================================================
    // 1. REGISTRO DE PRÉSTAMO (Cabecera + Detalle)
    // =========================================================================

    /**
     * Registra un nuevo préstamo y sus detalles asociados en una transacción.
     * @param prestamo El objeto Prestamo que contiene la cabecera y los detalles.
     * @return true si el registro fue exitoso, false en caso contrario.
     */
    public boolean registrarPrestamo(Prestamo prestamo) {
        Connection conn = null;
        boolean exito = false;
        
        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false); // 🚨 Iniciar la Transacción

            // 1. Insertar la Cabecera del Préstamo
            try (PreparedStatement psPrestamo = conn.prepareStatement(INSERT_PRESTAMO_SQL, Statement.RETURN_GENERATED_KEYS)) {
                
                psPrestamo.setInt(1, prestamo.getLector().getLectorId());
                psPrestamo.setInt(2, prestamo.getTrabajador().getTrabajadorId());
                psPrestamo.setTimestamp(3, prestamo.getFechaDevolucionPrevista());
                psPrestamo.setString(4, prestamo.getEstado() != null ? prestamo.getEstado() : "Pendiente");
                
                int affectedRows = psPrestamo.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new SQLException("La inserción del préstamo falló, no se obtuvieron filas afectadas.");
                }

                // Obtener el ID generado (PrestamoID)
                try (ResultSet generatedKeys = psPrestamo.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        prestamo.setPrestamoId(generatedKeys.getInt(1)); // Asignar el ID al objeto Java
                    } else {
                        throw new SQLException("La inserción del préstamo falló, no se obtuvo el ID generado.");
                    }
                }
            }

            // 2. Insertar los Detalles (Libros)
            if (prestamo.getDetalles() != null && !prestamo.getDetalles().isEmpty()) {
                try (PreparedStatement psDetalle = conn.prepareStatement(INSERT_DETALLE_SQL)) {
                    for (PrestamoDetalle detalle : prestamo.getDetalles()) {
                        psDetalle.setInt(1, prestamo.getPrestamoId()); // El ID que acabamos de generar
                        psDetalle.setInt(2, detalle.getLibro().getLibroId());
                        psDetalle.setInt(3, detalle.getCantidad());
                        psDetalle.addBatch(); // Añadir a un lote de inserciones
                    }
                    psDetalle.executeBatch(); // Ejecutar todas las inserciones del detalle
                }
            }
            
            conn.commit(); // Commit final: todo fue bien
            exito = true;
            logger.info("Préstamo registrado exitosamente. ID de Préstamo: {}", prestamo.getPrestamoId());

        } catch (SQLException e) {
            // Rollback: algo falló (posiblemente por stock insuficiente, chequeado por el trigger)
            if (conn != null) {
                try {
                    conn.rollback();
                    logger.warn("Transacción de préstamo revertida debido a un error de SQL: {}", e.getMessage());
                } catch (SQLException ex) {
                    logger.error("Error al intentar realizar rollback: {}", ex.getMessage());
                }
            }
            logger.error("Error crítico durante la inserción del préstamo: {}", e.getMessage(), e);
        } finally {
            // Restaurar el autoCommit a true
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    logger.error("Error al restaurar autoCommit: {}", e.getMessage());
                }
            }
        }
        return exito;
    }
    
    // =========================================================================
    // 2. ACTUALIZAR DEVOLUCIÓN
    // =========================================================================
    
    /**
     * Marca un préstamo como devuelto en la base de datos. 
     * El trigger asociado manejará la actualización de stock y multas.
     * @param prestamoId ID del préstamo a devolver.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean registrarDevolucion(int prestamoId) {
        // Obtenemos la fecha y hora actual para registrar la devolución
        Timestamp fechaActual = new Timestamp(System.currentTimeMillis());
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_DEVOLUCION_SQL)) {
            
            
            ps.setTimestamp(1, fechaActual);
            ps.setString(2, "Devuelto"); // Marcador, el trigger lo afinará.
            ps.setInt(3, prestamoId);
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Devolución registrada exitosamente para el Préstamo ID: {}", prestamoId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error SQL al intentar registrar la devolución del Préstamo ID {}: {}", prestamoId, e.getMessage(), e);
        }
        return false;
    }
}
