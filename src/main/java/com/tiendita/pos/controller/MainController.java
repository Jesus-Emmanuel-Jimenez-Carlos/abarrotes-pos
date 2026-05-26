package com.tiendita.pos.controller;

import com.tiendita.pos.model.*;
import com.tiendita.pos.service.*;
import com.tiendita.pos.view.LoginView;
import com.tiendita.pos.view.MainView;
import com.tiendita.pos.view.panels.*;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador Principal (Orquestador MVC) del sistema AbarrotesPOS.
 */
public class MainController {
    // Vistas
    private final LoginView loginView;
    private final MainView mainView;

    // Paneles del Workspace
    private final DashboardPanel dashboardPanel;
    private final InventoryPanel inventoryPanel;
    private final POSPanel posPanel;
    private final ReportsPanel reportsPanel;
    private final ProvidersPanel providersPanel;

    // Servicios
    private final UserService userService;
    private final ProductService productService;
    private final POSService posService;
    private final ReportService reportService;
    private final ProviderService providerService;

    public MainController(LoginView loginView, MainView mainView,
                          DashboardPanel dashboardPanel, InventoryPanel inventoryPanel,
                          POSPanel posPanel, ReportsPanel reportsPanel, ProvidersPanel providersPanel,
                          UserService userService, ProductService productService,
                          POSService posService, ReportService reportService, ProviderService providerService) {
        
        this.loginView = loginView;
        this.mainView = mainView;
        this.dashboardPanel = dashboardPanel;
        this.inventoryPanel = inventoryPanel;
        this.posPanel = posPanel;
        this.reportsPanel = reportsPanel;
        this.providersPanel = providersPanel;

        this.userService = userService;
        this.productService = productService;
        this.posService = posService;
        this.reportService = reportService;
        this.providerService = providerService;

        // Registrar Paneles en la Ventana Principal
        this.mainView.addPage("Dashboard", this.dashboardPanel);
        this.mainView.addPage("POS", this.posPanel);
        this.mainView.addPage("Inventario", this.inventoryPanel);
        this.mainView.addPage("Reportes", this.reportsPanel);
        this.mainView.addPage("Proveedores", this.providersPanel);

        // Inicializar Enlaces de Eventos
        initEventBindings();
    }

    /**
     * Muestra la pantalla de login para iniciar el flujo.
     */
    public void start() {
        loginView.setVisible(true);
    }

    /**
     * Enlaza todos los listeners (MVC Event Binding).
     */
    private void initEventBindings() {
        // --- 1. SEGURIDAD & INICIO DE SESIÓN ---
        loginView.addLoginListener(e -> handleLogin());
        
        // --- 2. NAVEGACIÓN LATERAL (SIDEBAR) ---
        mainView.addNavigationListener("Dashboard", e -> showDashboard());
        mainView.addNavigationListener("POS", e -> showPOS());
        mainView.addNavigationListener("Inventario", e -> showInventory());
        mainView.addNavigationListener("Reportes", e -> showReports());
        mainView.addNavigationListener("Proveedores", e -> showProviders());
        mainView.addNavigationListener("Logout", e -> handleLogout());

        // Escucha del Cierre de Ventana Principal para ejecutar Respaldos
        mainView.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleSystemExit();
            }
        });

        // --- 3. INVENTARIO & MERMAS ---
        inventoryPanel.addTableSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleInventoryTableSelection();
            }
        });
        inventoryPanel.addSaveProductListener(e -> handleSaveProduct());
        inventoryPanel.addUpdateProductListener(e -> handleUpdateProduct());
        inventoryPanel.addDeleteProductListener(e -> handleDeleteProduct());
        inventoryPanel.addClearProductListener(e -> inventoryPanel.clearProductForm());
        inventoryPanel.addRegisterMermaListener(e -> handleRegisterMerma());
        inventoryPanel.addSearchListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                handleProductSearch();
            }
        });

        // --- 4. PUNTO DE VENTA (POS) ---
        posPanel.addBarcodeSearchListener(e -> handlePOSProductScan());
        posPanel.addCancelListener(e -> posPanel.clearCart());
        posPanel.addCheckoutListener(e -> handlePOSCheckout());
        posPanel.addCartTableListener(this::handlePOSCartUpdate);

        // --- 5. REPORTES CONTABLES ---
        reportsPanel.addFilterListener(e -> handleFilterReports());
        reportsPanel.addViewDetailListener(e -> handleViewSaleDetail());

        // --- 6. PROVEEDORES ---
        providersPanel.addTableSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleProviderTableSelection();
            }
        });
        providersPanel.addSaveListener(e -> handleSaveProvider());
        providersPanel.addUpdateListener(e -> handleUpdateProvider());
        providersPanel.addDeleteListener(e -> handleDeleteProvider());
        providersPanel.addClearListener(e -> providersPanel.clearForm());
    }

    // ==========================================
    // 1. CONTROL DE ACCESOS Y SESIÓN
    // ==========================================
    private void handleLogin() {
        String username = loginView.getUsername();
        String password = loginView.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            loginView.showErrorMessage("Por favor llena todos los campos.");
            return;
        }

        boolean authenticated = userService.login(username, password);
        if (authenticated) {
            loginView.setVisible(false);
            loginView.clearForm();
            
            // Actualizar interfaz con datos de usuario
            mainView.updateSessionUI();
            
            // Cargar página inicial
            showDashboard();
            mainView.setVisible(true);
        } else {
            loginView.showErrorMessage("Credenciales incorrectas o usuario inactivo.");
        }
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(mainView, 
            "¿Estás seguro de que deseas cerrar tu sesión?", 
            "Cerrar Sesión", JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            userService.logout();
            mainView.setVisible(false);
            loginView.setVisible(true);
        }
    }

    private void handleSystemExit() {
        int choice = JOptionPane.showConfirmDialog(mainView, 
            "¿Deseas cerrar el sistema? Se realizará un respaldo automático de seguridad.", 
            "Cerrar Tiendita POS", JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            // Respaldar base de datos
            BackupService.performBackup();
            System.out.println("Salida ordenada del sistema completada.");
            System.exit(0);
        }
    }

    // ==========================================
    // 2. DASHBOARD Y CARGA DE METRICAS
    // ==========================================
    private void showDashboard() {
        mainView.showPage("Dashboard");
        refreshDashboardMetrics();
    }

    private void refreshDashboardMetrics() {
        try {
            LocalDateTime start = LocalDate.now().atStartOfDay();
            LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);

            double salesToday = reportService.getTotalSalesAmount(start, end);
            double profitToday = reportService.getNetUtility(start, end);
            int lowStockCount = reportService.getLowStockAlertCount();
            double inventoryValuation = reportService.getInventoryTotalValue();

            // Actualizar tarjetas de KPI
            dashboardPanel.updateStats(salesToday, profitToday, lowStockCount, inventoryValuation);

            // Cargar ventas recientes de hoy
            dashboardPanel.clearRecentSales();
            List<Sale> sales = reportService.topRecentSales(start, end, 8); // Método helper que agregaremos
            DateTimeFormatter hmFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            for (Sale s : sales) {
                dashboardPanel.addRecentSale(s.getId(), s.getSaleDate().format(hmFormatter), s.getTotal(), s.getPaymentMethod().getLabel());
            }

            // Cargar datos del gráfico (Top 5 productos más vendidos)
            List<ReportService.ProductSalesSummary> summaries = reportService.getTopSellingProducts();
            List<String> labels = summaries.stream().map(ReportService.ProductSalesSummary::getDescription).collect(Collectors.toList());
            List<Double> values = summaries.stream().map(ReportService.ProductSalesSummary::getTotalQuantity).collect(Collectors.toList());
            dashboardPanel.updateChart(labels, values);

        } catch (Exception e) {
            System.err.println("Error cargando estadísticas de dashboard: " + e.getMessage());
        }
    }

    // ==========================================
    // 3. GESTIÓN DE INVENTARIOS Y MERMAS
    // ==========================================
    private void showInventory() {
        mainView.showPage("Inventario");
        refreshInventoryList();
    }

    private void refreshInventoryList() {
        try {
            List<Product> products = productService.getAllProducts();
            inventoryPanel.setProductList(products);
        } catch (Exception e) {
            showError("No se pudo cargar el inventario: " + e.getMessage());
        }
    }

    private void handleInventoryTableSelection() {
        String barcode = inventoryPanel.getBarcodeFromSelectedRow();
        if (barcode != null) {
            try {
                Product p = productService.getProductByBarcode(barcode);
                if (p != null) {
                    inventoryPanel.fillProductForm(p);
                }
            } catch (Exception e) {
                showError("Error al seleccionar producto: " + e.getMessage());
            }
        }
    }

    private void handleSaveProduct() {
        try {
            Product p = new Product(
                inventoryPanel.getBarcode(),
                inventoryPanel.getDescription(),
                inventoryPanel.getBuyPrice(),
                inventoryPanel.getSellPrice(),
                inventoryPanel.getStock(),
                inventoryPanel.getMinStock(),
                inventoryPanel.getUnit()
            );

            productService.addProduct(p);
            showSuccess("¡Producto agregado al inventario exitosamente!");
            inventoryPanel.clearProductForm();
            refreshInventoryList();
        } catch (NumberFormatException ex) {
            showError("Verifica los precios y el stock. Deben ser valores numéricos válidos.");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void handleUpdateProduct() {
        try {
            Product p = new Product(
                inventoryPanel.getBarcode(),
                inventoryPanel.getDescription(),
                inventoryPanel.getBuyPrice(),
                inventoryPanel.getSellPrice(),
                inventoryPanel.getStock(),
                inventoryPanel.getMinStock(),
                inventoryPanel.getUnit()
            );

            productService.updateProduct(p);
            showSuccess("¡Producto actualizado exitosamente!");
            inventoryPanel.clearProductForm();
            refreshInventoryList();
        } catch (NumberFormatException ex) {
            showError("Precios y stock deben ser numéricos.");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void handleDeleteProduct() {
        String barcode = inventoryPanel.getBarcode();
        if (barcode.isEmpty()) {
            showError("Selecciona un producto para eliminar.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(mainView, 
            "¿Deseas eliminar permanentemente el producto '" + inventoryPanel.getDescription() + "'?", 
            "Eliminar Producto", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                productService.deleteProduct(barcode);
                showSuccess("Producto eliminado correctamente.");
                inventoryPanel.clearProductForm();
                refreshInventoryList();
            } catch (Exception e) {
                showError("No se pudo eliminar el producto: " + e.getMessage());
            }
        }
    }

    private void handleRegisterMerma() {
        try {
            String barcode = inventoryPanel.getSelectedMermaBarcode();
            double qty = inventoryPanel.getMermaQty();
            MermaReason reason = inventoryPanel.getMermaReason();

            if (barcode == null || barcode.isEmpty()) {
                showError("Selecciona un producto para reportar merma.");
                return;
            }

            productService.registerMerma(barcode, qty, reason);
            showSuccess("¡Merma registrada! El stock del producto ha sido reducido.");
            inventoryPanel.clearMermaForm();
            refreshInventoryList();
        } catch (NumberFormatException ex) {
            showError("La cantidad de merma debe ser un valor numérico.");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void handleProductSearch() {
        String query = inventoryPanel.getSearchQuery();
        try {
            List<Product> list = productService.searchProducts(query);
            inventoryPanel.setProductList(list);
        } catch (Exception e) {
            showError("Fallo en la búsqueda: " + e.getMessage());
        }
    }

    // ==========================================
    // 4. PUNTO DE VENTA (POS)
    // ==========================================
    private void showPOS() {
        mainView.showPage("POS");
        posPanel.clearCart();
    }

    private void handlePOSProductScan() {
        String query = posPanel.getSearchQuery();
        if (query.isEmpty()) return;

        try {
            // 1. Intentar buscar por código exacto primero
            Product p = productService.getProductByBarcode(query);
            
            // 2. Si no hay código exacto, buscar por descripción
            if (p == null) {
                List<Product> matches = productService.searchProducts(query);
                if (matches.size() == 1) {
                    p = matches.get(0);
                } else if (matches.size() > 1) {
                    // Mostrar lista modal simple para seleccionar
                    Product selected = selectProductFromSearchMatches(matches);
                    if (selected != null) {
                        p = selected;
                    }
                }
            }

            if (p != null) {
                posPanel.addProductToCart(p, 1.0); // Por defecto se añade 1 pza/kg
                posPanel.clearSearchQuery();
            } else {
                showError("El producto '" + query + "' no existe en el catálogo.");
            }
        } catch (Exception e) {
            showError("Error al escanear producto: " + e.getMessage());
        }
    }

    private Product selectProductFromSearchMatches(List<Product> matches) {
        String[] options = matches.stream()
            .map(p -> p.getBarcode() + " - " + p.getDescription() + " ($" + p.getSellPrice() + ")")
            .toArray(String[]::new);

        String choice = (String) JOptionPane.showInputDialog(mainView,
            "Múltiples productos coinciden, selecciona uno:",
            "Seleccionar Producto",
            JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (choice != null) {
            String barcode = choice.split(" - ")[0];
            return matches.stream().filter(p -> p.getBarcode().equals(barcode)).findFirst().orElse(null);
        }
        return null;
    }

    private void handlePOSCartUpdate(TableModelEvent e) {
        if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 2) {
            int row = e.getFirstRow();
            try {
                String barcode = (String) posPanel.getCartModel().getValueAt(row, 0);
                double qty = (double) posPanel.getCartModel().getValueAt(row, 2);
                
                Product p = productService.getProductByBarcode(barcode);
                if (p != null) {
                    if (qty <= 0) {
                        showError("La cantidad debe ser mayor a cero.");
                        posPanel.getCartModel().setValueAt(1.0, row, 2);
                        return;
                    }
                    
                    // Actualizar subtotal de la fila
                    posPanel.getCartModel().setValueAt(qty * p.getSellPrice(), row, 5);
                    posPanel.updateTotals();
                }
            } catch (Exception ex) {
                // Manejar error de parseo
            }
        }
    }

    private void handlePOSCheckout() {
        if (posPanel.getTotal() <= 0.0) {
            showError("El carrito está vacío. Agrega productos antes de cobrar.");
            return;
        }

        PaymentMethod method = posPanel.getPaymentMethod();
        double received = posPanel.getCashReceived();

        if (method == PaymentMethod.EFECTIVO && received < posPanel.getTotal()) {
            showError("El efectivo recibido debe ser igual o mayor al total.");
            return;
        }

        try {
            Sale sale = new Sale();
            sale.setPaymentMethod(method);

            // Reconstruir detalles desde la tabla del carrito
            for (int i = 0; i < posPanel.getCartModel().getRowCount(); i++) {
                String barcode = (String) posPanel.getCartModel().getValueAt(i, 0);
                String desc = (String) posPanel.getCartModel().getValueAt(i, 1);
                double qty = (double) posPanel.getCartModel().getValueAt(i, 2);
                double sellPrice = (double) posPanel.getCartModel().getValueAt(i, 4);

                SaleDetail detail = new SaleDetail(barcode, desc, qty, 0.0, sellPrice); // El costo real se asignará en el service
                sale.addDetail(detail);
            }

            // Procesar venta en BD transaccionalmente
            posService.processSale(sale);

            // Imprimir cambio y ticket virtual
            if (method == PaymentMethod.EFECTIVO) {
                double change = received - posPanel.getTotal();
                JOptionPane.showMessageDialog(mainView, 
                    String.format("¡Venta Cobrada con Éxito!\n\nTotal: $%.2f\nEfectivo: $%.2f\nCambio: $%.2f", 
                    posPanel.getTotal(), received, change), 
                    "Ticket de Venta #" + sale.getId(), JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(mainView, 
                    String.format("¡Venta por Tarjeta Aprobada!\n\nTotal: $%.2f", posPanel.getTotal()), 
                    "Ticket de Venta #" + sale.getId(), JOptionPane.INFORMATION_MESSAGE);
            }

            posPanel.clearCart();
        } catch (Exception e) {
            showError("Fallo al registrar la venta: " + e.getMessage());
        }
    }

    // ==========================================
    // 5. REPORTES CONTABLES
    // ==========================================
    private void showReports() {
        mainView.showPage("Reportes");
        handleFilterReports();
    }

    private void handleFilterReports() {
        try {
            LocalDate start = LocalDate.parse(reportsPanel.getStartDateStr());
            LocalDate end = LocalDate.parse(reportsPanel.getEndDateStr());

            LocalDateTime startDT = start.atStartOfDay();
            LocalDateTime endDT = end.atTime(LocalTime.MAX);

            double sales = reportService.getTotalSalesAmount(startDT, endDT);
            double profit = reportService.getNetUtility(startDT, endDT);

            reportsPanel.setSalesSummary(sales, profit);

            // Cargar historial en tabla
            List<Sale> list = reportService.getSalesByRange(startDT, endDT); // Se agregará
            reportsPanel.setSalesHistory(list);
        } catch (Exception e) {
            showError("Formato de fechas incorrecto (debe ser AAAA-MM-DD): " + e.getMessage());
        }
    }

    private void handleViewSaleDetail() {
        Integer saleId = reportsPanel.getSelectedSaleId();
        if (saleId == null) {
            showError("Selecciona una venta del historial para auditar.");
            return;
        }

        try {
            List<SaleDetail> details = reportService.getSaleDetails(saleId); // Se agregará
            reportsPanel.showSaleDetailsModal(mainView, saleId, details);
        } catch (Exception e) {
            showError("No se pudieron cargar los detalles: " + e.getMessage());
        }
    }

    // ==========================================
    // 6. PROVEEDORES
    // ==========================================
    private void showProviders() {
        mainView.showPage("Proveedores");
        refreshProvidersList();
    }

    private void refreshProvidersList() {
        try {
            List<Provider> list = providerService.getAllProviders();
            providersPanel.setProviderList(list);
        } catch (Exception e) {
            showError("No se pudieron cargar los proveedores: " + e.getMessage());
        }
    }

    private void handleProviderTableSelection() {
        Integer id = providersPanel.getSelectedProviderId();
        if (id != null) {
            try {
                Provider p = providerService.getById(id);
                if (p != null) {
                    providersPanel.fillForm(p);
                }
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }
    }

    private void handleSaveProvider() {
        try {
            Provider p = new Provider(
                providersPanel.getProviderName(),
                providersPanel.getContactName(),
                providersPanel.getPhone(),
                providersPanel.getEmail()
            );
            providerService.addProvider(p);
            showSuccess("¡Proveedor guardado exitosamente!");
            providersPanel.clearForm();
            refreshProvidersList();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void handleUpdateProvider() {
        Integer id = providersPanel.getSelectedId();
        if (id == null) {
            showError("Selecciona un proveedor de la tabla para actualizar.");
            return;
        }
        try {
            Provider p = new Provider(
                id,
                providersPanel.getProviderName(),
                providersPanel.getContactName(),
                providersPanel.getPhone(),
                providersPanel.getEmail()
            );
            providerService.updateProvider(p);
            showSuccess("¡Proveedor actualizado correctamente!");
            providersPanel.clearForm();
            refreshProvidersList();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void handleDeleteProvider() {
        Integer id = providersPanel.getSelectedId();
        if (id == null) {
            showError("Selecciona un proveedor para eliminar.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(mainView, 
            "¿Deseas eliminar permanentemente al proveedor '" + providersPanel.getProviderName() + "'?", 
            "Eliminar Proveedor", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                providerService.deleteProvider(id);
                showSuccess("Proveedor eliminado exitosamente.");
                providersPanel.clearForm();
                refreshProvidersList();
            } catch (Exception e) {
                showError("No se pudo eliminar: " + e.getMessage());
            }
        }
    }

    // ==========================================
    // UTILERÍAS COMUNES DE VENTANA
    // ==========================================
    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(mainView, msg, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(mainView, msg, "Atención", JOptionPane.WARNING_MESSAGE);
    }
}
