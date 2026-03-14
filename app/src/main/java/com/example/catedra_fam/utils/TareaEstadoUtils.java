package com.example.catedra_fam.utils;

import com.example.catedra_fam.R;

/**
 * Utilidades para manejar estados de tareas con UX optimizada
 * Basado en las recomendaciones de colores y mensajes amigables
 */
public class TareaEstadoUtils {

    /**
     * Información de estado con colores y mensajes para la UI
     */
    public static class EstadoInfo {
        private final String emoji;
        private final String mensaje;
        private final int colorResource;
        private final int backgroundColorResource;
        private final String descripcion;

        public EstadoInfo(String emoji, String mensaje, int colorResource,
                         int backgroundColorResource, String descripcion) {
            this.emoji = emoji;
            this.mensaje = mensaje;
            this.colorResource = colorResource;
            this.backgroundColorResource = backgroundColorResource;
            this.descripcion = descripcion;
        }

        public String getEmoji() { return emoji; }
        public String getMensaje() { return mensaje; }
        public int getColorResource() { return colorResource; }
        public int getBackgroundColorResource() { return backgroundColorResource; }
        public String getDescripcion() { return descripcion; }

        public String getMensajeCompleto() { return emoji + " " + mensaje; }
    }

    /**
     * Obtiene información de estado optimizada para UX según las recomendaciones
     */
    public static EstadoInfo getEstadoInfo(String estado, int diasRestantes) {
        if (estado == null) estado = "pendiente";

        switch (estado.toLowerCase()) {
            case "pendiente":
                if (diasRestantes <= 1) {
                    // Próximo a vencer - Amarillo para generar urgencia apropiada
                    return new EstadoInfo("🟡", "PRÓXIMO A VENCER",
                        R.color.warning_text, R.color.warning_background,
                        diasRestantes == 0 ? "Vence hoy" : "Vence mañana");
                } else {
                    // Pendiente normal - Verde para transmitir calma
                    return new EstadoInfo("🟢", "PENDIENTE",
                        R.color.success_text, R.color.success_background,
                        "Vence en " + diasRestantes + " días");
                }

            case "próximo_vencimiento":
                // Amarillo para urgencia apropiada
                return new EstadoInfo("🟡", "PRÓXIMO A VENCER",
                    R.color.warning_text, R.color.warning_background,
                    diasRestantes <= 0 ? "Vence hoy" : "Vence en " + diasRestantes + " días");

            case "vencida":
                // Rojo sin generar ansiedad excesiva
                return new EstadoInfo("🔴", "VENCIDA",
                    R.color.error_text, R.color.error_background,
                    diasRestantes == -1 ? "Venció ayer" : "Venció hace " + Math.abs(diasRestantes) + " días");

            case "entregada":
                // Verde claro para reforzar sensación de logro
                return new EstadoInfo("✅", "ENTREGADA",
                    R.color.success_text, R.color.success_light_background,
                    "Esperando calificación");

            case "calificada":
                // Dorado para celebrar el éxito
                return new EstadoInfo("🏆", "CALIFICADA",
                    R.color.gold_text, R.color.gold_background,
                    "Tarea evaluada");

            case "completada":
                // Verde para completadas
                return new EstadoInfo("✅", "COMPLETADA",
                    R.color.success_text, R.color.success_background,
                    "Tarea terminada");

            default:
                // Estado desconocido - Gris neutro
                return new EstadoInfo("⚪", "SIN DEFINIR",
                    R.color.neutral_text, R.color.neutral_background,
                    "Estado no definido");
        }
    }

    /**
     * Obtiene mensaje de tiempo restante optimizado para UX
     */
    public static String getMensajeTiempoRestante(int diasRestantes, String fechaVencimiento) {
        if (diasRestantes > 1) {
            return "📅 Vence en " + diasRestantes + " días";
        } else if (diasRestantes == 1) {
            return "⚠️ Vence mañana";
        } else if (diasRestantes == 0) {
            return "🚨 Vence hoy";
        } else if (diasRestantes == -1) {
            return "📉 Venció ayer";
        } else {
            return "📉 Venció hace " + Math.abs(diasRestantes) + " días";
        }
    }

    /**
     * Obtiene texto del botón principal según el estado (para UX optimizada)
     */
    public static String getTextoBotonPrincipal(String estado, boolean tieneEntrega) {
        if (estado == null) estado = "pendiente";

        switch (estado.toLowerCase()) {
            case "pendiente":
            case "próximo_vencimiento":
                return tieneEntrega ? "EDITAR ENTREGA" : "ENTREGAR AHORA";

            case "vencida":
                return tieneEntrega ? "VER ENTREGA" : "ENTREGAR TARDE";

            case "entregada":
                return "VER ENTREGA";

            case "calificada":
                return "VER CALIFICACIÓN";

            default:
                return "VER DETALLE";
        }
    }

    /**
     * Obtiene texto del botón secundario
     */
    public static String getTextoBotonSecundario(String estado) {
        return "VER DETALLE";
    }

    /**
     * Verifica si se puede entregar la tarea según las reglas de negocio
     */
    public static boolean puedeEntregar(String estado, boolean tieneEntrega, boolean estaCalificada) {
        if (estaCalificada) return false; // No se puede editar si ya está calificada

        switch (estado.toLowerCase()) {
            case "pendiente":
            case "próximo_vencimiento":
            case "vencida":
                return true;

            case "entregada":
                return !estaCalificada; // Solo si no está calificada

            default:
                return false;
        }
    }

    /**
     * Obtiene prioridad visual para ordenamiento (mayor número = más urgente)
     */
    public static int getPrioridadVisual(String estado, int diasRestantes) {
        switch (estado.toLowerCase()) {
            case "próximo_vencimiento":
                return 10;
            case "pendiente":
                if (diasRestantes <= 1) return 9;
                if (diasRestantes <= 3) return 7;
                return 5;
            case "vencida":
                return Math.abs(diasRestantes) + 6; // Más vencida = más urgente
            case "entregada":
                return 3;
            case "calificada":
                return 1;
            default:
                return 0;
        }
    }

    /**
     * Obtiene mensaje motivacional según el progreso del estudiante
     */
    public static String getMensajeMotivacional(int tareasCompletadas, int totalTareas, double promedio) {
        double porcentajeCompletado = totalTareas > 0 ? (tareasCompletadas * 100.0 / totalTareas) : 0;

        if (porcentajeCompletado >= 90) {
            return "🌟 ¡Excelente trabajo! Casi todas las tareas completadas";
        } else if (porcentajeCompletado >= 70) {
            return "👍 ¡Muy buen progreso! Sigues por buen camino";
        } else if (porcentajeCompletado >= 50) {
            return "💪 ¡Sigue así! Ya llevas más de la mitad";
        } else if (porcentajeCompletado >= 25) {
            return "📚 ¡Buen comienzo! Continúa con las tareas";
        } else if (totalTareas > 0) {
            return "🎯 ¡Empecemos! Tienes " + totalTareas + " tareas por completar";
        } else {
            return "📖 No hay tareas asignadas por el momento";
        }
    }

    /**
     * Obtiene color de progreso para barras de progreso
     */
    public static int getColorProgreso(double porcentaje) {
        if (porcentaje >= 80) return R.color.success;
        if (porcentaje >= 60) return R.color.warning;
        if (porcentaje >= 40) return R.color.info;
        return R.color.error_text;
    }

    /**
     * Formatea el promedio de calificaciones de manera amigable
     */
    public static String formatearPromedio(double promedio, String escala) {
        if (promedio <= 0) {
            return "Sin calificaciones";
        }
        String promedioStr = String.format(java.util.Locale.US, "%.1f", promedio);
        if (escala != null && !escala.isEmpty() && !"Sin datos".equals(escala)) {
            return promedioStr + " - " + escala;
        } else {
            return promedioStr;
        }
    }

    /**
     * Obtiene emoji para el promedio
     */
    public static String getEmojiPromedio(double promedio) {
        if (promedio >= 4.5) return "🏆";
        if (promedio >= 4.0) return "🌟";
        if (promedio >= 3.5) return "👍";
        if (promedio >= 3.0) return "📚";
        if (promedio > 0) return "💪";
        return "📖";
    }

    /**
     * Genera resumen visual del estado general de las tareas
     */
    public static String getResumenEstado(int pendientes, int completadas, int vencidas, int calificadas) {
        StringBuilder resumen = new StringBuilder();

        if (completadas > 0) {
            resumen.append("✅ ").append(completadas).append(" Completadas  ");
        }
        if (pendientes > 0) {
            resumen.append("⏰ ").append(pendientes).append(" Pendientes  ");
        }
        if (vencidas > 0) {
            resumen.append("🔴 ").append(vencidas).append(" Vencidas  ");
        }
        if (calificadas > 0) {
            resumen.append("🏆 ").append(calificadas).append(" Calificadas");
        }

        return resumen.toString().trim();
    }
}
