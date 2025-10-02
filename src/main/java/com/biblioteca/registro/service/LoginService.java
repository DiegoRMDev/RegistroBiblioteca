/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.registro.service;

import com.biblioteca.registro.model.dao.TrabajadorDAO;
import com.biblioteca.registro.model.entities.Trabajador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio encargado de la lógica de autenticación de trabajadores.
 * Orquesta la llamada al TrabajadorDAO.
 */
public class LoginService {
    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);
    private final TrabajadorDAO trabajadorDAO;

    // Constructor que inicializa el DAO
    public LoginService() {
        this.trabajadorDAO = new TrabajadorDAO();
    }

    /**
     * Intenta autenticar a un trabajador usando el DAO.
     * @param usuario El nombre de usuario.
     * @param password La contraseña en texto plano.
     * @return El objeto Trabajador si las credenciales son válidas, o null si falla.
     */
    public Trabajador autenticar(String usuario, String password) {
        if (usuario == null || usuario.trim().isEmpty() || password == null || password.isEmpty()) {
            logger.warn("Intento de login fallido: Usuario o contraseña no proporcionados.");
            return null;
        }

        // 1. Convertir el usuario a minúsculas o sanitizar si es necesario (el DAO ya lo maneja con el DB check)
        String usuarioNormalizado = usuario.trim(); 
        
        // 2. Llamar al DAO para la verificación de credenciales (que incluye el hashing)
        Trabajador trabajador = trabajadorDAO.login(usuarioNormalizado, password);

        if (trabajador != null) {
            logger.info("Autenticación exitosa para el trabajador: {}", trabajador.getNombre());
        } else {
            logger.warn("Autenticación fallida para el usuario: {}", usuarioNormalizado);
        }
        
        return trabajador;
    }
}
