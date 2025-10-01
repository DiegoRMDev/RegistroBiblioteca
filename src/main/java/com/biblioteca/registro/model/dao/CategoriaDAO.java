/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.registro.model.dao;

import com.biblioteca.registro.model.entities.Categoria;
import com.biblioteca.registro.model.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CategoriaDAO {
     private static final Logger logger = LoggerFactory.getLogger(CategoriaDAO.class);
    private final String SELECT_ALL_SQL = "SELECT CategoriaID, Nombre, Descripcion FROM dbo.Categorias";

    /**
     * Recupera todas las categorías disponibles en la base de datos.
     * @return Una lista de objetos Categoria.
     */
    public List<Categoria> findAll() {
        List<Categoria> categorias = new ArrayList<>();
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            
            logger.info("Ejecutando consulta: {}", SELECT_ALL_SQL);

            while (rs.next()) {
                // Mapear la fila del ResultSet al objeto Categoria
                Categoria categoria = new Categoria();
                categoria.setCategoriaId(rs.getInt("CategoriaID"));
                categoria.setNombre(rs.getString("Nombre"));
                categoria.setDescripcion(rs.getString("Descripcion"));
                categorias.add(categoria);
            }
            logger.info("Se recuperaron {} categorías de la base de datos.", categorias.size());

        } catch (SQLException e) {
            // Loguear cualquier error de SQL
            logger.error("Error SQL al intentar obtener todas las categorías: {}", e.getMessage(), e);
        }
        return categorias;
    }
}
