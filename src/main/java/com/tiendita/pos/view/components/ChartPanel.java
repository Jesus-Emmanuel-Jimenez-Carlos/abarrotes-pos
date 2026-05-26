package com.tiendita.pos.view.components;

import com.tiendita.pos.view.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Gráfico estadístico dibujado mediante vectores con soporte dinámico para el Dashboard.
 */
public class ChartPanel extends JPanel {
    private String title = "Top 5 Productos Más Vendidos";
    private final List<String> labels = new ArrayList<>();
    private final List<Double> values = new ArrayList<>();

    public ChartPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(450, 220));
    }

    public synchronized void setData(List<String> newLabels, List<Double> newValues) {
        labels.clear();
        values.clear();
        labels.addAll(newLabels);
        values.addAll(newValues);
        repaint();
    }

    public void setTitle(String title) {
        this.title = title;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // 1. Dibujar tarjeta de fondo
        g2.setColor(ThemeManager.getCardBackground());
        g2.fillRoundRect(0, 0, width, height, 16, 16);
        g2.setColor(ThemeManager.isDarkMode() ? new Color(255, 255, 255, 15) : new Color(0, 0, 0, 15));
        g2.drawRoundRect(0, 0, width - 1, height - 1, 16, 16);

        // 2. Título del gráfico
        g2.setFont(new Font("Inter", Font.BOLD, 14));
        g2.setColor(ThemeManager.getTextColor());
        g2.drawString(title, 20, 30);

        if (labels.isEmpty() || values.isEmpty()) {
            g2.setFont(new Font("Inter", Font.PLAIN, 12));
            g2.setColor(new Color(148, 163, 184)); // Slate 400
            g2.drawString("No hay datos de ventas disponibles.", width / 2 - 90, height / 2);
            g2.dispose();
            return;
        }

        // 3. Encontrar valor máximo para escalar
        double maxValue = 0.0;
        for (double v : values) {
            if (v > maxValue) {
                maxValue = v;
            }
        }
        if (maxValue == 0.0) maxValue = 1.0;

        // 4. Parámetros de dibujo de barras horizontales
        int leftMargin = 120;
        int rightMargin = 40;
        int topMargin = 55;
        int bottomMargin = 20;

        int chartWidth = width - leftMargin - rightMargin;
        int chartHeight = height - topMargin - bottomMargin;
        int barCount = labels.size();
        int rowHeight = chartHeight / barCount;
        int barHeight = (int) (rowHeight * 0.55);

        for (int i = 0; i < barCount; i++) {
            int y = topMargin + (i * rowHeight) + (rowHeight - barHeight) / 2;
            
            // Dibujar Etiqueta
            g2.setFont(new Font("Inter", Font.BOLD, 11));
            g2.setColor(ThemeManager.getTextColor());
            String text = labels.get(i);
            if (text.length() > 16) {
                text = text.substring(0, 14) + "..";
            }
            
            // Alinear texto a la derecha del margen izquierdo
            FontMetrics fm = g2.getFontMetrics();
            int stringWidth = fm.stringWidth(text);
            g2.drawString(text, leftMargin - stringWidth - 10, y + (barHeight / 2) + 4);

            // Calcular ancho escalado de la barra
            double val = values.get(i);
            int barWidth = (int) ((val / maxValue) * chartWidth);
            if (barWidth < 5) barWidth = 5; // Ancho mínimo visible

            // Fondo sutil de barra
            g2.setColor(ThemeManager.isDarkMode() ? new Color(255, 255, 255, 8) : new Color(0, 0, 0, 8));
            g2.fill(new RoundRectangle2D.Float(leftMargin, y, chartWidth, barHeight, 8, 8));

            // Barra real con gradiente
            GradientPaint gp = new GradientPaint(
                leftMargin, y, ThemeManager.COLOR_PRIMARY,
                leftMargin + barWidth, y, new Color(96, 165, 250) // Light Blue (#60A5FA)
            );
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Float(leftMargin, y, barWidth, barHeight, 8, 8));

            // Dibujar valor al final de la barra
            g2.setFont(new Font("Inter", Font.BOLD, 10));
            g2.setColor(ThemeManager.getTextColor());
            String valStr = String.format("%.1f", val);
            g2.drawString(valStr, leftMargin + barWidth + 8, y + (barHeight / 2) + 4);
        }

        g2.dispose();
    }
}
