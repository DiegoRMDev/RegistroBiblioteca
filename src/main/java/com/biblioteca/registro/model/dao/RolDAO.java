/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.registro.model.dao;

import com.biblioteca.registro.model.entities.Rol;
import com.biblioteca.registro.model.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase DAO para gestionar el acceso a datos de la tabla dbo.Roles.
 */

public class RolDAO {
      private static final Logger logger = LoggerFactory.getLogger(RolDAO.class);
    private final String SELECT_ALL_SQL = "SELECT RolID, NombreRol FROM dbo.Roles";

    /**
     * Recupera todos los roles disponibles en la base de datos.
     * @return Una lista de objetos Rol.
     */
    public List<Rol> findAll() {
        List<Rol> roles = new ArrayList<>();
        
        // El 'try-with-resources' asegura que la conexión y el PreparedStatement se cierren automáticamente
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) { // Ejecuta la consulta
            
            logger.info("Ejecutando consulta: {}", SELECT_ALL_SQL);

            while (rs.next()) {
                // Mapear la fila del ResultSet al objeto Rol
                Rol rol = new Rol();
                rol.setRolId(rs.getInt("RolID")); // Coincide con el nombre de la columna SQL
                rol.setNombreRol(rs.getString("NombreRol")); // Coincide con el nombre de la columna SQL
                roles.add(rol);
            }
            logger.info("Se recuperaron {} roles de la base de datos.", roles.size());

        } catch (SQLException e) {
            // Loguear cualquier error de SQL
            logger.error("Error SQL al intentar obtener todos los roles: {}", e.getMessage(), e);
        }
        return roles;
    }
}
