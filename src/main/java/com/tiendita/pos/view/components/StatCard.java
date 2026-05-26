package com.tiendita.pos.view.components;

import com.tiendita.pos.view.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Tarjeta premium para el dashboard que muestra indicadores clave (KPIs) con bordes suaves.
 */
public class StatCard extends JPanel {
    private final JLabel lblTitle;
    private final JLabel lblValue;
    private final JLabel lblSubtitle;
    private final JLabel lblIcon;

    public StatCard(String title, String initialValue, String subtitle, String iconUnicode, Color themeColor) {
        setLayout(new BorderLayout(10, 5));
        setBorder(new EmptyBorder(15, 20, 15, 20));
        setOpaque(false);

        // Panel de Textos
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Inter", Font.BOLD, 13));
        lblTitle.setForeground(new Color(148, 163, 184)); // Slate 400

        lblValue = new JLabel(initialValue);
        lblValue.setFont(new Font("Inter", Font.BOLD, 22));
        lblValue.setForeground(ThemeManager.getTextColor());

        lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setFont(new Font("Inter", Font.PLAIN, 11));
        lblSubtitle.setForeground(themeColor);

        textPanel.add(lblTitle);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(lblValue);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(lblSubtitle);

        // Panel de Icono
        lblIcon = new JLabel(iconUnicode);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcon.setForeground(themeColor);

        add(textPanel, BorderLayout.CENTER);
        add(lblIcon, BorderLayout.EAST);
        
        // Agregar efecto de redimensionado simple
        setPreferredSize(new Dimension(220, 100));
    }

    public void setValue(String value) {
        lblValue.setText(value);
        lblValue.setForeground(ThemeManager.getTextColor());
    }

    public void setSubtitle(String subtitle) {
        lblSubtitle.setText(subtitle);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibujar fondo redondeado
        g2.setColor(ThemeManager.getCardBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

        // Dibujar borde sutil
        g2.setColor(ThemeManager.isDarkMode() ? new Color(255, 255, 255, 15) : new Color(0, 0, 0, 15));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);

        g2.dispose();
    }
}
