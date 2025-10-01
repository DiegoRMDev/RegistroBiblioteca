/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.registro.model.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DIEGO
 */
public class DBConnection {
   // Usamos SLF4J para el logging (requerido por JasperReports)
    private static final Logger logger = LoggerFactory.getLogger(DBConnection.class);

    // Patrón Singleton: Única instancia de la conexión
    private static DBConnection instance = null;
    private Connection connection = null;

    // --- CONFIGURACIÓN DE TU SQL SERVER  ---
    private static final String SERVER_NAME = "DESKTOP-P9EGVCF"; // Nombre de servidor
    private static final String DATABASE_NAME = "RegistroBibliotecaDB"; //Nombre de base de datos
    private static final String USER = "user_biblioteca"; // Tu usuario SQL Server 
    private static final String PASS = "TheChipis"; // Tu contraseña 

    // Definición de la URL de conexión con parámetros de seguridad
    private static final String DB_URL = "jdbc:sqlserver://" + SERVER_NAME + ";databaseName=" + DATABASE_NAME + 
                                       ";encrypt=true;trustServerCertificate=true;";

    // Constructor privado (solo puede ser llamado internamente)
    private DBConnection() {
        try {
            this.connection = DriverManager.getConnection(DB_URL, USER, PASS);
            logger.info("Conexión a la base de datos establecida con éxito.");

        } catch (SQLException e) {
            // Usamos logger.error() para que el log sepa que es un fallo crítico.
            logger.error("Error al conectar a la base de datos. Verifique: URL, usuario, contraseña y la activación de TCP/IP.", e);
        }
    }

    // Método estático para obtener la instancia única (Singleton)
    public static DBConnection getInstance() {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) {
                    instance = new DBConnection();
                }
            }
        }
        return instance;
    }

    // Método que devuelve el objeto Connection para ser usado por los DAOs
    public Connection getConnection() {
        return this.connection;
    }

    // Método para cerrar la conexión al finalizar la aplicación
    public void closeConnection() {
        if (this.connection != null) {
            try {
                this.connection.close();
                logger.info("Conexión a la base de datos cerrada.");
            } catch (SQLException e) {
                logger.error("Error al cerrar la conexión.", e);
            }
        }
    }
}
