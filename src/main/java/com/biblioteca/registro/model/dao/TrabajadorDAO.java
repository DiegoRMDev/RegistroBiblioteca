/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.registro.model.dao;

import com.biblioteca.registro.model.entities.Rol;
import com.biblioteca.registro.model.entities.Trabajador;
import com.biblioteca.registro.model.util.DBConnection;
import com.biblioteca.registro.model.util.PasswordHasher;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrabajadorDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(TrabajadorDAO.class);

    private final String INSERT_SQL = "INSERT INTO dbo.Trabajadores (Nombre, UsuarioLogin, ContrasenaHash, Salt, RolID) VALUES (?, ?, ?, ?, ?)";
    private final String SELECT_BY_USER_SQL = "SELECT t.TrabajadorID, t.Nombre, t.UsuarioLogin, t.ContrasenaHash, t.Salt, t.FechaRegistro, r.RolID, r.NombreRol "
                                            + "FROM dbo.Trabajadores t JOIN dbo.Roles r ON t.RolID = r.RolID WHERE t.UsuarioLogin = ?";

    // =========================================================================
    // 1. REGISTRO DE TRABAJADOR (Para crear cuentas de administrador/empleado)
    // =========================================================================
    
    /**
     * Registra un nuevo trabajador en la base de datos.
     * @param trabajador El objeto Trabajador a registrar.
     * @param password La contraseña en texto plano (se hashea internamente).
     * @return true si el registro fue exitoso, false en caso contrario.
     */
    public boolean registrar(Trabajador trabajador, String password) {
        // 1. Generar Salt y Hash antes de la conexión
        byte[] salt = PasswordHasher.getSalt();
        byte[] hash = PasswordHasher.getHash(password, salt);

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, trabajador.getNombre());
            ps.setString(2, trabajador.getUsuarioLogin());
            ps.setBytes(3, hash); // ContrasenaHash
            ps.setBytes(4, salt); // Salt
            ps.setInt(5, trabajador.getRol().getRolId()); // RolID
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                // Opcional: Recuperar el ID generado por la BD
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        trabajador.setTrabajadorId(generatedKeys.getInt(1));
                    }
                }
                logger.info("Trabajador registrado exitosamente. ID: {}", trabajador.getTrabajadorId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error SQL al intentar registrar trabajador: {}", e.getMessage(), e);
        }
        return false;
    }

    // =========================================================================
    // 2. INICIO DE SESIÓN (LOGIN)
    // =========================================================================

    /**
     * Intenta autenticar a un trabajador usando su usuario y contraseña.
     * @param usuarioLogin El nombre de usuario.
     * @param password La contraseña en texto plano.
     * @return El objeto Trabajador si la autenticación es exitosa, o null si falla.
     */
    public Trabajador login(String usuarioLogin, String password) {
        Trabajador trabajador = null;

        // 1. Buscar al trabajador para obtener el Salt y Hash almacenados
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_USER_SQL)) {
            
            ps.setString(1, usuarioLogin);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // 2. Mapear los datos de la BD
                    trabajador = mapResultSetToTrabajador(rs);

                    // 3. Obtener el Hash y Salt
                    byte[] storedHash = trabajador.getContrasenaHash();
                    byte[] storedSalt = trabajador.getSalt();
                    
                    // 4. Calcular el Hash de la contraseña ingresada con el Salt almacenado
                    byte[] inputHash = PasswordHasher.getHash(password, storedSalt);

                    // 5. Comparar: si los hashes son idénticos, la autenticación es exitosa
                    if (Arrays.equals(storedHash, inputHash)) {
                        logger.info("Login exitoso para el usuario: {}", usuarioLogin);
                        return trabajador;
                    } else {
                        logger.warn("Fallo de login para {}: Contraseña incorrecta.", usuarioLogin);
                        return null; // Contraseña incorrecta
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error SQL durante el proceso de login para {}: {}", usuarioLogin, e.getMessage(), e);
        }
        logger.warn("Fallo de login: Usuario '{}' no encontrado.", usuarioLogin);
        return null; // Usuario no encontrado o error SQL
    }
    
    // =========================================================================
    // 3. Método de Mapeo (Helper)
    // =========================================================================

    private Trabajador mapResultSetToTrabajador(ResultSet rs) throws SQLException {
        Trabajador t = new Trabajador();
        t.setTrabajadorId(rs.getInt("TrabajadorID"));
        t.setNombre(rs.getString("Nombre"));
        t.setUsuarioLogin(rs.getString("UsuarioLogin"));
        t.setContrasenaHash(rs.getBytes("ContrasenaHash"));
        t.setSalt(rs.getBytes("Salt"));
        t.setFechaRegistro(rs.getTimestamp("FechaRegistro"));
        
        // Mapear la entidad Rol asociada
        Rol rol = new Rol();
        rol.setRolId(rs.getInt("RolID"));
        rol.setNombreRol(rs.getString("NombreRol"));
        t.setRol(rol);
        
        return t;
    }
    
}
