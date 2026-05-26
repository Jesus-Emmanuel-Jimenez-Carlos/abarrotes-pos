package com.tiendita.pos.view.panels;

import com.tiendita.pos.view.components.ChartPanel;
import com.tiendita.pos.view.components.StatCard;
import com.tiendita.pos.view.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de Dashboard Gerencial que consolida métricas financieras de hoy y el gráfico de top ventas.
 */
public class DashboardPanel extends JPanel {
    private final StatCard cardSales;
    private final StatCard cardProfit;
    private final StatCard cardAlerts;
    private final StatCard cardValuation;
    private final ChartPanel topSalesChart;
    private final JTable tblRecentSales;
    private final DefaultTableModel tableModel;

    public DashboardPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));

        // 1. Panel Superior (KPI Grid - 4 Columnas)
        JPanel gridKPI = new JPanel(new GridLayout(1, 4, 15, 0));
        gridKPI.setOpaque(false);

        cardSales = new StatCard("Ventas de Hoy", "$0.00", "Efectivo + Tarjeta", "💰", ThemeManager.COLOR_SUCCESS);
        cardProfit = new StatCard("Utilidad de Hoy", "$0.00", "Ganancia Neta Real", "📈", ThemeManager.COLOR_PRIMARY);
        cardAlerts = new StatCard("Alertas de Stock", "0 prod", "Stock mínimo o inferior", "⚠️", ThemeManager.COLOR_DANGER);
        cardValuation = new StatCard("Valor de Tienda", "$0.00", "Inversión en Almacén", "📦", ThemeManager.COLOR_WARNING);

        gridKPI.add(cardSales);
        gridKPI.add(cardProfit);
        gridKPI.add(cardAlerts);
        gridKPI.add(cardValuation);
        add(gridKPI, BorderLayout.NORTH);

        // 2. Panel Inferior (Gráfico de Ventas a la izquierda y Tabla de ventas recientes a la derecha)
        JPanel bottomSplit = new JPanel(new GridBagLayout());
        bottomSplit.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Gráfico (Lado Izquierdo)
        topSalesChart = new ChartPanel();
        gbc.gridx = 0;
        gbc.weightx = 0.55;
        gbc.insets = new Insets(0, 0, 0, 10);
        bottomSplit.add(topSalesChart, gbc);

        // Ventas Recientes (Lado Derecho)
        JPanel recentSalesCard = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getCardBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(ThemeManager.isDarkMode() ? new Color(255, 255, 255, 15) : new Color(0, 0, 0, 15));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        recentSalesCard.setOpaque(false);
        recentSalesCard.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("Ventas Recientes (Hoy)");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 14));
        lblTitle.setForeground(ThemeManager.getTextColor());
        recentSalesCard.add(lblTitle, BorderLayout.NORTH);

        // Tabla de Ventas Recientes
        String[] columns = {"ID", "Hora", "Total Venta", "Método"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblRecentSales = new JTable(tableModel);
        tblRecentSales.setFont(new Font("Inter", Font.PLAIN, 12));
        tblRecentSales.setRowHeight(26);
        tblRecentSales.getTableHeader().setFont(new Font("Inter", Font.BOLD, 12));
        tblRecentSales.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(tblRecentSales);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(ThemeManager.getCardBackground());
        recentSalesCard.add(scrollPane, BorderLayout.CENTER);

        gbc.gridx = 1;
        gbc.weightx = 0.45;
        gbc.insets = new Insets(0, 10, 0, 0);
        bottomSplit.add(recentSalesCard, gbc);

        add(bottomSplit, BorderLayout.CENTER);
    }

    /**
     * Refresca las 4 tarjetas de estadísticas con valores actualizados.
     */
    public void updateStats(double sales, double profit, int lowStockCount, double inventoryValue) {
        cardSales.setValue(String.format("$%.2f", sales));
        cardProfit.setValue(String.format("$%.2f", profit));
        
        cardAlerts.setValue(String.format("%d prod", lowStockCount));
        cardAlerts.setSubtitle(lowStockCount > 0 ? "¡Requiere reabastecer!" : "Inventario al día");
        
        cardValuation.setValue(String.format("$%.2f", inventoryValue));
    }

    /**
     * Actualiza el gráfico del top de ventas.
     */
    public void updateChart(List<String> labels, List<Double> values) {
        topSalesChart.setData(labels, values);
    }

    /**
     * Agrega renglones de ventas recientes a la tabla del dashboard.
     */
    public void clearRecentSales() {
        tableModel.setRowCount(0);
    }

    public void addRecentSale(int id, String time, double total, String method) {
        tableModel.addRow(new Object[]{id, time, String.format("$%.2f", total), method});
    }
}
