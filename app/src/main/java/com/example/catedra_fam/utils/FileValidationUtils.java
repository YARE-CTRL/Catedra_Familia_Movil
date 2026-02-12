package com.example.catedra_fam.utils;

import android.webkit.MimeTypeMap;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Utilidades para validación de archivos de evidencias
 * Basado en las especificaciones UX para optimizar la experiencia del usuario
 */
public class FileValidationUtils {

    // Tamaño máximo permitido: 50MB
    public static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB en bytes

    // Formatos permitidos según especificaciones del backend
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
        // Documentos
        "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt",
        // Imágenes
        "jpg", "jpeg", "png", "webp", "gif",
        // Videos
        "mp4", "mov", "mkv", "avi",
        // Audio
        "mp3", "wav", "m4a"
    );

    // Tipos MIME permitidos
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
        // Documentos
        "application/pdf", "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/vnd.ms-powerpoint",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        "text/plain",
        // Imágenes
        "image/jpeg", "image/png", "image/webp", "image/gif",
        // Videos
        "video/mp4", "video/quicktime", "video/x-msvideo",
        // Audio
        "audio/mpeg", "audio/wav", "audio/mp4"
    );

    /**
     * Resultado de validación de archivo
     */
    public static class ValidationResult {
        private boolean isValid;
        private String errorMessage;
        private String userFriendlyMessage;

        public ValidationResult(boolean isValid, String errorMessage, String userFriendlyMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
            this.userFriendlyMessage = userFriendlyMessage;
        }

        public boolean isValid() { return isValid; }
        public String getErrorMessage() { return errorMessage; }
        public String getUserFriendlyMessage() { return userFriendlyMessage; }

        public static ValidationResult success() {
            return new ValidationResult(true, null, "Archivo válido");
        }

        public static ValidationResult error(String error, String userMessage) {
            return new ValidationResult(false, error, userMessage);
        }
    }

    /**
     * Valida un archivo para evidencia de tarea
     * @param file Archivo a validar
     * @return ValidationResult con el resultado de la validación
     */
    public static ValidationResult validateFile(File file) {
        if (file == null || !file.exists()) {
            return ValidationResult.error("FILE_NOT_EXISTS", "El archivo no existe");
        }

        // Validar tamaño
        long fileSize = file.length();
        if (fileSize > MAX_FILE_SIZE) {
            String sizeInMB = String.format("%.1f", fileSize / (1024.0 * 1024.0));
            return ValidationResult.error("FILE_TOO_LARGE",
                "El archivo es muy grande (" + sizeInMB + "MB). El tamaño máximo es 50MB");
        }

        if (fileSize == 0) {
            return ValidationResult.error("FILE_EMPTY", "El archivo está vacío");
        }

        // Validar extensión
        String fileName = file.getName();
        String extension = getFileExtension(fileName);

        if (extension.isEmpty()) {
            return ValidationResult.error("NO_EXTENSION",
                "El archivo debe tener una extensión válida");
        }

        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            return ValidationResult.error("INVALID_EXTENSION",
                "Formato no permitido. Formatos válidos: PDF, DOC, XLS, PPT, JPG, PNG, MP4, etc.");
        }

        return ValidationResult.success();
    }

    /**
     * Valida múltiples archivos
     * @param files Lista de archivos a validar
     * @return ValidationResult del primer error encontrado, o success si todos son válidos
     */
    public static ValidationResult validateFiles(List<File> files) {
        if (files == null || files.isEmpty()) {
            return ValidationResult.success(); // Permitir envío sin archivos
        }

        long totalSize = 0;
        for (File file : files) {
            ValidationResult result = validateFile(file);
            if (!result.isValid()) {
                return result;
            }
            totalSize += file.length();
        }

        // Validar tamaño total (opcional - puede ser útil para la UX)
        if (totalSize > MAX_FILE_SIZE * 3) { // 150MB total máximo
            String totalSizeInMB = String.format("%.1f", totalSize / (1024.0 * 1024.0));
            return ValidationResult.error("TOTAL_SIZE_TOO_LARGE",
                "El tamaño total de archivos es muy grande (" + totalSizeInMB + "MB)");
        }

        return ValidationResult.success();
    }

    /**
     * Obtiene la extensión de un archivo
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }

    /**
     * Obtiene un mensaje amigable del tamaño del archivo
     */
    public static String getFileSizeString(long sizeInBytes) {
        if (sizeInBytes < 1024) {
            return sizeInBytes + " B";
        } else if (sizeInBytes < 1024 * 1024) {
            return String.format("%.1f KB", sizeInBytes / 1024.0);
        } else {
            return String.format("%.1f MB", sizeInBytes / (1024.0 * 1024.0));
        }
    }

    /**
     * Verifica si un archivo es una imagen
     */
    public static boolean isImageFile(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        return Arrays.asList("jpg", "jpeg", "png", "webp", "gif").contains(extension);
    }

    /**
     * Verifica si un archivo es un video
     */
    public static boolean isVideoFile(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        return Arrays.asList("mp4", "mov", "mkv", "avi").contains(extension);
    }

    /**
     * Verifica si un archivo es un documento
     */
    public static boolean isDocumentFile(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        return Arrays.asList("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt").contains(extension);
    }

    /**
     * Obtiene un icono apropiado para el tipo de archivo (para la UI)
     */
    public static String getFileTypeIcon(String fileName) {
        if (isImageFile(fileName)) return "🖼️";
        if (isVideoFile(fileName)) return "🎥";
        if (isDocumentFile(fileName)) return "📄";
        if (getFileExtension(fileName).toLowerCase().equals("pdf")) return "📕";
        return "📎";
    }

    /**
     * Obtiene el tipo MIME de un archivo
     */
    public static String getMimeType(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
}
