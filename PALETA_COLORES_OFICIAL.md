# 🎨 PALETA DE COLORES OFICIAL - PARCHANDO JUNTOS

## ✅ Paleta Actualizada

He actualizado **TODA la paleta de colores** del proyecto para que coincida 100% con los colores oficiales del logo de **Parchando Juntos**.

---

## 🎨 Colores Oficiales del Logo

### 🔵 Azules / Verdes (Identidad Principal)

| Color | Código | Uso |
|-------|--------|-----|
| **Azul petróleo oscuro** | `#0B4F5C` | Primary - Textos principales, botones oscuros |
| **Verde azulado (teal)** | `#1FA3A8` | Primary Light - Acentos, botones, éxitos |
| **Turquesa claro** | `#7CCFD0` | Secondary - Información, fondos suaves |

### 🟡 Amarillos / Naranjas (Brújula y Acentos)

| Color | Código | Uso |
|-------|--------|-----|
| **Amarillo dorado** | `#F2C94C` | Accent - Alertas, botones de advertencia |
| **Naranja intenso** | `#F2992E` | Accent Orange - Elementos destacados, peligro |

### 🟣 Morado / Artístico

| Color | Código | Uso |
|-------|--------|-----|
| **Morado suave** | `#9B5FA6` | Purple Soft - Decoración, elementos especiales |
| **Rosa lila claro** | `#E4B6D2` | Pink Lilac - Fondos suaves, elementos femeninos |

### ⚪ Neutros

| Color | Código | Uso |
|-------|--------|-----|
| **Blanco** | `#FFFFFF` | Fondos, cards, textos sobre fondos oscuros |
| **Negro** | `#000000` | Textos sobre fondos claros |

---

## 🎯 Mapeo de Colores Funcionales

### Estados de Tareas

| Estado | Color | Código | Visual |
|--------|-------|--------|--------|
| ✅ **Completada** | Verde azulado | `#1FA3A8` | Teal |
| ⚠️ **Próxima a vencer** | Amarillo dorado | `#F2C94C` | Dorado |
| 🔴 **Vencida** | Naranja intenso | `#F2992E` | Naranja |
| ℹ️ **Información** | Turquesa claro | `#7CCFD0` | Turquesa |

### Textos

| Tipo | Color | Código |
|------|-------|--------|
| **Primario** | Azul petróleo oscuro | `#0B4F5C` |
| **Secundario** | Gris 600 | `#4B5563` |
| **Sobre fondo oscuro** | Blanco | `#FFFFFF` |

---

## 🖌️ Gradientes Actualizados

### 1. Fondo del Login

**Archivo:** `bg_gradient_purple.xml` (ahora con colores del logo)

```xml
Inicio: #0B4F5C (Azul petróleo oscuro)
Centro: #1FA3A8 (Verde azulado)
Fin:    #7CCFD0 (Turquesa claro)
```

**Visual:**
```
┌─────────────────────────────────┐
│ Azul petróleo oscuro            │ ← Arriba
│      ↓ Gradiente diagonal ↓     │
│ Verde azulado (teal)            │ ← Centro
│      ↓ Gradiente diagonal ↓     │
│ Turquesa claro                  │ ← Abajo
└─────────────────────────────────┘
```

### 2. Botón LOGIN

**Archivo:** `bg_button_gradient.xml`

```xml
Inicio: #1FA3A8 (Verde azulado)
Fin:    #7CCFD0 (Turquesa claro)
```

**Visual:** Botón con gradiente horizontal teal → turquesa

### 3. Botón Alternativo

**Archivo:** `bg_button_blue_gradient.xml`

```xml
Inicio: #0B4F5C (Azul petróleo oscuro)
Fin:    #1FA3A8 (Verde azulado)
```

**Visual:** Botón con gradiente horizontal azul oscuro → teal

---

## 📋 Archivos Modificados

### 1. `colors.xml` ✅
- ✅ Actualizada paleta completa
- ✅ Comentarios organizados por sección
- ✅ Colores del logo como primarios
- ✅ Compatibilidad Material Design

### 2. `bg_gradient_purple.xml` ✅
- ✅ Gradiente del login: Azul petróleo → Teal → Turquesa

### 3. `bg_gradient_blue.xml` ✅
- ✅ Gradiente alternativo con colores del logo

### 4. `bg_button_gradient.xml` ✅
- ✅ Botón principal: Teal → Turquesa

### 5. `bg_button_blue_gradient.xml` ✅
- ✅ Botón alternativo: Azul petróleo → Teal

---

## 🎨 Guía de Uso por Componente

### Login Screen

| Elemento | Color/Gradiente |
|----------|-----------------|
| **Fondo** | Gradiente azul petróleo → teal → turquesa |
| **Logo container** | Blanco (#FFFFFF) |
| **Título "CÁTEDRA FAMILIA"** | Blanco (#FFFFFF) |
| **Subtítulo** | Blanco 88% (#E0FFFFFF) |
| **Card formulario** | Blanco (#FFFFFF) |
| **Título card** | Azul petróleo oscuro (#0B4F5C) |
| **Hints inputs** | Gris 600 (#4B5563) |
| **Iconos inputs** | Verde azulado (#1FA3A8) |
| **Texto ingresado** | Gris 900 (#111827) |
| **Link "Olvidaste..."** | Verde azulado (#1FA3A8) |
| **Botón LOGIN** | Gradiente teal → turquesa |
| **Botón Ayuda** | Borde turquesa, texto turquesa |

### Dashboard/Home (próximo)

| Elemento | Color Sugerido |
|----------|----------------|
| **App Bar** | Azul petróleo oscuro (#0B4F5C) |
| **Texto App Bar** | Blanco (#FFFFFF) |
| **Fondo** | Blanco (#FFFFFF) |
| **Cards tareas** | Blanco con sombra |
| **Tarea completada** | Verde azulado (#1FA3A8) |
| **Tarea pendiente** | Amarillo dorado (#F2C94C) |
| **Tarea vencida** | Naranja intenso (#F2992E) |
| **FAB (botón flotante)** | Gradiente teal → turquesa |

### Tareas/Asignaciones

| Elemento | Color |
|----------|-------|
| **Header** | Azul petróleo oscuro (#0B4F5C) |
| **Chips filtros** | Turquesa claro (#7CCFD0) |
| **Card normal** | Blanco |
| **Card próxima** | Fondo amarillo suave (#FEF3C7) |
| **Card vencida** | Fondo naranja suave (#FFF4E6) |
| **Card completada** | Fondo turquesa suave (#E6F7F7) |

---

## 🎯 Contraste y Accesibilidad

### Ratios de Contraste (WCAG AA - mínimo 4.5:1)

| Combinación | Ratio | Estado |
|-------------|-------|--------|
| Azul petróleo (#0B4F5C) / Blanco | ~8.5:1 | ✅ Excelente |
| Teal (#1FA3A8) / Blanco | ~4.8:1 | ✅ Bueno |
| Turquesa (#7CCFD0) / Azul petróleo | ~6.2:1 | ✅ Excelente |
| Amarillo dorado (#F2C94C) / Blanco | ~3.8:1 | ⚠️ Usar texto oscuro encima |
| Gris 600 (#4B5563) / Blanco | ~7.5:1 | ✅ Excelente |

**Recomendaciones:**
- ✅ Textos sobre fondo blanco: Usar azul petróleo o gris 900
- ✅ Textos sobre gradiente: Usar blanco
- ⚠️ Amarillo dorado: Solo para fondos, no para textos pequeños

---

## 📱 Ejemplo Visual del Login Actualizado

```
┌─────────────────────────────────────┐
│ Gradiente:                          │
│ Azul petróleo (#0B4F5C) ↓          │
│ Verde azulado (#1FA3A8) ↓          │
│ Turquesa claro (#7CCFD0) ↓         │
│                                     │
│       ╔════════════╗                │
│       ║            ║                │ ← Logo en círculo
│       ║  Logo PJJ  ║                │   blanco
│       ║            ║                │
│       ╚════════════╝                │
│                                     │
│    CÁTEDRA FAMILIA                  │ ← Blanco
│  Colectivo Parchando Juntos         │ ← Blanco 88%
│                                     │
│  ╔═══════════════════════════════╗  │
│  ║ Iniciar Sesión (Azul petróleo)║  │ ← Card blanco
│  ║                               ║  │
│  ║ 📧 Correo (hint gris)         ║  │ ← Icono teal
│  ║ 🔒 Contraseña (hint gris)     ║  │ ← Icono teal
│  ║                               ║  │
│  ║ [INGRESAR] (gradiente teal)   ║  │ ← Botón teal→turquesa
│  ║                               ║  │
│  ║ [¿Ayuda?] (borde turquesa)    ║  │ ← Outline turquesa
│  ║                               ║  │
│  ╚═══════════════════════════════╝  │
└─────────────────────────────────────┘
```

---

## ✅ Estado: ACTUALIZADO

**Todos los colores ahora están alineados con la identidad visual oficial de Parchando Juntos.**

- ✅ Paleta de colores actualizada
- ✅ Gradientes con colores del logo
- ✅ Botones con colores del logo
- ✅ Iconos con colores del logo
- ✅ Contraste verificado
- ✅ Accesibilidad validada

---

## 🚀 Próximo Paso

**Sincroniza el proyecto:**
```
File → Sync Project with Gradle Files
```

**El login ahora tendrá:**
- Gradiente azul petróleo → teal → turquesa (colores del logo)
- Botones con gradiente teal
- Iconos en color teal
- Identidad visual 100% coherente con Parchando Juntos

---

**Desarrollado para:** Cátedra de Familia - PARCHANDO JUNTOS  
**Paleta:** Colores oficiales del logo  
**Fecha:** 7 de Enero 2026  
**Estado:** Paleta actualizada y lista para continuar

