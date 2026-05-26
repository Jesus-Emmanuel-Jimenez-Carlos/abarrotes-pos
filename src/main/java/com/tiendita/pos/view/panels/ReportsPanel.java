package com.tiendita.pos.view.panels;

import com.tiendita.pos.model.Sale;
import com.tiendita.pos.model.SaleDetail;
import com.tiendita.pos.view.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel de reportes de auditoría contable y revisión de ticket histórico con detalle modal.
 */
public class ReportsPanel extends JPanel {
    private final JTextField txtStartDate;
    private final JTextField txtEndDate;
    private final JButton btnFilter;

    private final JLabel lblTotalSales;
    private final JLabel lblNetProfit;

    private final JTable tblSales;
    private final DefaultTableModel tableModel;
    private final JButton btnViewDetail;

    public ReportsPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(15, 15));

        // ==================== BARRA SUPERIOR: FILTRO DE FECHAS ====================
        JPanel filterCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getCardBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        filterCard.setOpaque(false);
        filterCard.setBorder(new EmptyBorder(5, 10, 5, 10));

        filterCard.add(createLabel("Fecha Inicio (AAAA-MM-DD):"));
        txtStartDate = new JTextField(10);
        txtStartDate.setFont(new Font("Inter", Font.BOLD, 13));
        txtStartDate.setText(LocalDate.now().toString());
        filterCard.add(txtStartDate);

        filterCard.add(createLabel("Fecha Fin (AAAA-MM-DD):"));
        txtEndDate = new JTextField(10);
        txtEndDate.setFont(new Font("Inter", Font.BOLD, 13));
        txtEndDate.setText(LocalDate.now().toString());
        filterCard.add(txtEndDate);

        btnFilter = new JButton("Filtrar Reportes 📅");
        btnFilter.setFont(new Font("Inter", Font.BOLD, 12));
        btnFilter.setBackground(ThemeManager.COLOR_PRIMARY);
        btnFilter.setForeground(Color.WHITE);
        filterCard.add(btnFilter);

        add(filterCard, BorderLayout.NORTH);

        // ==================== ZONA CENTRAL: SPLIT METRICS & TABLE ====================
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setOpaque(false);

        // Subpanel de Métricas del Filtro
        JPanel metricsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        metricsPanel.setOpaque(false);
        metricsPanel.setPreferredSize(new Dimension(getWidth(), 80));

        // Tarjeta Ventas Totales
        JPanel card1 = new JPanel(new GridLayout(2, 1)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(ThemeManager.getCardBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        card1.setOpaque(false);
        card1.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel l1 = new JLabel("VENTAS BRUTAS EN RANGO"); l1.setFont(new Font("Inter", Font.BOLD, 11)); l1.setForeground(new Color(148, 163, 184));
        lblTotalSales = new JLabel("$0.00"); lblTotalSales.setFont(new Font("Inter", Font.BOLD, 22)); lblTotalSales.setForeground(ThemeManager.COLOR_SUCCESS);
        card1.add(l1); card1.add(lblTotalSales);

        // Tarjeta Utilidad Neta
        JPanel card2 = new JPanel(new GridLayout(2, 1)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(ThemeManager.getCardBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        card2.setOpaque(false);
        card2.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel l2 = new JLabel("UTILIDAD NETA (GANANCIA)"); l2.setFont(new Font("Inter", Font.BOLD, 11)); l2.setForeground(new Color(148, 163, 184));
        lblNetProfit = new JLabel("$0.00"); lblNetProfit.setFont(new Font("Inter", Font.BOLD, 22)); lblNetProfit.setForeground(ThemeManager.COLOR_PRIMARY);
        card2.add(l2); card2.add(lblNetProfit);

        metricsPanel.add(card1);
        metricsPanel.add(card2);
        centerPanel.add(metricsPanel, BorderLayout.NORTH);

        // Tabla de Historial de Ventas
        JPanel tableCard = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getCardBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        tableCard.setOpaque(false);
        tableCard.setBorder(new EmptyBorder(15, 20, 15, 20));

        String[] columns = {"ID Venta", "Fecha y Hora", "Método", "Operador", "Total ($)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblSales = new JTable(tableModel);
        tblSales.setRowHeight(28);
        tblSales.setFont(new Font("Inter", Font.PLAIN, 12));
        tblSales.getTableHeader().setFont(new Font("Inter", Font.BOLD, 12));
        tblSales.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollTable = new JScrollPane(tblSales);
        scrollTable.setBorder(BorderFactory.createEmptyBorder());
        scrollTable.getViewport().setBackground(ThemeManager.getCardBackground());
        tableCard.add(scrollTable, BorderLayout.CENTER);

        // Botón Ver Detalle Modal
        btnViewDetail = new JButton("Ver Detalle del Ticket Seleccionado 🔍");
        btnViewDetail.setFont(new Font("Inter", Font.BOLD, 13));
        btnViewDetail.setBackground(ThemeManager.COLOR_PRIMARY);
        btnViewDetail.setForeground(Color.WHITE);
        tableCard.add(btnViewDetail, BorderLayout.SOUTH);

        centerPanel.add(tableCard, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Inter", Font.BOLD, 12));
        label.setForeground(ThemeManager.getTextColor());
        return label;
    }

    public String getStartDateStr() {
        return txtStartDate.getText().trim();
    }

    public String getEndDateStr() {
        return txtEndDate.getText().trim();
    }

    public void setSalesSummary(double totalSales, double netProfit) {
        lblTotalSales.setText(String.format("$%.2f", totalSales));
        lblNetProfit.setText(String.format("$%.2f", netProfit));
    }

    public void setSalesHistory(List<Sale> sales) {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (Sale s : sales) {
            tableModel.addRow(new Object[]{
                s.getId(),
                s.getSaleDate().format(formatter),
                s.getPaymentMethod().getLabel(),
                s.getUsername(),
                s.getTotal()
            });
        }
    }

    public Integer getSelectedSaleId() {
        int row = tblSales.getSelectedRow();
        if (row != -1) {
            return (Integer) tblSales.getValueAt(row, 0);
        }
        return null;
    }

    /**
     * Lanza un cuadro de diálogo modal detallando los productos vendidos de una venta.
     */
    public void showSaleDetailsModal(Frame parent, int saleId, List<SaleDetail> details) {
        JDialog dialog = new JDialog(parent, "Detalle del Ticket #" + saleId, true);
        dialog.setSize(550, 400);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        JLabel title = new JLabel("Ticket de Compra #" + saleId);
        title.setFont(new Font("Inter", Font.BOLD, 16));
        header.add(title);
        dialog.add(header, BorderLayout.NORTH);

        // Tabla de Renglones de venta
        String[] cols = {"Código", "Descripción", "Cantidad", "P. Unitario ($)", "Subtotal ($)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(26);
        table.setFont(new Font("Inter", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Inter", Font.BOLD, 12));

        double sumTotal = 0.0;
        for (SaleDetail d : details) {
            model.addRow(new Object[]{
                d.getProductBarcode(),
                d.getProductDescription(),
                d.getQuantity(),
                d.getSellPrice(),
                d.getSubtotal()
            });
            sumTotal += d.getSubtotal();
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(5, 15, 5, 15));
        dialog.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        JLabel lblTotal = new JLabel(String.format("Total: $%.2f", sumTotal));
        lblTotal.setFont(new Font("Inter", Font.BOLD, 16));
        lblTotal.setForeground(ThemeManager.COLOR_SUCCESS);
        footer.add(lblTotal);

        JButton btnClose = new JButton("Cerrar 🚪");
        btnClose.addActionListener(e -> dialog.dispose());
        footer.add(btnClose);
        dialog.add(footer, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    public void addFilterListener(ActionListener l) { btnFilter.addActionListener(l); }
    public void addViewDetailListener(ActionListener l) { btnViewDetail.addActionListener(l); }
}
