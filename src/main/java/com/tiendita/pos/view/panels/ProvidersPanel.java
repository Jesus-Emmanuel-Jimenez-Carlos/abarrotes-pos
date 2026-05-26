package com.tiendita.pos.view.panels;

import com.tiendita.pos.model.Provider;
import com.tiendita.pos.view.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel para la administración de proveedores y directorio de contactos comerciales.
 */
public class ProvidersPanel extends JPanel {
    private final JTextField txtName;
    private final JTextField txtContact;
    private final JTextField txtPhone;
    private final JTextField txtEmail;

    private final JButton btnSave;
    private final JButton btnUpdate;
    private final JButton btnDelete;
    private final JButton btnClear;

    private final JTable tblProviders;
    private final DefaultTableModel tableModel;

    private Integer currentSelectedId = null;

    public ProvidersPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);
        splitPane.setDividerLocation(380);
        splitPane.setContinuousLayout(true);
        add(splitPane, BorderLayout.CENTER);

        // ==================== PANEL IZQUIERDO: FORMULARIO CRUD ====================
        JPanel crudCard = new JPanel(new BorderLayout(15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getCardBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        crudCard.setOpaque(false);
        crudCard.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("🤝 Directorio de Proveedores");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 14));
        lblTitle.setForeground(ThemeManager.getTextColor());
        crudCard.add(lblTitle, BorderLayout.NORTH);

        JPanel formGrid = new JPanel(new GridLayout(4, 2, 10, 20));
        formGrid.setOpaque(false);

        formGrid.add(createLabel("Razón Social / Nombre:"));
        txtName = new JTextField();
        formGrid.add(txtName);

        formGrid.add(createLabel("Nombre de Contacto:"));
        txtContact = new JTextField();
        formGrid.add(txtContact);

        formGrid.add(createLabel("Teléfono Móvil/Fijo:"));
        txtPhone = new JTextField();
        formGrid.add(txtPhone);

        formGrid.add(createLabel("Correo Electrónico:"));
        txtEmail = new JTextField();
        formGrid.add(txtEmail);

        crudCard.add(formGrid, BorderLayout.CENTER);

        // Grid de Botones
        JPanel buttonsGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonsGrid.setOpaque(false);

        btnSave = new JButton("Guardar Proveedor 💾");
        btnSave.setBackground(ThemeManager.COLOR_SUCCESS);
        btnSave.setForeground(Color.WHITE);

        btnUpdate = new JButton("Actualizar Datos 🔄");
        btnUpdate.setBackground(ThemeManager.COLOR_PRIMARY);
        btnUpdate.setForeground(Color.WHITE);

        btnDelete = new JButton("Eliminar Contacto ❌");
        btnDelete.setBackground(ThemeManager.COLOR_DANGER);
        btnDelete.setForeground(Color.WHITE);

        btnClear = new JButton("Limpiar Panel 🧹");

        buttonsGrid.add(btnSave);
        buttonsGrid.add(btnUpdate);
        buttonsGrid.add(btnDelete);
        buttonsGrid.add(btnClear);
        crudCard.add(buttonsGrid, BorderLayout.SOUTH);

        splitPane.setLeftComponent(crudCard);

        // ==================== PANEL DERECHO: DIRECTORIO EN TABLA ====================
        JPanel rightCard = new JPanel(new BorderLayout(15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getCardBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        rightCard.setOpaque(false);
        rightCard.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblListTitle = new JLabel("Lista de Proveedores Registrados");
        lblListTitle.setFont(new Font("Inter", Font.BOLD, 14));
        lblListTitle.setForeground(ThemeManager.getTextColor());
        rightCard.add(lblListTitle, BorderLayout.NORTH);

        String[] columns = {"ID", "Razón Social", "Contacto", "Teléfono", "Correo"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblProviders = new JTable(tableModel);
        tblProviders.setRowHeight(28);
        tblProviders.setFont(new Font("Inter", Font.PLAIN, 12));
        tblProviders.getTableHeader().setFont(new Font("Inter", Font.BOLD, 12));
        tblProviders.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollTable = new JScrollPane(tblProviders);
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
    public String getProviderName() { return txtName.getText().trim(); }
    public String getContactName() { return txtContact.getText().trim(); }
    public String getPhone() { return txtPhone.getText().trim(); }
    public String getEmail() { return txtEmail.getText().trim(); }
    public Integer getSelectedId() { return currentSelectedId; }

    public void fillForm(Provider p) {
        currentSelectedId = p.getId();
        txtName.setText(p.getName());
        txtContact.setText(p.getContactName());
        txtPhone.setText(p.getPhone());
        txtEmail.setText(p.getEmail());
    }

    public void clearForm() {
        currentSelectedId = null;
        txtName.setText("");
        txtContact.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        tblProviders.clearSelection();
    }

    public void setProviderList(List<Provider> providers) {
        tableModel.setRowCount(0);
        for (Provider p : providers) {
            tableModel.addRow(new Object[]{
                p.getId(),
                p.getName(),
                p.getContactName(),
                p.getPhone(),
                p.getEmail()
            });
        }
    }

    public Integer getSelectedProviderId() {
        int row = tblProviders.getSelectedRow();
        if (row != -1) {
            return (Integer) tblProviders.getValueAt(row, 0);
        }
        return null;
    }

    // ========== LISTENERS ==========
    public void addSaveListener(ActionListener l) { btnSave.addActionListener(l); }
    public void addUpdateListener(ActionListener l) { btnUpdate.addActionListener(l); }
    public void addDeleteListener(ActionListener l) { btnDelete.addActionListener(l); }
    public void addClearListener(ActionListener l) { btnClear.addActionListener(l); }
    public void addTableSelectionListener(ListSelectionListener l) { tblProviders.getSelectionModel().addListSelectionListener(l); }
}
