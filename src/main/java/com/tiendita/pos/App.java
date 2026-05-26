package com.tiendita.pos;

import com.tiendita.pos.config.DatabaseConfig;
import com.tiendita.pos.controller.MainController;
import com.tiendita.pos.repository.*;
import com.tiendita.pos.repository.impl.*;
import com.tiendita.pos.service.*;
import com.tiendita.pos.view.LoginView;
import com.tiendita.pos.view.MainView;
import com.tiendita.pos.view.panels.*;
import com.tiendita.pos.view.theme.ThemeManager;

import javax.swing.*;

/**
 * Clase principal de inicio (Bootstrap) para la aplicación Tiendita POS.
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Iniciando Tiendita POS Engine...");

        // 1. Inicializar base de datos local SQLite y migraciones de esquema
        DatabaseConfig.initializeDatabase();

        // 2. Establecer el Look & Feel de FlatLaf moderno de forma inicial
        ThemeManager.applyTheme();

        // 3. Instanciar Repositorios (Inyección de Dependencias manual limpia)
        UserRepository userRepository = new SQLiteUserRepository();
        ProductRepository productRepository = new SQLiteProductRepository();
        MermaRepository mermaRepository = new SQLiteMermaRepository();
        SaleRepository saleRepository = new SQLiteSaleRepository();
        ProviderRepository providerRepository = new SQLiteProviderRepository();

        // 4. Instanciar Servicios con inyección de repositorios
        UserService userService = new UserService(userRepository);
        ProductService productService = new ProductService(productRepository, mermaRepository);
        POSService posService = new POSService(saleRepository, productRepository);
        ReportService reportService = new ReportService(saleRepository);
        ProviderService providerService = new ProviderService(providerRepository);

        // 5. Crear Vistas y Paneles
        LoginView loginView = new LoginView();
        MainView mainView = new MainView();

        DashboardPanel dashboardPanel = new DashboardPanel();
        InventoryPanel inventoryPanel = new InventoryPanel();
        POSPanel posPanel = new POSPanel();
        ReportsPanel reportsPanel = new ReportsPanel();
        ProvidersPanel providersPanel = new ProvidersPanel();

        // 6. Instanciar el Controlador Orquestador (MVC)
        MainController controller = new MainController(
            loginView, mainView,
            dashboardPanel, inventoryPanel, posPanel, reportsPanel, providersPanel,
            userService, productService, posService, reportService, providerService
        );

        // 7. Arrancar en el hilo de despacho de eventos de Swing (Seguridad UI)
        SwingUtilities.invokeLater(() -> {
            try {
                controller.start();
                System.out.println("Tiendita POS listo para operar. ¡Excelente ventas!");
            } catch (Exception e) {
                System.err.println("Falla crítica en el arranque de la interfaz gráfica: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
