/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.registro.model.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase de utilidad para generar un 'salt' y aplicar hashing
 * (SHA-256) a las contraseñas, siguiendo el estándar de la tabla Trabajadores.
 */

public class PasswordHasher {
    private static final Logger logger = LoggerFactory.getLogger(PasswordHasher.class);
    private static final int SALT_LENGTH = 16; // 16 bytes = 128 bits para el Salt

    /**
     * Genera una cadena de bytes aleatoria (Salt).
     * @return Array de bytes (Salt) de 16 bytes de longitud.
     */
    public static byte[] getSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Genera el hash de una contraseña usando un Salt específico.
     * @param password La contraseña en texto plano.
     * @param salt El salt generado.
     * @return Array de bytes (Hash) de la contraseña salteada.
     */
    public static byte[] getHash(String password, byte[] salt) {
        try {
            // Usamos SHA-256 (estándar para hashing)
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            // 1. Añadir el Salt al algoritmo
            md.update(salt);
            
            // 2. Generar el hash de la combinación (Salt + Password)
            byte[] hashedPassword = md.digest(password.getBytes());
            
            return hashedPassword;

        } catch (NoSuchAlgorithmException e) {
            // Este error solo ocurriría si el entorno Java no tiene SHA-256
            logger.error("Error crítico: Algoritmo de hashing no encontrado (SHA-256).", e);
            throw new RuntimeException("Error de configuración de seguridad.", e);
        }
    }

    /**
     * Convierte un array de bytes (Hash o Salt) a una cadena Base64
     * para facilitar la visualización en logs, si fuera necesario (aunque se guarda como VARBINARY).
     * @param bytes El array de bytes.
     * @return Cadena Base64.
     */
    public static String bytesToHex(byte[] bytes) {
        // En este proyecto, los DAOs lo manejarán como VARBINARY,
        // pero esta función es útil si lo necesitas como String para debug.
        return Base64.getEncoder().encodeToString(bytes);
    }
}
