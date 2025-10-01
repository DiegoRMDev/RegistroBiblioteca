/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.registro.model.dao;

import com.biblioteca.registro.model.entities.Autor;
import com.biblioteca.registro.model.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutorDAO {
    private static final Logger logger = LoggerFactory.getLogger(AutorDAO.class);
    private final String SELECT_ALL_SQL = "SELECT AutorID, Nombre, Apellido, Nacionalidad FROM dbo.Autores ORDER BY Apellido, Nombre";

    /**
     * Recupera todos los autores disponibles en la base de datos.
     * @return Una lista de objetos Autor.
     */
    public List<Autor> findAll() {
        List<Autor> autores = new ArrayList<>();
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            
            logger.info("Ejecutando consulta: {}", SELECT_ALL_SQL);

            while (rs.next()) {
                // Mapear la fila del ResultSet al objeto Autor
                Autor autor = new Autor();
                autor.setAutorId(rs.getInt("AutorID"));
                autor.setNombre(rs.getString("Nombre"));
                autor.setApellido(rs.getString("Apellido"));
                autor.setNacionalidad(rs.getString("Nacionalidad"));
                autores.add(autor);
            }
            logger.info("Se recuperaron {} autores de la base de datos.", autores.size());

        } catch (SQLException e) {
            logger.error("Error SQL al intentar obtener todos los autores: {}", e.getMessage(), e);
        }
        return autores;
    }
}
