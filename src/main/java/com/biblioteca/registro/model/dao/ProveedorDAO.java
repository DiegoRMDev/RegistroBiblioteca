/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.registro.model.dao;

import com.biblioteca.registro.model.entities.Proveedor;
import com.biblioteca.registro.model.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProveedorDAO {
    private static final Logger logger = LoggerFactory.getLogger(ProveedorDAO.class);
    private final String SELECT_ALL_SQL = "SELECT ProveedorID, Nombre, Direccion, Telefono, Email FROM dbo.Proveedores ORDER BY Nombre";

    /**
     * Recupera todos los proveedores disponibles en la base de datos.
     * @return Una lista de objetos Proveedor.
     */
    public List<Proveedor> findAll() {
        List<Proveedor> proveedores = new ArrayList<>();
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            
            logger.info("Ejecutando consulta: {}", SELECT_ALL_SQL);

            while (rs.next()) {
                // Mapear la fila del ResultSet al objeto Proveedor
                Proveedor proveedor = new Proveedor();
                proveedor.setProveedorId(rs.getInt("ProveedorID"));
                proveedor.setNombre(rs.getString("Nombre"));
                proveedor.setDireccion(rs.getString("Direccion"));
                proveedor.setTelefono(rs.getString("Telefono"));
                proveedor.setEmail(rs.getString("Email"));
                proveedores.add(proveedor);
            }
            logger.info("Se recuperaron {} proveedores de la base de datos.", proveedores.size());

        } catch (SQLException e) {
            logger.error("Error SQL al intentar obtener todos los proveedores: {}", e.getMessage(), e);
        }
        return proveedores;
    }
}
