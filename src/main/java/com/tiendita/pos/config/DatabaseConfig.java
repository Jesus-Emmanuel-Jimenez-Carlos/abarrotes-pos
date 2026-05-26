package com.tiendita.pos.config;

import org.mindrot.jbcrypt.BCrypt;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Gestor de la conexión a la base de datos local SQLite y migraciones de esquema inicial.
 */
public class DatabaseConfig {
    private static final String DB_DIR = "data";
    private static final String DB_NAME = "abarrotes.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_DIR + "/" + DB_NAME;

    static {
        try {
            // Cargar el driver JDBC de SQLite de manera explícita
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar el Driver JDBC de SQLite: " + e.getMessage());
        }
    }

    /**
     * Obtiene una nueva conexión a la base de datos SQLite.
     * Habilita soporte para llaves foráneas.
     */
    public static Connection getConnection() throws Exception {
        // Asegurar que el directorio de datos existe
        File directory = new File(DB_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        Connection conn = DriverManager.getConnection(DB_URL);
        // Habilitar restricciones de clave foránea en la conexión SQLite activa
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    /**
     * Inicializa la base de datos aplicando el esquema DDL y poblando datos por defecto.
     */
    public static void initializeDatabase() {
        System.out.println("Inicializando base de datos local SQLite...");
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 1. Leer esquema de base de datos desde los recursos
            InputStream is = DatabaseConfig.class.getClassLoader().getResourceAsStream("db/schema.sql");
            if (is == null) {
                throw new RuntimeException("No se encontró el archivo db/schema.sql en los recursos.");
            }
            
            String schemaSql;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                schemaSql = reader.lines().collect(Collectors.joining("\n"));
            }
            
            // 2. Ejecutar instrucciones SQL separadas por punto y coma (excluyendo comentarios vacíos)
            String[] queries = schemaSql.split(";");
            for (String query : queries) {
                String trimmedQuery = query.trim();
                if (trimmedQuery.isEmpty() || trimmedQuery.startsWith("--")) {
                    continue;
                }
                stmt.execute(trimmedQuery);
            }
            System.out.println("Esquema de base de datos aplicado correctamente.");

            // 3. Crear usuario administrador inicial si no existe ninguno
            boolean hasUsers = false;
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
                if (rs.next()) {
                    hasUsers = rs.getInt(1) > 0;
                }
            }

            if (!hasUsers) {
                String defaultAdminUser = "admin";
                String defaultAdminPass = "admin123";
                String passwordHash = BCrypt.hashpw(defaultAdminPass, BCrypt.gensalt());
                
                String seedSql = String.format(
                    "INSERT INTO users (username, password_hash, role, active) VALUES ('%s', '%s', 'ADMINISTRADOR', 1)",
                    defaultAdminUser, passwordHash
                );
                stmt.execute(seedSql);
                System.out.println("------------------------------------------------------------------");
                System.out.println("¡Base de datos vacía! Se ha creado el Administrador inicial:");
                System.out.println("Usuario: " + defaultAdminUser);
                System.out.println("Contraseña: " + defaultAdminPass);
                System.out.println("------------------------------------------------------------------");
            }

        } catch (Exception e) {
            System.err.println("Falla crítica al inicializar la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retorna la ruta absoluta del archivo de la base de datos (útil para el Backup).
     */
    public static File getDatabaseFile() {
        return new File(DB_DIR, DB_NAME);
    }
}
