package com.tiendita.pos.service;

import com.tiendita.pos.config.DatabaseConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Servicio automatizado de respaldos locales de la base de datos SQLite.
 */
public class BackupService {
    private static final String BACKUP_DIR = "backups";
    private static final int MAX_BACKUPS = 10; // Mantener solo los últimos 10 respaldos

    /**
     * Genera un respaldo físico del archivo de la base de datos actual.
     */
    public static synchronized void performBackup() {
        System.out.println("Iniciando rutina automatizada de respaldo de base de datos...");
        
        File dbFile = DatabaseConfig.getDatabaseFile();
        if (!dbFile.exists()) {
            System.err.println("Advertencia: No existe el archivo de base de datos original para respaldar.");
            return;
        }

        // Crear carpeta de respaldos si no existe
        File backupDir = new File(BACKUP_DIR);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }

        // Generar nombre de archivo con marca de tiempo
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String backupFileName = "backup_" + timestamp + ".db";
        File destFile = new File(backupDir, backupFileName);

        try {
            // Copia física del archivo SQLite
            Files.copy(dbFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Respaldo creado con éxito en: " + destFile.getAbsolutePath());
            
            // Limpieza de respaldos antiguos (mantener un tope de 10 archivos)
            rotateBackups();
        } catch (IOException e) {
            System.err.println("Fallo al escribir el respaldo de base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Limpia los respaldos más antiguos si se supera el límite máximo permitido.
     */
    private static void rotateBackups() {
        File backupDir = new File(BACKUP_DIR);
        File[] files = backupDir.listFiles((dir, name) -> name.startsWith("backup_") && name.endsWith(".db"));
        
        if (files != null && files.length > MAX_BACKUPS) {
            // Ordenar por fecha de última modificación (los más viejos primero)
            Arrays.sort(files, Comparator.comparingLong(File::lastModified));
            
            int toDeleteCount = files.length - MAX_BACKUPS;
            System.out.println("Rotando respaldos antiguos. Se eliminarán los " + toDeleteCount + " más antiguos.");
            
            for (int i = 0; i < toDeleteCount; i++) {
                File toDelete = files[i];
                if (toDelete.delete()) {
                    System.out.println("Respaldo antiguo eliminado: " + toDelete.getName());
                } else {
                    System.err.println("No se pudo eliminar el respaldo: " + toDelete.getName());
                }
            }
        }
    }
}
