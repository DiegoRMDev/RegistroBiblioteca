/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.biblioteca.registro.view.util;

import java.awt.Image;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

//Clase para agregar imagenes desde un Label
public class ImagenLabel {
    public static void pintarImagen(JLabel lbl, String ruta) {
        
        ImageIcon imagen = new ImageIcon(ruta); 
        
        Icon icono = new ImageIcon(
            imagen.getImage().getScaledInstance(
                lbl.getWidth(), 
                lbl.getHeight(), 
                Image.SCALE_SMOOTH
            )
        );
        
        lbl.setIcon(icono);
        
    }
}

