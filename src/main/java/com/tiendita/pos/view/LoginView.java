package com.tiendita.pos.view;

import com.tiendita.pos.view.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Pantalla de inicio de sesión premium y segura con soporte dinámico de temas.
 */
public class LoginView extends JFrame {
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final JButton btnLogin;
    private final JButton btnToggleTheme;
    private final JLabel lblErrorMessage;

    public LoginView() {
        setTitle("Acceso al Sistema POS - Abarrotes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 480);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel Principal con fondo gradiente sutil
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp;
                if (ThemeManager.isDarkMode()) {
                    gp = new GradientPaint(0, 0, new Color(15, 23, 42), 0, getHeight(), new Color(30, 41, 59));
                } else {
                    gp = new GradientPaint(0, 0, new Color(241, 245, 249), 0, getHeight(), new Color(226, 232, 240));
                }
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        setContentPane(mainPanel);

        // Barra superior con botón de tema
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topBar.setOpaque(false);
        btnToggleTheme = new JButton(ThemeManager.isDarkMode() ? "☀️ Claro" : "🌙 Oscuro");
        btnToggleTheme.setFont(new Font("Inter", Font.BOLD, 11));
        btnToggleTheme.setFocusPainted(false);
        topBar.add(btnToggleTheme);
        mainPanel.add(topBar, BorderLayout.NORTH);

        // Contenedor del Formulario (Tarjeta Central)
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getCardBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(ThemeManager.isDarkMode() ? new Color(255, 255, 255, 15) : new Color(0, 0, 0, 15));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setLayout(new GridBagLayout());
        cardPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;

        // Logo / Icono descriptivo
        JLabel lblLogo = new JLabel("🏪");
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        cardPanel.add(lblLogo, gbc);

        // Título del POS
        JLabel lblHeader = new JLabel("Tiendita POS");
        lblHeader.setFont(new Font("Inter", Font.BOLD, 22));
        lblHeader.setHorizontalAlignment(SwingConstants.CENTER);
        lblHeader.setForeground(ThemeManager.getTextColor());
        gbc.gridy = 1;
        cardPanel.add(lblHeader, gbc);

        // Subtítulo
        JLabel lblSub = new JLabel("Inicia sesión para abrir caja");
        lblSub.setFont(new Font("Inter", Font.PLAIN, 12));
        lblSub.setHorizontalAlignment(SwingConstants.CENTER);
        lblSub.setForeground(new Color(148, 163, 184)); // Slate 400
        gbc.gridy = 2;
        cardPanel.add(lblSub, gbc);

        // Espaciador
        gbc.gridy = 3;
        cardPanel.add(Box.createVerticalStrut(10), gbc);

        // Campo Usuario
        JLabel lblUser = new JLabel("Nombre de Usuario");
        lblUser.setFont(new Font("Inter", Font.BOLD, 12));
        lblUser.setForeground(ThemeManager.getTextColor());
        gbc.gridy = 4;
        cardPanel.add(lblUser, gbc);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Inter", Font.PLAIN, 13));
        txtUsername.putClientProperty("JTextField.placeholderText", "Ingresa tu usuario");
        txtUsername.setPreferredSize(new Dimension(280, 36));
        gbc.gridy = 5;
        cardPanel.add(txtUsername, gbc);

        // Campo Contraseña
        JLabel lblPass = new JLabel("Contraseña");
        lblPass.setFont(new Font("Inter", Font.BOLD, 12));
        lblPass.setForeground(ThemeManager.getTextColor());
        gbc.gridy = 6;
        cardPanel.add(lblPass, gbc);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Inter", Font.PLAIN, 13));
        txtPassword.putClientProperty("JTextField.placeholderText", "Ingresa tu clave");
        txtPassword.setPreferredSize(new Dimension(280, 36));
        gbc.gridy = 7;
        cardPanel.add(txtPassword, gbc);

        // Mensaje de Error
        lblErrorMessage = new JLabel(" ");
        lblErrorMessage.setFont(new Font("Inter", Font.BOLD, 11));
        lblErrorMessage.setForeground(ThemeManager.COLOR_DANGER);
        lblErrorMessage.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 8;
        cardPanel.add(lblErrorMessage, gbc);

        // Botón Login
        btnLogin = new JButton("Iniciar Sesión 🚀");
        btnLogin.setFont(new Font("Inter", Font.BOLD, 13));
        btnLogin.setBackground(ThemeManager.COLOR_PRIMARY);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(280, 40));
        gbc.gridy = 9;
        cardPanel.add(btnLogin, gbc);

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        // Manejar cambio de tema local
        btnToggleTheme.addActionListener(e -> {
            ThemeManager.toggleTheme(this);
            btnToggleTheme.setText(ThemeManager.isDarkMode() ? "☀️ Claro" : "🌙 Oscuro");
            cardPanel.repaint();
            mainPanel.repaint();
        });
    }

    public String getUsername() {
        return txtUsername.getText().trim();
    }

    public String getPassword() {
        return new String(txtPassword.getPassword());
    }

    public void showErrorMessage(String msg) {
        lblErrorMessage.setText(msg);
    }

    public void clearForm() {
        txtUsername.setText("");
        txtPassword.setText("");
        lblErrorMessage.setText(" ");
    }

    public void addLoginListener(ActionListener listener) {
        btnLogin.addActionListener(listener);
        txtPassword.addActionListener(listener); // Enter en el password dispara login
    }
}
