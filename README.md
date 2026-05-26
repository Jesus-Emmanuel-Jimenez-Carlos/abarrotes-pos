# 🏪 Tiendita POS & CRUD System

[![Java Version](https://img.shields.io/badge/Java-17%20%7C%2021-orange.svg?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Database](https://img.shields.io/badge/Database-SQLite-blue.svg?style=flat-square&logo=sqlite)](https://www.sqlite.org/)
[![UI Style](https://img.shields.io/badge/UI%20Look-FlatLaf%20Dark%2FLight-blueviolet.svg?style=flat-square)](https://www.formdev.com/flatlaf/)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20MVC-success.svg?style=flat-square)](#)

Un sistema de **Punto de Venta (POS) e Inventario** de escritorio ultra-rápido, moderno y robusto, diseñado a la medida para el mercado de las **"tienditas de la esquina"** y comercios minoristas en México.

An ultra-fast, modern, and robust desktop **Point of Sale (POS) & Inventory** system tailored specifically for Mexican corner stores ("tienditas de la esquina") and retail shops.

---

## 🌎 Idioma / Language
- [Español (Presentación de Negocio & Guía)](#español---manual-de-negocio)
- [English (Technical & Recruiter Documentation)](#english---technical-documentation)

---

# ESPAÑOL - MANUAL DE NEGOCIO

### ⚡ ¿Qué problema resuelve Tiendita POS?
Para miles de comerciantes en México, el control diario de su tienda es un dolor de cabeza:
* **El misterio del dinero**: Al final del día no sabes exactamente cuánto ganaste de utilidad real y cuánto es solo el costo de surtir.
* **El robo hormiga y pérdidas**: Los productos caducados o dañados ("mermas") se registran tarde o simplemente se ignoran, evaporando tus ganancias.
* **Filas lentas**: Cobrar a mano o en sistemas lentos ahuyenta a tus clientes más ocupados.
* **Miedo a perder tu información**: Que se apague la computadora y pierdas toda tu base de datos de meses de trabajo.

**Tiendita POS sana ese dolor por completo.** Te da el control absoluto de tu negocio con una interfaz premium, rápida y súper sencilla de usar, sin importar tu nivel de experiencia con computadoras.

---

## 🌟 Características Destacadas para el Comerciante

### 1. 🛒 Punto de Venta Ultra-Rápido
Soporte nativo para escáneres de código de barras. Solo pasa el producto y el sistema lo añade al carrito al instante.
* **Cálculo de cambio inteligente**: Escribe cuánto te da el cliente en efectivo y visualiza el cambio exacto en letras gigantes para evitar errores.
* **Tarjetas y Efectivo**: Elige cómo te pagan para cuadrar perfectamente tu caja al final del día.

### 2. 📦 Inventario Inteligente y Alertas Visuales
Un catálogo completo de tus productos donde puedes ver de reojo qué se está agotando.
* **Semáforo de Abastecimiento**: Si un producto llega a su límite mínimo, la tabla se pinta en **color rojo de advertencia**, indicándote de inmediato qué debes comprar al proveedor.
* **Registro de Mermas**: Descuenta productos dañados, caducados o robados especificando la razón, manteniendo tus cuentas claras.

### 3. 📈 Reportes Financieros y Ganancia Real (Utilidad Neta)
¡Deja la libreta en el pasado!
* **Utilidad Neta Exacta**: A diferencia de otros sistemas que solo te dicen cuánto vendiste, Tiendita POS calcula tu ganancia real restando el costo al que le compraste a los proveedores (`Venta - Costo`).
* **Visualización Dinámica**: Gráficas sencillas que te muestran tus 5 productos estrella más vendidos de un solo vistazo.
* **Historial de Tickets**: Audita y abre el detalle de cualquier ticket del pasado de manera transparente.

### 4. ☀️ / 🌙 Modo Claro y Modo Oscuro Nativos
Cuida tu vista durante las largas jornadas de trabajo. Cambia el diseño visual con un solo botón en cualquier momento.

### 5. 🛡️ Respaldo de Seguridad Automático (Cero Pérdida de Datos)
La base de datos se guarda en tu propia computadora. **Al cerrar el sistema, Tiendita POS realiza automáticamente una copia de seguridad** con fecha y hora. Si tu máquina falla, tus datos están a salvo.

---

# ENGLISH - TECHNICAL DOCUMENTATION

This project represents a **Senior-level portfolio MVP** demonstrating industrial-grade software engineering, strict adherence to Object-Oriented design principles (SOLID), and premium Desktop UX implementation in Java.

## 🏗️ Architectural Overview & Design Patterns

The application is engineered using a highly-decoupled, layered **Model-View-Controller (MVC)** design pattern, separating domain rules, transactional services, database layers, and UI rendering:

1. **Model Layer**: Pure POJOs representing core business entities (`User`, `Product`, `Sale`, `SaleDetail`, `Merma`, `Provider`) along with strong, type-safe enums (`Role`, `UnitType`, `PaymentMethod`, `MermaReason`).
2. **Repository Layer (DAO)**: Interface-driven data access object design (`UserRepository`, `ProductRepository`, etc.) implemented with concrete JDBC mappings for SQLite.
3. **Service Layer**: Houses core business invariants:
   * **`UserService`**: Handles secure user authentication using **BCrypt cryptographic password hashing** (never storing raw passwords).
   * **`POSService`**: Processes checkouts **transactionally** (`conn.setAutoCommit(false)`), deducting inventory and inserting sale items atomically, triggering full rollback on stock conflicts.
   * **`ReportService`**: Provides aggregated SQLite statistical calculations for valuations and margins.
   * **`BackupService`**: Safely performs scheduled filesystem backups with a rolling window algorithm capped at 10 database files.
4. **View Layer**: Beautiful UI written in Java Swing styled with **FlatLaf Look & Feel** (with native runtime Light/Dark mode toggling and custom Graphics2D chart vector drawing).
5. **Controller Layer**: Action-event listener bindings that bridge UI frames and transactional service injections.

```
                  [ USER ACTION ]
                         │
                         ▼
┌────────────────────────────────────────────────────────┐
│                      VIEW LAYER                        │
│   (LoginView, MainView, POSPanel, InventoryPanel...)   │
└───────────┬────────────────────────▲───────────────────┘
            │ Event Binding          │ Refreshes State
            ▼                        │
┌────────────────────────────────────┴───────────────────┐
│                    CONTROLLER LAYER                    │
│                    (MainController)                    │
└───────────┬────────────────────────────────────────────┘
            │ Injects Business Logic
            ▼
┌────────────────────────────────────────────────────────┐
│                     SERVICE LAYER                      │
│     (POSService, UserService, ReportService...)        │
└───────────┬────────────────────────▲───────────────────┘
            │ Reads/Writes           │ Database Connection
            ▼                        │
┌────────────────────────────────────┴───────────────────┐
│                REPOSITORY / DAO LAYER                  │
│       (SQLiteProductRepository, SQLiteSaleRepository)  │
└───────────────────────────┬────────────────────────────┘
                            │ SQL Queries
                            ▼
┌────────────────────────────────────────────────────────┐
│                   SQLITE LOCAL DB                      │
│                  (data/abarrotes.db)                   │
└────────────────────────────────────────────────────────┘
```

---

## 🛠️ Technology Stack & Dependencies

- **Core**: Java SE 17+ (LTS)
- **Build & Lifecycle**: Apache Maven
- **Database Engine**: SQLite-JDBC Driver (`org.xerial:sqlite-jdbc`)
- **Theme & Vector Styling**: FlatLaf Core (`com.formdev:flatlaf`) & FlatLaf Extras
- **Security & Hashing**: jBCrypt (`org.mindrot:jbcrypt`)
- **Testing Suite**: JUnit 5 (Jupiter Engine)

---

## ⚡ Setup & Execution Instructions

Ensure you have **Java 17+** and **Maven** installed on your system.

### 1. Clone the repository
```bash
git clone https://github.com/Jesus-Emmanuel-Jimenez-Carlos/abarrotes-pos.git
cd abarrotes-pos
```

### 2. Run Automated Unit Tests
Verify database transactional constraints and cryptographic BCrypt properties are in order:
```bash
mvn test
```

### 3. Compile and Package the Executable (Fat JAR)
Build a single self-contained executable JAR containing all compiled classes and libraries:
```bash
mvn clean package
```
This produces `target/abarrotes-pos-1.0.0-jar-with-dependencies.jar`.

### 4. Run the Application
You can launch the POS directly using Maven:
```bash
mvn exec:java -Dexec.mainClass="com.tiendita.pos.App"
```
Or execute the built executable package:
```bash
java -jar target/abarrotes-pos-1.0.0-jar-with-dependencies.jar
```

---

## 🔑 Default Credentials (First Boot Seed)

When launched for the first time, the system will detect an empty database and automatically seed a default administrator user:
* **Username**: `admin`
* **Password**: `admin123`

> [!IMPORTANT]
> Change the default password or register a new custom Cashier (`Cajero`) user once logged in to preserve security.

---

## 📝 SOLID & Clean Code Adherence

* **Single Responsibility (SRP)**: View panels only lay out components; Controllers handle event coordination; Services enforce business calculations; Repositories map data.
* **Dependency Inversion (DIP)**: Controllers and Services depend on Repository interfaces, not on the concrete SQLite JDBC implementation.
* **Interface Segregation (ISP)**: Separate DAOs ensure that product modifications do not interfere with sales logs or suppliers listings.
