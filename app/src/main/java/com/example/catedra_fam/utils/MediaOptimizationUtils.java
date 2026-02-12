package com.example.catedra_fam.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utilidades para optimización de archivos multimedia
 * Mejora la velocidad de upload y reduce el consumo de datos según recomendaciones UX
 */
public class MediaOptimizationUtils {

    private static final String TAG = "MediaOptimization";

    // Configuraciones de compresión para mejor UX
    private static final int MAX_IMAGE_WIDTH = 1920;
    private static final int MAX_IMAGE_HEIGHT = 1080;
    private static final int JPEG_QUALITY = 85; // Balance entre calidad y tamaño
    private static final long MAX_COMPRESSED_SIZE = 2 * 1024 * 1024; // 2MB máximo después de compresión

    /**
     * Resultado de optimización
     */
    public static class OptimizationResult {
        private boolean success;
        private File optimizedFile;
        private long originalSize;
        private long optimizedSize;
        private String errorMessage;

        public OptimizationResult(boolean success, File optimizedFile, long originalSize, long optimizedSize, String errorMessage) {
            this.success = success;
            this.optimizedFile = optimizedFile;
            this.originalSize = originalSize;
            this.optimizedSize = optimizedSize;
            this.errorMessage = errorMessage;
        }

        // Getters
        public boolean isSuccess() { return success; }
        public File getOptimizedFile() { return optimizedFile; }
        public long getOriginalSize() { return originalSize; }
        public long getOptimizedSize() { return optimizedSize; }
        public String getErrorMessage() { return errorMessage; }

        public double getCompressionRatio() {
            if (originalSize == 0) return 0;
            return (1.0 - ((double) optimizedSize / originalSize)) * 100;
        }

        public String getSizeSummary() {
            return FileValidationUtils.getFileSizeString(originalSize) + " → " +
                   FileValidationUtils.getFileSizeString(optimizedSize) +
                   " (" + String.format("%.1f", getCompressionRatio()) + "% reducción)";
        }
    }

    /**
     * Optimiza una imagen para reducir tamaño y mejorar velocidad de upload
     */
    public static OptimizationResult optimizeImage(Context context, File inputFile) {
        if (!FileValidationUtils.isImageFile(inputFile.getName())) {
            return new OptimizationResult(false, null, inputFile.length(), 0,
                "El archivo no es una imagen");
        }

        long originalSize = inputFile.length();

        try {
            // Crear archivo temporal para la versión optimizada
            File outputDir = new File(context.getCacheDir(), "optimized_images");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            String outputFileName = "optimized_" + System.currentTimeMillis() + "_" + inputFile.getName();
            File outputFile = new File(outputDir, outputFileName);

            // Decodificar imagen con sampling para reducir memoria
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(inputFile.getAbsolutePath(), options);

            // Calcular factor de escala
            int scaleFactor = calculateScaleFactor(options.outWidth, options.outHeight);
            options.inJustDecodeBounds = false;
            options.inSampleSize = scaleFactor;
            options.inPreferredConfig = Bitmap.Config.RGB_565; // Menos memoria

            // Decodificar imagen escalada
            Bitmap originalBitmap = BitmapFactory.decodeFile(inputFile.getAbsolutePath(), options);
            if (originalBitmap == null) {
                return new OptimizationResult(false, null, originalSize, 0,
                    "No se pudo decodificar la imagen");
            }

            // Corregir orientación si es necesario
            Bitmap rotatedBitmap = fixImageOrientation(inputFile.getAbsolutePath(), originalBitmap);
            if (rotatedBitmap != originalBitmap) {
                originalBitmap.recycle();
                originalBitmap = rotatedBitmap;
            }

            // Redimensionar si es necesario
            Bitmap resizedBitmap = resizeBitmap(originalBitmap, MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT);
            if (resizedBitmap != originalBitmap) {
                originalBitmap.recycle();
                originalBitmap = resizedBitmap;
            }

            // Guardar imagen comprimida
            FileOutputStream fos = new FileOutputStream(outputFile);
            boolean compressed = originalBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, fos);
            fos.close();
            originalBitmap.recycle();

            if (!compressed) {
                return new OptimizationResult(false, null, originalSize, 0,
                    "Error al comprimir la imagen");
            }

            long optimizedSize = outputFile.length();

            // Verificar que el archivo optimizado no sea más grande que el original
            if (optimizedSize >= originalSize) {
                outputFile.delete();
                return new OptimizationResult(true, inputFile, originalSize, originalSize,
                    "La imagen ya está optimizada");
            }

            Log.d(TAG, "Imagen optimizada: " + FileValidationUtils.getFileSizeString(originalSize) +
                      " → " + FileValidationUtils.getFileSizeString(optimizedSize));

            return new OptimizationResult(true, outputFile, originalSize, optimizedSize, null);

        } catch (Exception e) {
            Log.e(TAG, "Error optimizando imagen", e);
            return new OptimizationResult(false, null, originalSize, 0,
                "Error interno: " + e.getMessage());
        }
    }

    /**
     * Calcula factor de escala para reducir resolución sin perder demasiada calidad
     */
    private static int calculateScaleFactor(int width, int height) {
        int scaleFactor = 1;

        // Calcular factor basado en la dimensión más grande
        int maxDimension = Math.max(width, height);
        int targetDimension = Math.max(MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT);

        while (maxDimension / scaleFactor > targetDimension) {
            scaleFactor *= 2;
        }

        return scaleFactor;
    }

    /**
     * Corrige la orientación de una imagen basándose en EXIF data
     */
    private static Bitmap fixImageOrientation(String imagePath, Bitmap bitmap) {
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                default:
                    return bitmap; // No rotation needed
            }

            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        } catch (IOException e) {
            Log.w(TAG, "No se pudo leer EXIF data", e);
            return bitmap;
        }
    }

    /**
     * Redimensiona un bitmap manteniendo la relación de aspecto
     */
    private static Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width <= maxWidth && height <= maxHeight) {
            return bitmap; // No need to resize
        }

        float ratio = Math.min((float) maxWidth / width, (float) maxHeight / height);
        int newWidth = Math.round(width * ratio);
        int newHeight = Math.round(height * ratio);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    /**
     * Verifica si un archivo necesita optimización
     */
    public static boolean needsOptimization(File file) {
        if (!FileValidationUtils.isImageFile(file.getName())) {
            return false; // Solo optimizamos imágenes
        }

        long fileSize = file.length();

        // Optimizar si es mayor a 1MB
        return fileSize > 1024 * 1024;
    }

    /**
     * Obtiene información sobre cuánto se puede comprimir un archivo
     */
    public static String getOptimizationEstimate(File file) {
        if (!needsOptimization(file)) {
            return "Archivo ya optimizado";
        }

        long size = file.length();
        if (FileValidationUtils.isImageFile(file.getName())) {
            // Estimación aproximada para imágenes
            long estimatedSize = size / 3; // Aproximadamente 70% de reducción
            double reduction = (1.0 - ((double) estimatedSize / size)) * 100;
            return String.format("Se puede reducir ~%.0f%% (aprox. %s)",
                reduction, FileValidationUtils.getFileSizeString(estimatedSize));
        }

        return "Optimización no disponible para este tipo de archivo";
    }

    /**
     * Limpia archivos temporales de optimización
     */
    public static void cleanupOptimizedFiles(Context context) {
        File outputDir = new File(context.getCacheDir(), "optimized_images");
        if (outputDir.exists() && outputDir.isDirectory()) {
            File[] files = outputDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        }
    }

    /**
     * Crear thumbnail de una imagen para preview rápido
     */
    public static Bitmap createThumbnail(String imagePath, int thumbnailSize) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, options);

            int scaleFactor = Math.max(options.outWidth, options.outHeight) / thumbnailSize;
            if (scaleFactor < 1) scaleFactor = 1;

            options.inJustDecodeBounds = false;
            options.inSampleSize = scaleFactor;

            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
            if (bitmap != null) {
                return resizeBitmap(bitmap, thumbnailSize, thumbnailSize);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creando thumbnail", e);
        }
        return null;
    }
}
