/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.registro.model.dao;

import com.biblioteca.registro.model.entities.Autor;
import com.biblioteca.registro.model.entities.Libro;
import com.biblioteca.registro.model.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LibroDAO {
    private static final Logger logger = LoggerFactory.getLogger(LibroDAO.class);

    // Consulta para insertar un nuevo libro
    private final String INSERT_LIBRO_SQL = "INSERT INTO dbo.Libros "
            + "(ISBN, Titulo, Editorial, AnioPublicacion, CategoriaID, Idioma, UbicacionFisica, RutaImagen, Estado, Stock) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    // Consulta para insertar la relación Libro-Autor (tabla intermedia)
    private final String INSERT_AUTOR_LIBRO_SQL = "INSERT INTO dbo.LibroAutores (LibroID, AutorID, OrdenAutor) VALUES (?, ?, ?)";
    
    /**
     * Inserta un nuevo Libro y sus Autores asociados en una transacción.
     * @param libro El objeto Libro a insertar.
     * @return true si la inserción fue exitosa, false en caso contrario.
     */
    public boolean insertar(Libro libro) {
        Connection conn = null;
        boolean exito = false;
        
        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false); // 🚨 Iniciar la Transacción

            // 1. Insertar el Libro principal
            try (PreparedStatement psLibro = conn.prepareStatement(INSERT_LIBRO_SQL, Statement.RETURN_GENERATED_KEYS)) {
                
                psLibro.setString(1, libro.getIsbn());
                psLibro.setString(2, libro.getTitulo());
                psLibro.setString(3, libro.getEditorial());
                psLibro.setInt(4, libro.getAnioPublicacion());
                psLibro.setInt(5, libro.getCategoria().getCategoriaId()); // Usar el ID de la Categoría
                psLibro.setString(6, libro.getIdioma());
                psLibro.setString(7, libro.getUbicacionFisica());
                psLibro.setString(8, libro.getRutaImagen());
                psLibro.setString(9, libro.getEstado() != null ? libro.getEstado() : "Activo");
                psLibro.setInt(10, libro.getStock());
                
                int affectedRows = psLibro.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new SQLException("La inserción del libro falló, no se obtuvieron filas afectadas.");
                }

                // Obtener el ID generado (LibroID)
                try (ResultSet generatedKeys = psLibro.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        libro.setLibroId(generatedKeys.getInt(1)); // Asignar el ID al objeto Java
                    } else {
                        throw new SQLException("La inserción del libro falló, no se obtuvo el ID generado.");
                    }
                }
            }

            // 2. Insertar los Autores asociados (tabla LibroAutores)
            if (libro.getAutores() != null && !libro.getAutores().isEmpty()) {
                int orden = 1;
                try (PreparedStatement psAutor = conn.prepareStatement(INSERT_AUTOR_LIBRO_SQL)) {
                    for (Autor autor : libro.getAutores()) {
                        psAutor.setInt(1, libro.getLibroId()); // El ID que acabamos de generar
                        psAutor.setInt(2, autor.getAutorId());
                        psAutor.setInt(3, orden++);
                        psAutor.addBatch(); // Añadir a un lote de inserciones
                    }
                    psAutor.executeBatch(); // Ejecutar todas las inserciones de autores
                }
            }
            
            conn.commit(); // ✅ Commit final: todo fue bien
            exito = true;
            logger.info("Libro y Autores asociados insertados exitosamente. ID de Libro: {}", libro.getLibroId());

        } catch (SQLException e) {
            // 🚨 Rollback: algo falló, deshacer todo
            if (conn != null) {
                try {
                    conn.rollback();
                    logger.warn("Transacción revertida debido a un error de SQL: {}", e.getMessage());
                } catch (SQLException ex) {
                    logger.error("Error al intentar realizar rollback: {}", ex.getMessage());
                }
            }
            logger.error("Error crítico durante la inserción del libro: {}", e.getMessage(), e);
        } finally {
            // Restaurar el autoCommit a true y cerrar la conexión
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    logger.error("Error al restaurar autoCommit: {}", e.getMessage());
                }
                // Nota: La conexión se cerrará automáticamente si se usa try-with-resources
                // con el DAO, pero aquí la manejamos manualmente porque establecimos setAutoCommit(false).
                // Sin embargo, si DBConnection.getInstance().getConnection() devuelve una conexión nueva,
                // no necesitamos cerrarla manualmente si el DAO no la crea.
                // Asumiendo que DBConnection usa Singleton (como lo implementamos), no la cerramos aquí.
            }
        }
        return exito;
    }
}
