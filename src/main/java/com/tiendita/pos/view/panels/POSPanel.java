package com.tiendita.pos.view.panels;

import com.tiendita.pos.model.PaymentMethod;
import com.tiendita.pos.model.Product;
import com.tiendita.pos.view.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

/**
 * Panel interactivo del Punto de Venta (POS) con calculadora de cambio en tiempo real.
 */
public class POSPanel extends JPanel {
    private final JTextField txtBarcodeSearch;
    private final JTable tblCart;
    private final DefaultTableModel tableModel;

    // Panel de Cobro
    private final JLabel lblTotalDisplay;
    private final JComboBox<PaymentMethod> cbPaymentMethod;
    private final JTextField txtCashReceived;
    private final JLabel lblChangeDisplay;
    private final JButton btnCheckout;
    private final JButton btnCancel;

    private double currentTotal = 0.0;

    public POSPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(15, 15));

        // ==================== PARTE SUPERIOR: BUSCADOR DE CÓDIGO ====================
        JPanel topSearchPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getCardBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        topSearchPanel.setOpaque(false);
        topSearchPanel.setBorder(new EmptyBorder(12, 15, 12, 15));

        JLabel lblScan = new JLabel("🔍 Escanear / Buscar:");
        lblScan.setFont(new Font("Inter", Font.BOLD, 13));
        lblScan.setForeground(ThemeManager.getTextColor());
        topSearchPanel.add(lblScan, BorderLayout.WEST);

        txtBarcodeSearch = new JTextField();
        txtBarcodeSearch.setFont(new Font("Inter", Font.BOLD, 15));
        txtBarcodeSearch.putClientProperty("JTextField.placeholderText", "Escanea código de barras o escribe descripción del producto y presiona Enter...");
        topSearchPanel.add(txtBarcodeSearch, BorderLayout.CENTER);

        add(topSearchPanel, BorderLayout.NORTH);

        // ==================== CUERPO PRINCIPAL: CARRITO (Izquierda) - COBRO (Derecha) ====================
        JSplitPane bodySplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        bodySplit.setOpaque(false);
        bodySplit.setBorder(null);
        bodySplit.setDividerLocation(700);
        bodySplit.setContinuousLayout(true);
        add(bodySplit, BorderLayout.CENTER);

        // A. Carrito de Compras (Tabla)
        JPanel cartCard = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getCardBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        cartCard.setOpaque(false);
        cartCard.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblCartTitle = new JLabel("🛒 Carrito de Compra");
        lblCartTitle.setFont(new Font("Inter", Font.BOLD, 14));
        lblCartTitle.setForeground(ThemeManager.getTextColor());
        cartCard.add(lblCartTitle, BorderLayout.NORTH);

        String[] columns = {"Código", "Descripción", "Cantidad", "Medida", "P. Unitario ($)", "Subtotal ($)"};
        // Permitimos editar la cantidad únicamente
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Solo la columna "Cantidad" es editable directamente
            }
        };
        tblCart = new JTable(tableModel);
        tblCart.setRowHeight(32);
        tblCart.setFont(new Font("Inter", Font.PLAIN, 13));
        tblCart.getTableHeader().setFont(new Font("Inter", Font.BOLD, 13));
        tblCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollTable = new JScrollPane(tblCart);
        scrollTable.setBorder(BorderFactory.createEmptyBorder());
        scrollTable.getViewport().setBackground(ThemeManager.getCardBackground());
        cartCard.add(scrollTable, BorderLayout.CENTER);

        // Botón rápido para remover producto del carrito
        JButton btnRemoveItem = new JButton("Remover Producto Seleccionado 🗑️");
        btnRemoveItem.setFont(new Font("Inter", Font.BOLD, 11));
        btnRemoveItem.setBackground(ThemeManager.COLOR_DANGER);
        btnRemoveItem.setForeground(Color.WHITE);
        btnRemoveItem.addActionListener(e -> removeSelectedRow());
        cartCard.add(btnRemoveItem, BorderLayout.SOUTH);

        bodySplit.setLeftComponent(cartCard);

        // B. Panel de Control de Pago (Lado Derecho)
        JPanel paymentCard = new JPanel(new BorderLayout(15, 15)) {
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
        paymentCard.setOpaque(false);
        paymentCard.setBorder(new EmptyBorder(25, 20, 25, 20));

        // Subpanel de Lectura Total (Alto Contraste)
        JPanel totalBox = new JPanel(new GridLayout(2, 1));
        totalBox.setOpaque(false);
        
        JLabel lblTotalLabel = new JLabel("TOTAL A COBRAR");
        lblTotalLabel.setFont(new Font("Inter", Font.BOLD, 12));
        lblTotalLabel.setForeground(new Color(148, 163, 184));
        lblTotalLabel.setHorizontalAlignment(SwingConstants.CENTER);

        lblTotalDisplay = new JLabel("$0.00");
        lblTotalDisplay.setFont(new Font("Inter", Font.BOLD, 36));
        lblTotalDisplay.setForeground(ThemeManager.COLOR_SUCCESS);
        lblTotalDisplay.setHorizontalAlignment(SwingConstants.CENTER);

        totalBox.add(lblTotalLabel);
        totalBox.add(lblTotalDisplay);
        paymentCard.add(totalBox, BorderLayout.NORTH);

        // Formulario de Pago
        JPanel payForm = new JPanel(new GridLayout(6, 1, 5, 12));
        payForm.setOpaque(false);

        payForm.add(createLabel("Método de Pago:"));
        cbPaymentMethod = new JComboBox<>(PaymentMethod.values());
        cbPaymentMethod.setFont(new Font("Inter", Font.BOLD, 13));
        payForm.add(cbPaymentMethod);

        payForm.add(createLabel("Efectivo Recibido ($):"));
        txtCashReceived = new JTextField();
        txtCashReceived.setFont(new Font("Inter", Font.BOLD, 18));
        txtCashReceived.setForeground(ThemeManager.COLOR_PRIMARY);
        payForm.add(txtCashReceived);

        payForm.add(createLabel("Cambio a Entregar ($):"));
        lblChangeDisplay = new JLabel("$0.00");
        lblChangeDisplay.setFont(new Font("Inter", Font.BOLD, 24));
        lblChangeDisplay.setForeground(ThemeManager.COLOR_WARNING);
        lblChangeDisplay.setHorizontalAlignment(SwingConstants.LEFT);
        payForm.add(lblChangeDisplay);

        paymentCard.add(payForm, BorderLayout.CENTER);

        // Botones de Acción (Cobrar / Cancelar)
        JPanel actPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        actPanel.setOpaque(false);

        btnCheckout = new JButton("COBRAR Y REGISTRAR VENTA 💵");
        btnCheckout.setFont(new Font("Inter", Font.BOLD, 14));
        btnCheckout.setBackground(ThemeManager.COLOR_PRIMARY);
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.setPreferredSize(new Dimension(200, 48));

        btnCancel = new JButton("Cancelar Operación 🗑️");
        btnCancel.setFont(new Font("Inter", Font.BOLD, 12));

        actPanel.add(btnCheckout);
        actPanel.add(btnCancel);
        paymentCard.add(actPanel, BorderLayout.SOUTH);

        bodySplit.setRightComponent(paymentCard);

        // ========== BINDING DE CÁLCULO DE CAMBIO EN TIEMPO REAL ==========
        txtCashReceived.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calculateChange(); }
            public void removeUpdate(DocumentEvent e) { calculateChange(); }
            public void changedUpdate(DocumentEvent e) { calculateChange(); }
        });

        // Alternar campo de efectivo si cambia el método de pago
        cbPaymentMethod.addActionListener(e -> {
            boolean isCash = cbPaymentMethod.getSelectedItem() == PaymentMethod.EFECTIVO;
            txtCashReceived.setEnabled(isCash);
            if (!isCash) {
                txtCashReceived.setText("");
                lblChangeDisplay.setText("$0.00");
            }
        });
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Inter", Font.BOLD, 12));
        label.setForeground(ThemeManager.getTextColor());
        return label;
    }

    private synchronized void calculateChange() {
        if (cbPaymentMethod.getSelectedItem() != PaymentMethod.EFECTIVO) {
            return;
        }

        String receivedText = txtCashReceived.getText().trim();
        if (receivedText.isEmpty()) {
            lblChangeDisplay.setText("$0.00");
            return;
        }

        try {
            double received = Double.parseDouble(receivedText);
            double change = received - currentTotal;
            if (change < 0) {
                lblChangeDisplay.setText("Monto Insuficiente");
                lblChangeDisplay.setForeground(ThemeManager.COLOR_DANGER);
            } else {
                lblChangeDisplay.setText(String.format("$%.2f", change));
                lblChangeDisplay.setForeground(ThemeManager.COLOR_SUCCESS);
            }
        } catch (NumberFormatException ex) {
            lblChangeDisplay.setText("Monto Inválido");
            lblChangeDisplay.setForeground(ThemeManager.COLOR_DANGER);
        }
    }

    /**
     * Añade un renglón al carrito de compra o incrementa la cantidad si ya existe.
     */
    public synchronized void addProductToCart(Product p, double qty) {
        // Verificar si ya existe en la JTable
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String barcode = (String) tableModel.getValueAt(i, 0);
            if (barcode.equals(p.getBarcode())) {
                double currentQty = (double) tableModel.getValueAt(i, 2);
                double newQty = currentQty + qty;
                tableModel.setValueAt(newQty, i, 2);
                tableModel.setValueAt(newQty * p.getSellPrice(), i, 5);
                updateTotals();
                return;
            }
        }

        // Si no existe, agregarlo como nuevo renglón
        tableModel.addRow(new Object[]{
            p.getBarcode(),
            p.getDescription(),
            qty,
            p.getUnit().name(),
            p.getSellPrice(),
            qty * p.getSellPrice()
        });
        updateTotals();
    }

    /**
     * Elimina el renglón actualmente seleccionado del carrito.
     */
    private void removeSelectedRow() {
        int row = tblCart.getSelectedRow();
        if (row != -1) {
            tableModel.removeRow(row);
            updateTotals();
        }
    }

    /**
     * Actualiza la suma total consolidada del carrito.
     */
    public synchronized void updateTotals() {
        double sum = 0.0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            double sub = (double) tableModel.getValueAt(i, 5);
            sum += sub;
        }
        currentTotal = sum;
        lblTotalDisplay.setText(String.format("$%.2f", sum));
        lblTotalDisplay.setForeground(sum > 0 ? ThemeManager.COLOR_SUCCESS : ThemeManager.getTextColor());
        calculateChange();
    }

    public void clearCart() {
        tableModel.setRowCount(0);
        txtCashReceived.setText("");
        lblChangeDisplay.setText("$0.00");
        updateTotals();
    }

    // ========== GETTERS Y SETTERS ==========
    public String getSearchQuery() { return txtBarcodeSearch.getText().trim(); }
    public void clearSearchQuery() { txtBarcodeSearch.setText(""); }
    public double getTotal() { return currentTotal; }
    public PaymentMethod getPaymentMethod() { return (PaymentMethod) cbPaymentMethod.getSelectedItem(); }
    public double getCashReceived() {
        if (getPaymentMethod() == PaymentMethod.TARJETA) return currentTotal;
        String val = txtCashReceived.getText().trim();
        return val.isEmpty() ? 0.0 : Double.parseDouble(val);
    }
    public DefaultTableModel getCartModel() { return tableModel; }

    // ========== LISTENERS ==========
    public void addBarcodeSearchListener(ActionListener l) { txtBarcodeSearch.addActionListener(l); }
    public void addCheckoutListener(ActionListener l) { btnCheckout.addActionListener(l); }
    public void addCancelListener(ActionListener l) { btnCancel.addActionListener(l); }
    public void addCartTableListener(javax.swing.event.TableModelListener l) { tableModel.addTableModelListener(l); }
}
