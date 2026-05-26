package com.tiendita.pos.view.panels;

import com.tiendita.pos.model.Product;
import com.tiendita.pos.model.UnitType;
import com.tiendita.pos.model.MermaReason;
import com.tiendita.pos.view.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.List;

/**
 * Panel de Inventario con CRUD completo y control de pérdidas (Mermas).
 */
public class InventoryPanel extends JPanel {
    // Formulario de Producto (Campos de entrada)
    private final JTextField txtBarcode;
    private final JTextField txtDescription;
    private final JTextField txtBuyPrice;
    private final JTextField txtSellPrice;
    private final JTextField txtStock;
    private final JTextField txtMinStock;
    private final JComboBox<UnitType> cbUnit;

    private final JButton btnSave;
    private final JButton btnUpdate;
    private final JButton btnDelete;
    private final JButton btnClear;

    // Formulario de Mermas (Campos de entrada)
    private final JComboBox<String> cbMermaProducts;
    private final JTextField txtMermaQty;
    private final JComboBox<MermaReason> cbMermaReason;
    private final JButton btnSaveMerma;

    // Buscador y Tabla
    private final JTextField txtSearch;
    private final JTable tblProducts;
    private final DefaultTableModel tableModel;

    public InventoryPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));

        // SPLIT PANEL: Formulario (Izquierda) - Tabla (Derecha)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);
        splitPane.setDividerLocation(380);
        splitPane.setContinuousLayout(true);
        add(splitPane, BorderLayout.CENTER);

        // ==================== PANEL IZQUIERDO: TABS (CRUD & MERMA) ====================
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Inter", Font.BOLD, 12));
        
        // Tab 1: CRUD de Productos
        JPanel crudTab = new JPanel(new BorderLayout(10, 10)) {
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
        crudTab.setOpaque(false);
        crudTab.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel formGrid = new JPanel(new GridLayout(7, 2, 10, 15));
        formGrid.setOpaque(false);

        formGrid.add(createLabel("Código de Barras:"));
        txtBarcode = new JTextField();
        formGrid.add(txtBarcode);

        formGrid.add(createLabel("Descripción:"));
        txtDescription = new JTextField();
        formGrid.add(txtDescription);

        formGrid.add(createLabel("Costo Compra ($):"));
        txtBuyPrice = new JTextField();
        formGrid.add(txtBuyPrice);

        formGrid.add(createLabel("Precio Venta ($):"));
        txtSellPrice = new JTextField();
        formGrid.add(txtSellPrice);

        formGrid.add(createLabel("Existencia Inicial:"));
        txtStock = new JTextField();
        formGrid.add(txtStock);

        formGrid.add(createLabel("Stock Mínimo (Alerta):"));
        txtMinStock = new JTextField();
        formGrid.add(txtMinStock);

        formGrid.add(createLabel("Unidad de Medida:"));
        cbUnit = new JComboBox<>(UnitType.values());
        formGrid.add(cbUnit);

        crudTab.add(formGrid, BorderLayout.CENTER);

        // Panel de Botones del CRUD
        JPanel buttonsGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonsGrid.setOpaque(false);
        buttonsGrid.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnSave = new JButton("Guardar 💾");
        btnSave.setBackground(ThemeManager.COLOR_SUCCESS);
        btnSave.setForeground(Color.WHITE);
        
        btnUpdate = new JButton("Actualizar 🔄");
        btnUpdate.setBackground(ThemeManager.COLOR_PRIMARY);
        btnUpdate.setForeground(Color.WHITE);

        btnDelete = new JButton("Eliminar ❌");
        btnDelete.setBackground(ThemeManager.COLOR_DANGER);
        btnDelete.setForeground(Color.WHITE);

        btnClear = new JButton("Limpiar Escoba 🧹");

        buttonsGrid.add(btnSave);
        buttonsGrid.add(btnUpdate);
        buttonsGrid.add(btnDelete);
        buttonsGrid.add(btnClear);

        crudTab.add(buttonsGrid, BorderLayout.SOUTH);
        tabbedPane.addTab("📦  Catálogo / CRUD", crudTab);

        // Tab 2: Gestión de Mermas
        JPanel mermaTab = new JPanel(new BorderLayout(15, 15)) {
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
        mermaTab.setOpaque(false);
        mermaTab.setBorder(new EmptyBorder(25, 20, 25, 20));

        JPanel mermaForm = new JPanel(new GridLayout(4, 1, 10, 20));
        mermaForm.setOpaque(false);

        JPanel p1 = new JPanel(new BorderLayout(5, 5)); p1.setOpaque(false);
        p1.add(createLabel("Seleccionar Producto:"), BorderLayout.NORTH);
        cbMermaProducts = new JComboBox<>();
        p1.add(cbMermaProducts, BorderLayout.CENTER);
        mermaForm.add(p1);

        JPanel p2 = new JPanel(new BorderLayout(5, 5)); p2.setOpaque(false);
        p2.add(createLabel("Cantidad Merma (Pza/Kg):"), BorderLayout.NORTH);
        txtMermaQty = new JTextField();
        p2.add(txtMermaQty, BorderLayout.CENTER);
        mermaForm.add(p2);

        JPanel p3 = new JPanel(new BorderLayout(5, 5)); p3.setOpaque(false);
        p3.add(createLabel("Motivo de Pérdida:"), BorderLayout.NORTH);
        cbMermaReason = new JComboBox<>(MermaReason.values());
        p3.add(cbMermaReason, BorderLayout.CENTER);
        mermaForm.add(p3);

        btnSaveMerma = new JButton("Registrar Merma ⚠️");
        btnSaveMerma.setBackground(ThemeManager.COLOR_WARNING);
        btnSaveMerma.setForeground(Color.WHITE);
        btnSaveMerma.setPreferredSize(new Dimension(300, 45));

        mermaTab.add(mermaForm, BorderLayout.CENTER);
        mermaTab.add(btnSaveMerma, BorderLayout.SOUTH);
        tabbedPane.addTab("⚠️  Reportar Mermas", mermaTab);

        splitPane.setLeftComponent(tabbedPane);

        // ==================== PANEL DERECHO: BUSCADOR & TABLA ====================
        JPanel rightCard = new JPanel(new BorderLayout(15, 15)) {
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
        rightCard.setOpaque(false);
        rightCard.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Barra Superior de Búsqueda
        JPanel searchBar = new JPanel(new BorderLayout(10, 10));
        searchBar.setOpaque(false);
        searchBar.add(createLabel("Buscar Producto:"), BorderLayout.WEST);
        txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "Escribe descripción o código de barras...");
        searchBar.add(txtSearch, BorderLayout.CENTER);
        rightCard.add(searchBar, BorderLayout.NORTH);

        // Tabla de Productos
        String[] columns = {"Código", "Descripción", "Costo ($)", "P. Venta ($)", "Stock", "Mínimo", "Medida"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblProducts = new JTable(tableModel);
        tblProducts.setRowHeight(28);
        tblProducts.setFont(new Font("Inter", Font.PLAIN, 12));
        tblProducts.getTableHeader().setFont(new Font("Inter", Font.BOLD, 12));
        tblProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Custom Cell Renderer para pintar de color rojo los productos con bajo stock
        tblProducts.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                try {
                    double stock = (double) table.getValueAt(row, 4);
                    double minStock = (double) table.getValueAt(row, 5);
                    
                    if (stock <= minStock) {
                        if (isSelected) {
                            c.setBackground(ThemeManager.COLOR_DANGER);
                            c.setForeground(Color.WHITE);
                        } else {
                            // Rojo muy suave en claro, rojo oscuro opaco en oscuro
                            c.setBackground(ThemeManager.isDarkMode() ? new Color(153, 27, 27) : new Color(254, 226, 226));
                            c.setForeground(ThemeManager.isDarkMode() ? Color.WHITE : new Color(153, 27, 27));
                        }
                    } else {
                        c.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                        c.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                    }
                } catch (Exception e) {
                    // Fallback
                }
                return c;
            }
        });

        JScrollPane scrollTable = new JScrollPane(tblProducts);
        scrollTable.setBorder(BorderFactory.createEmptyBorder());
        scrollTable.getViewport().setBackground(ThemeManager.getCardBackground());
        rightCard.add(scrollTable, BorderLayout.CENTER);

        splitPane.setRightComponent(rightCard);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Inter", Font.BOLD, 12));
        label.setForeground(ThemeManager.getTextColor());
        return label;
    }

    // ========== GETTERS Y SETTERS ==========
    public String getBarcode() { return txtBarcode.getText().trim(); }
    public String getDescription() { return txtDescription.getText().trim(); }
    public double getBuyPrice() { return Double.parseDouble(txtBuyPrice.getText()); }
    public double getSellPrice() { return Double.parseDouble(txtSellPrice.getText()); }
    public double getStock() { return Double.parseDouble(txtStock.getText()); }
    public double getMinStock() { return Double.parseDouble(txtMinStock.getText()); }
    public UnitType getUnit() { return (UnitType) cbUnit.getSelectedItem(); }

    public String getSearchQuery() { return txtSearch.getText().trim(); }

    // Campos de Merma
    public String getSelectedMermaBarcode() {
        String selected = (String) cbMermaProducts.getSelectedItem();
        if (selected == null) return null;
        // El formato es "Barcode - Description"
        return selected.split(" - ")[0];
    }
    public double getMermaQty() { return Double.parseDouble(txtMermaQty.getText()); }
    public MermaReason getMermaReason() { return (MermaReason) cbMermaReason.getSelectedItem(); }

    // Rellenar formulario al seleccionar renglón de la tabla
    public void fillProductForm(Product p) {
        txtBarcode.setText(p.getBarcode());
        txtBarcode.setEditable(false); // No editar la llave primaria
        txtDescription.setText(p.getDescription());
        txtBuyPrice.setText(String.valueOf(p.getBuyPrice()));
        txtSellPrice.setText(String.valueOf(p.getSellPrice()));
        txtStock.setText(String.valueOf(p.getStock()));
        txtMinStock.setText(String.valueOf(p.getMinStock()));
        cbUnit.setSelectedItem(p.getUnit());
    }

    public void clearProductForm() {
        txtBarcode.setText("");
        txtBarcode.setEditable(true);
        txtDescription.setText("");
        txtBuyPrice.setText("");
        txtSellPrice.setText("");
        txtStock.setText("");
        txtMinStock.setText("");
        cbUnit.setSelectedIndex(0);
        tblProducts.clearSelection();
    }

    public void clearMermaForm() {
        txtMermaQty.setText("");
        cbMermaReason.setSelectedIndex(0);
    }

    public void setProductList(List<Product> products) {
        tableModel.setRowCount(0);
        cbMermaProducts.removeAllItems();
        for (Product p : products) {
            tableModel.addRow(new Object[]{
                p.getBarcode(),
                p.getDescription(),
                p.getBuyPrice(),
                p.getSellPrice(),
                p.getStock(),
                p.getMinStock(),
                p.getUnit().getLabel()
            });
            cbMermaProducts.addItem(p.getBarcode() + " - " + p.getDescription());
        }
    }

    public int getSelectedRow() {
        return tblProducts.getSelectedRow();
    }

    public String getBarcodeFromSelectedRow() {
        int row = getSelectedRow();
        if (row != -1) {
            return (String) tblProducts.getValueAt(row, 0);
        }
        return null;
    }

    // ========== LISTENERS ==========
    public void addSaveProductListener(ActionListener l) { btnSave.addActionListener(l); }
    public void addUpdateProductListener(ActionListener l) { btnUpdate.addActionListener(l); }
    public void addDeleteProductListener(ActionListener l) { btnDelete.addActionListener(l); }
    public void addClearProductListener(ActionListener l) { btnClear.addActionListener(l); }
    public void addSearchListener(KeyListener l) { txtSearch.addKeyListener(l); }
    public void addRegisterMermaListener(ActionListener l) { btnSaveMerma.addActionListener(l); }
    public void addTableSelectionListener(ListSelectionListener l) { tblProducts.getSelectionModel().addListSelectionListener(l); }
}
