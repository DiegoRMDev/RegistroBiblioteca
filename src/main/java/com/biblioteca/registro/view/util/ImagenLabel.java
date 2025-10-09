/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.registro.view.util;

import java.awt.Image;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ImagenLabel {
    public static void pintarImagen(JLabel lbl, String ruta) {
        // Usamos variables locales, ya no las de instancia (imagen, icono)
        ImageIcon imagen = new ImageIcon(ruta); 
        
        Icon icono = new ImageIcon(
            imagen.getImage().getScaledInstance(
                lbl.getWidth(), 
                lbl.getHeight(), 
                Image.SCALE_SMOOTH
            )
        );
        
        lbl.setIcon(icono);
        // Nota: No se llama a this.repaint() aquí porque no estamos en el Frame/Componente.
        // La actualización de la GUI ocurre cuando el Frame se hace visible o repinta.
    }
}

