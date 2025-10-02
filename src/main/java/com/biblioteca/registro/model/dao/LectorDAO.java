/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.registro.model.dao;

import com.biblioteca.registro.model.entities.Lector;
import com.biblioteca.registro.model.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LectorDAO {
    private static final Logger logger = LoggerFactory.getLogger(LectorDAO.class);
    private final String INSERT_SQL = "INSERT INTO dbo.Lectores (DNI, Nombre, Direccion, Telefono, Email) VALUES (?, ?, ?, ?, ?)";
    private final String SELECT_ALL_SQL = "SELECT LectorID, DNI, Nombre, Direccion, Telefono, Email, FechaRegistro FROM dbo.Lectores ORDER BY Nombre";

    /**
     * Registra un nuevo lector en la base de datos.
     * @param lector El objeto Lector a registrar.
     * @return true si el registro fue exitoso, false en caso contrario.
     */
    public boolean insertar(Lector lector) {
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, lector.getDni());
            ps.setString(2, lector.getNombre());
            ps.setString(3, lector.getDireccion());
            ps.setString(4, lector.getTelefono());
            ps.setString(5, lector.getEmail());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        lector.setLectorId(generatedKeys.getInt(1));
                    }
                }
                logger.info("Lector registrado exitosamente. ID: {}", lector.getLectorId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error SQL al intentar registrar lector: {}", e.getMessage(), e);
            // Manejar errores de UNIQUE CONSTRAINT (DNI duplicado)
            if (e.getMessage().contains("UNIQUE KEY constraint 'UQ__Lectores__C031CF349F5A48F7'")) {
                logger.warn("El DNI ya se encuentra registrado.");
            }
        }
        return false;
    }

    /**
     * Recupera todos los lectores disponibles en la base de datos.
     * @return Una lista de objetos Lector.
     */
    public List<Lector> findAll() {
        List<Lector> lectores = new ArrayList<>();
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                lectores.add(mapResultSetToLector(rs));
            }
            logger.info("Se recuperaron {} lectores de la base de datos.", lectores.size());

        } catch (SQLException e) {
            logger.error("Error SQL al intentar obtener todos los lectores: {}", e.getMessage(), e);
        }
        return lectores;
    }
    
    /**
     * Método helper para mapear un ResultSet a un objeto Lector.
     */
    private Lector mapResultSetToLector(ResultSet rs) throws SQLException {
        Lector lector = new Lector();
        lector.setLectorId(rs.getInt("LectorID"));
        lector.setDni(rs.getString("DNI"));
        lector.setNombre(rs.getString("Nombre"));
        lector.setDireccion(rs.getString("Direccion"));
        lector.setTelefono(rs.getString("Telefono"));
        lector.setEmail(rs.getString("Email"));
        lector.setFechaRegistro(rs.getTimestamp("FechaRegistro"));
        return lector;
    }
}
