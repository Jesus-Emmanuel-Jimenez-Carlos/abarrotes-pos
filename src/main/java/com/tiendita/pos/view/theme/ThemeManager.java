package com.tiendita.pos.view.theme;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import java.awt.*;

/**
 * Gestor de temas visuales del sistema POS (Soporte nativo Claro/Oscuro).
 */
public class ThemeManager {
    private static boolean isDarkMode = true; // Por defecto moderno en Oscuro

    // Colores primarios HSL adaptados para interfaz premium
    public static final Color COLOR_PRIMARY = new Color(37, 99, 235);    // Azul Eléctrico (#2563EB)
    public static final Color COLOR_SUCCESS = new Color(5, 150, 105);    // Esmeralda (#059669)
    public static final Color COLOR_DANGER = new Color(220, 38, 38);     // Rojo Alerta (#DC2626)
    public static final Color COLOR_WARNING = new Color(217, 119, 6);    // Ámbar (#D97706)
    
    // Colores de soporte para bordes y fondos de tarjetas
    public static final Color CARD_BG_DARK = new Color(30, 41, 59);      // Slate 800 (#1E293B)
    public static final Color CARD_BG_LIGHT = new Color(248, 250, 252);  // Slate 50 (#F8FAFC)

    /**
     * Aplica el tema guardado al sistema.
     */
    public static void applyTheme() {
        try {
            if (isDarkMode) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
                UIManager.put("Button.arc", 12);
                UIManager.put("Component.arc", 12);
                UIManager.put("TextComponent.arc", 12);
                UIManager.put("ProgressBar.arc", 12);
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
                UIManager.put("Button.arc", 12);
                UIManager.put("Component.arc", 12);
                UIManager.put("TextComponent.arc", 12);
                UIManager.put("ProgressBar.arc", 12);
            }
            
            // Configurar propiedades avanzadas de FlatLaf
            FlatLaf.updateUI();
        } catch (Exception ex) {
            System.err.println("Fallo al aplicar el Look & Feel de FlatLaf: " + ex.getMessage());
        }
    }

    /**
     * Alterna entre modo oscuro y claro de forma dinámica.
     */
    public static void toggleTheme(Window parentWindow) {
        isDarkMode = !isDarkMode;
        applyTheme();
        
        // Forzar repintado completo de la interfaz de forma recursiva
        if (parentWindow != null) {
            SwingUtilities.updateComponentTreeUI(parentWindow);
            parentWindow.validate();
            parentWindow.repaint();
        }
    }

    public static boolean isDarkMode() {
        return isDarkMode;
    }

    public static Color getCardBackground() {
        return isDarkMode ? CARD_BG_DARK : CARD_BG_LIGHT;
    }
    
    public static Color getTextColor() {
        return isDarkMode ? Color.WHITE : Color.BLACK;
    }
}
