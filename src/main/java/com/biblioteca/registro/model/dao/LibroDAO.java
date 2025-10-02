/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.registro.model.dao;

import com.biblioteca.registro.model.entities.Autor;
import com.biblioteca.registro.model.entities.Categoria;
import com.biblioteca.registro.model.entities.Libro;
import com.biblioteca.registro.model.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    
    // Consulta para obtener todos los libros con sus autores y categorías
    private final String FIND_ALL_LIBROS_SQL = "SELECT "
            + "l.LibroID, l.ISBN, l.Titulo, l.Editorial, l.AnioPublicacion, "
            + "l.Idioma, l.UbicacionFisica, l.RutaImagen, l.Estado, l.Stock, "
            + "c.CategoriaID, c.Nombre AS NombreCat, c.Descripcion AS DescripcionCat, " // Usamos alias para evitar conflictos
            + "a.AutorID, a.Nombre AS NombreAut, a.Apellido AS ApellidoAut, la.OrdenAutor " // Usamos alias para evitar conflictos
            + "FROM dbo.Libros l "
            + "INNER JOIN dbo.Categorias c ON l.CategoriaID = c.CategoriaID "
            + "LEFT JOIN dbo.LibroAutores la ON l.LibroID = la.LibroID "
            + "LEFT JOIN dbo.Autores a ON la.AutorID = a.AutorID "
            + "ORDER BY l.LibroID ASC, la.OrdenAutor ASC";


    /**
     * Recupera todos los Libros de la base de datos, incluyendo su Categoria
     * y la lista completa de Autores.
     * @return Una lista de objetos Libro completamente mapeados.
     */
    public List<Libro> findAll() {
        // Usamos un mapa para almacenar los libros por ID y evitar duplicados.
        Map<Integer, Libro> librosMap = new HashMap<>();
        Connection conn = null;
        
        try {
            conn = DBConnection.getInstance().getConnection();
            try (Statement statement = conn.createStatement();
                 ResultSet rs = statement.executeQuery(FIND_ALL_LIBROS_SQL)) {
                
                while (rs.next()) {
                    int libroId = rs.getInt("LibroID");
                    
                    // 1. Crear/Obtener el objeto Libro del mapa
                    Libro libro = librosMap.get(libroId);
                    if (libro == null) {
                        // El libro no existe en el mapa, lo creamos y lo agregamos.
                        libro = new Libro();
                        libro.setLibroId(libroId);
                        libro.setIsbn(rs.getString("ISBN"));
                        libro.setTitulo(rs.getString("Titulo"));
                        libro.setEditorial(rs.getString("Editorial"));
                        libro.setAnioPublicacion(rs.getInt("AnioPublicacion"));
                        libro.setIdioma(rs.getString("Idioma"));
                        libro.setUbicacionFisica(rs.getString("UbicacionFisica"));
                        libro.setRutaImagen(rs.getString("RutaImagen"));
                        libro.setEstado(rs.getString("Estado"));
                        libro.setStock(rs.getInt("Stock"));
                        
                        // Mapear la Categoria
                        Categoria categoria = new Categoria();
                        categoria.setCategoriaId(rs.getInt("CategoriaID"));
                        categoria.setNombre(rs.getString("NombreCat")); 
                        categoria.setDescripcion(rs.getString("DescripcionCat"));
                        libro.setCategoria(categoria);
                        
                        // Inicializar la lista de Autores
                        libro.setAutores(new ArrayList<>());
                        
                        librosMap.put(libroId, libro);
                    }
                    
                    // 2. Mapear el Autor (Si existe)
                    int autorId = rs.getInt("AutorID");
                    if (autorId > 0) { // Solo si la fila tiene un AutorID (evita nulos de LEFT JOIN)
                        Autor autor = new Autor();
                        autor.setAutorId(autorId);
                        autor.setNombre(rs.getString("NombreAut")); 
                        autor.setApellido(rs.getString("ApellidoAut")); 
                        // Opcional: Establecer el orden si es necesario para el front-end
                        // autor.setOrden(rs.getInt("OrdenAutor")); 
                        
                        // Agregar el Autor al Libro
                        libro.getAutores().add(autor);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error al ejecutar findAll en LibroDAO: {}", e.getMessage(), e);
        }
        
        // 3. Devolver la lista final de Libros
        return new ArrayList<>(librosMap.values());
    }
}
