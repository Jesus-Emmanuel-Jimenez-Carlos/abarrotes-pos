package com.tiendita.pos.view;

import com.tiendita.pos.model.Session;
import com.tiendita.pos.view.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Contenedor visual principal del sistema POS. Estilo Sidebar con CardLayout.
 */
public class MainView extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel contentContainer;
    private final JPanel sidebarPanel;
    private final JLabel lblPageTitle;
    private final JLabel lblUserInfo;
    private final JButton btnToggleTheme;
    private final Map<String, JButton> menuButtons = new HashMap<>();

    public MainView() {
        setTitle("Sistema de Administración y POS - Tiendita de la Esquina");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Respaldar al cerrar
        setSize(1200, 780);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);

        // Layout Principal
        JPanel rootPanel = new JPanel(new BorderLayout());
        setContentPane(rootPanel);

        // 1. SIDEBAR (Navegación Lateral)
        sidebarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (ThemeManager.isDarkMode()) {
                    g2.setColor(new Color(15, 23, 42)); // Slate 900
                } else {
                    g2.setColor(new Color(241, 245, 249)); // Slate 100
                }
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(240, getHeight()));
        sidebarPanel.setBorder(new EmptyBorder(20, 15, 20, 15));
        rootPanel.add(sidebarPanel, BorderLayout.WEST);

        // Header del Sidebar (Logo y Nombre)
        JLabel lblLogo = new JLabel("🏪 ");
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblAppName = new JLabel("Tiendita POS");
        lblAppName.setFont(new Font("Inter", Font.BOLD, 20));
        lblAppName.setForeground(ThemeManager.getTextColor());
        lblAppName.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebarPanel.add(lblLogo);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(lblAppName);
        sidebarPanel.add(Box.createVerticalStrut(30));

        // Separador sutil
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(210, 2));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(sep);
        sidebarPanel.add(Box.createVerticalStrut(20));

        // Botones de Navegación del Sidebar
        createMenuButton("Dashboard", "📊  Dashboard");
        createMenuButton("POS", "🛒  Punto de Venta");
        createMenuButton("Inventario", "📦  Inventario / CRUD");
        createMenuButton("Reportes", "📈  Reportes Financieros");
        createMenuButton("Proveedores", "🤝  Proveedores");
        
        sidebarPanel.add(Box.createVerticalGlue()); // Empujar botón salir al fondo

        createMenuButton("Logout", "🚪  Cerrar Sesión");

        // 2. HEADER SUPERIOR
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(ThemeManager.isDarkMode() ? new Color(30, 41, 59) : Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Línea inferior sutil
                g2.setColor(ThemeManager.isDarkMode() ? new Color(255, 255, 255, 10) : new Color(0, 0, 0, 10));
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
            }
        };
        headerPanel.setPreferredSize(new Dimension(getWidth(), 65));
        headerPanel.setBorder(new EmptyBorder(10, 25, 10, 25));
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        lblPageTitle = new JLabel("Dashboard");
        lblPageTitle.setFont(new Font("Inter", Font.BOLD, 20));
        lblPageTitle.setForeground(ThemeManager.getTextColor());
        headerPanel.add(lblPageTitle, BorderLayout.WEST);

        // Lado Derecho del Header (Usuario y Switch Oscuro)
        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        headerRight.setOpaque(false);
        
        lblUserInfo = new JLabel("Cargando sesión...");
        lblUserInfo.setFont(new Font("Inter", Font.BOLD, 12));
        lblUserInfo.setForeground(ThemeManager.getTextColor());
        headerRight.add(lblUserInfo);

        btnToggleTheme = new JButton(ThemeManager.isDarkMode() ? "☀️ Claro" : "🌙 Oscuro");
        btnToggleTheme.setFont(new Font("Inter", Font.BOLD, 11));
        btnToggleTheme.setFocusPainted(false);
        headerRight.add(btnToggleTheme);

        headerPanel.add(headerRight, BorderLayout.EAST);

        // 3. WORKSPACE CONTAINER (CardLayout)
        cardLayout = new CardLayout();
        contentContainer = new JPanel(cardLayout);
        contentContainer.setBorder(new EmptyBorder(25, 25, 25, 25));
        rootPanel.add(contentContainer, BorderLayout.CENTER);

        // Evento de alternancia de tema global
        btnToggleTheme.addActionListener(e -> {
            ThemeManager.toggleTheme(this);
            btnToggleTheme.setText(ThemeManager.isDarkMode() ? "☀️ Claro" : "🌙 Oscuro");
            SwingUtilities.updateComponentTreeUI(sidebarPanel);
            updateSessionUI();
        });
    }

    private void createMenuButton(String key, String label) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Inter", Font.BOLD, 13));
        btn.setMaximumSize(new Dimension(210, 40));
        btn.setPreferredSize(new Dimension(210, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(0, 15, 0, 15));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        
        // Estilo según tema por defecto
        btn.setContentAreaFilled(false);
        
        sidebarPanel.add(btn);
        sidebarPanel.add(Box.createVerticalStrut(10));
        menuButtons.put(key, btn);
    }

    /**
     * Añade un panel funcional al contenedor central y lo mapea a su ID.
     */
    public void addPage(String key, JPanel panel) {
        contentContainer.add(panel, key);
    }

    /**
     * Muestra el panel asociado a la clave y resalta el botón correspondiente en el menú.
     */
    public void showPage(String key) {
        cardLayout.show(contentContainer, key);
        lblPageTitle.setText(key.equals("POS") ? "Punto de Venta" : key);

        // Resetear colores de botones e iluminar el activo
        menuButtons.forEach((name, btn) -> {
            if (name.equals(key)) {
                btn.setForeground(Color.WHITE);
                btn.setBackground(ThemeManager.COLOR_PRIMARY);
                btn.setContentAreaFilled(true);
                btn.setOpaque(true);
            } else {
                btn.setForeground(ThemeManager.getTextColor());
                btn.setContentAreaFilled(false);
                btn.setOpaque(false);
            }
        });
    }

    /**
     * Refresca los datos del usuario en la parte superior.
     */
    public void updateSessionUI() {
        if (Session.getInstance().isActive()) {
            lblUserInfo.setText("👤 " + Session.getInstance().getCurrentUser().toString());
            // Ocultar pestaña reportes para cajeros estándar
            boolean isAdmin = Session.getInstance().isAdmin();
            menuButtons.get("Reportes").setVisible(isAdmin);
            menuButtons.get("Proveedores").setVisible(isAdmin);
        } else {
            lblUserInfo.setText("Sin sesión activa");
        }
        lblUserInfo.setForeground(ThemeManager.getTextColor());
    }

    public void addNavigationListener(String key, ActionListener listener) {
        JButton btn = menuButtons.get(key);
        if (btn != null) {
            btn.addActionListener(listener);
        }
    }
}
