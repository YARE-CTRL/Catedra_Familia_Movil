# Cátedra Familia - Aplicación Móvil Android

## 📱 Descripción del Proyecto

**Cátedra Familia** es una aplicación móvil Android desarrollada en **Java** que permite a los acudientes gestionar las tareas académicas de sus hijos, recibir notificaciones push en tiempo real y mantener una comunicación efectiva con la institución educativa.

---

## 🎯 Funcionalidades Principales

### 1. Autenticación y Seguridad
- Login con documento de identidad y contraseña
- Cambio obligatorio de contraseña en primer ingreso
- Recuperación de contraseña mediante código OTP
- Autenticación JWT con tokens seguros
- Sesión persistente

### 2. Gestión de Tareas
- Visualización de tareas asignadas por estudiante
- Filtros avanzados:
  - Por estado (todas, pendientes, completadas, calificadas, vencidas)
  - Por fecha (hoy, últimas 24h, últimas 7 días, este mes, todos)
- Estados de tareas:
  - Pendiente
  - Entregada
  - Calificada
  - Vencida
- Detalle completo de cada tarea con:
  - Descripción e instrucciones
  - Fecha de vencimiento
  - Archivos adjuntos
  - Estado de entrega
  - Calificación y retroalimentación (si aplica)

### 3. Envío de Evidencias
- Subir evidencias de tareas completadas
- Soporte para:
  - Imágenes (JPEG, PNG)
  - Videos (MP4, MOV)
  - Documentos (PDF)
- Optimización automática de archivos
- Validación de tamaños y formatos
- Vista previa antes de enviar

### 4. Notificaciones Push
- Notificaciones en tiempo real con Firebase Cloud Messaging (FCM)
- Tipos de notificaciones:
  - Nueva tarea asignada
  - Tarea calificada
  - Recordatorios de vencimiento
  - Eventos y anuncios
- Filtros de notificaciones por periodo
- Marcar como leídas
- Eliminar notificaciones (individual o todas)
- Badge con contador de notificaciones no leídas

### 5. Historial Académico
- Historial completo de tareas por periodo académico
- Estadísticas de desempeño:
  - Tareas completadas vs totales
  - Promedio de calificaciones
  - Porcentaje de cumplimiento
  - Entregas a tiempo vs tardías

### 6. Soporte y Ayuda
- Información de contacto institucional
- Preguntas frecuentes
- Formulario de solicitud de ayuda

---

## 🏗️ Arquitectura Técnica

### Stack Tecnológico

#### Frontend Móvil
- **Lenguaje**: Java 17
- **SDK**: Android API 24-34 (Android 7.0 a Android 14)
- **Patrón**: MVVM (Model-View-ViewModel)
- **UI**: Material Design 3

#### Librerías Principales
```gradle
// Networking
- Retrofit 2.9.0 (Cliente HTTP)
- OkHttp 4.11.0 (Logging e interceptores)
- Gson 2.10.1 (Serialización JSON)

// Firebase
- Firebase Cloud Messaging 23.3.1 (Notificaciones Push)
- Firebase BOM 32.7.0 (Bill of Materials)

// Base de Datos Local
- Room 2.6.1 (Caché y persistencia)
- WorkManager 2.9.0 (Sincronización en background)

// UI/UX
- Material Components 1.11.0
- Glide 4.16.0 (Carga de imágenes)
- Lottie 6.1.0 (Animaciones)
- Shimmer 0.5.0 (Efectos de carga)
- CircleImageView 3.1.0

// Utilidades
- ExifInterface 1.3.6 (Metadatos de imágenes)
```

### Estructura del Proyecto

```
app/src/main/java/com/example/catedra_fam/
├── activities/
│   ├── LoginActivity.java
│   ├── MainActivity.java
│   ├── TareasActivity.java
│   ├── TareaDetalleActivity.java
│   ├── NotificacionesActivity.java
│   ├── HistorialActivity.java
│   └── ... (11 activities en total)
│
├── api/
│   ├── ApiService.java (Endpoints REST)
│   ├── RetrofitClient.java (Configuración HTTP)
│   └── AuthInterceptor.java (JWT automático)
│
├── models/
│   ├── LoginResponse.java
│   ├── Tarea.java
│   ├── Notificacion.java
│   ├── Estudiante.java
│   └── ... (28 modelos en total)
│
├── adapters/
│   ├── TareasAdapter.java
│   ├── NotificacionesAdapter.java
│   ├── HijosAdapter.java
│   └── HistorialAdapter.java
│
├── services/
│   └── FCMNotificationService.java (Push notifications)
│
├── database/
│   ├── AppDatabase.java (Room DB)
│   ├── daos/ (Data Access Objects)
│   └── entities/ (Entidades locales)
│
├── utils/
│   ├── TareaEstadoUtils.java
│   ├── FileValidationUtils.java
│   ├── MediaOptimizationUtils.java
│   └── ... (helpers varios)
│
└── workers/
    └── NotificationSyncWorker.java (Sincronización)
```

---

## 🔌 Integración con Backend

### URL del Backend
- **Producción**: `https://escuelaparapadres-backend-1.onrender.com/api/`
- **Desarrollo**: Configurable en `RetrofitClient.java`

### Endpoints Principales

#### Autenticación
```
POST /movil/auth/login/movil
POST /auth/cambiar-password
POST /auth/recuperar/solicitar
POST /auth/recuperar/verificar
POST /auth/recuperar/restablecer
```

#### Estudiantes
```
GET /movil/acudientes/mis-estudiantes
GET /movil/estudiantes/:id/perfil
```

#### Tareas
```
GET /movil/estudiantes/:id/tareas
GET /movil/asignaciones/:id/detalle
POST /movil/asignaciones/:id/entregas
```

#### Notificaciones
```
POST /movil/notificaciones/token
GET /movil/notificaciones
PUT /movil/notificaciones/:id/leer
PUT /movil/notificaciones/leer-todas
DELETE /movil/notificaciones/:id
DELETE /movil/notificaciones
GET /movil/notificaciones/resumen
```

#### Historial y Estadísticas
```
GET /movil/estudiantes/:id/historial
GET /movil/estudiantes/:id/estadisticas
```

---

## 🔐 Seguridad

### Autenticación
- **JWT (JSON Web Tokens)** con expiración de 7 días
- Token almacenado en `SharedPreferences` cifrado
- Renovación automática de tokens
- Interceptor automático para agregar Authorization header

### Validaciones
- Validación de formatos de archivo
- Tamaño máximo de archivos: 10MB
- Sanitización de inputs
- Manejo seguro de credenciales

### Permisos Android
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

---

## 🚀 Instalación y Configuración

### Requisitos Previos
- **Android Studio**: Hedgehog (2023.1.1) o superior
- **JDK**: 17 o superior
- **Gradle**: 8.5
- **SDK Android**: API 24-34

### Pasos de Instalación

1. **Clonar el repositorio**
```bash
git clone https://github.com/tu-usuario/Catedra_Fam.git
cd Catedra_Fam
```

2. **Abrir en Android Studio**
- File → Open → Seleccionar carpeta del proyecto
- Esperar sincronización automática de Gradle

3. **Configurar Firebase**
- Asegurarse de que existe `app/google-services.json`
- Verificar configuración FCM en Firebase Console

4. **Configurar URL del Backend** (opcional)
- Editar `app/src/main/java/com/example/catedra_fam/api/RetrofitClient.java`
- Cambiar `BASE_URL` si es necesario

5. **Compilar y ejecutar**
```bash
./gradlew assembleDebug
```
O usar el botón Run en Android Studio

### Script de Compilación Rápida
```batch
# Windows: ejecutar compilar.bat
compilar.bat

# El APK se generará en:
# app/build/outputs/apk/debug/app-debug.apk
```

---

## 📊 Flujos de Usuario

### Flujo de Login
1. Usuario ingresa documento y contraseña
2. App valida credenciales con backend
3. Backend retorna JWT + datos de usuario + estudiantes vinculados
4. App guarda token y datos localmente
5. Redirige a pantalla principal

### Flujo de Visualización de Tareas
1. Seleccionar estudiante
2. Cargar tareas desde API
3. Aplicar filtros (estado, fecha)
4. Mostrar lista con estados visuales
5. Tap en tarea → Ver detalle completo

### Flujo de Envío de Tarea
1. Abrir detalle de tarea pendiente
2. Seleccionar archivo (cámara o galería)
3. Vista previa y validación
4. Optimización automática
5. Upload con progress bar
6. Confirmación de entrega exitosa

### Flujo de Notificaciones Push
1. Backend genera evento (nueva tarea, calificación, etc.)
2. Firebase FCM envía push notification
3. App recibe notificación en segundo plano
4. Actualiza badge con contador
5. Usuario tap → Redirige a pantalla correspondiente

---

## 🎨 Diseño UI/UX

### Paleta de Colores
```xml
<color name="primary">#6200EE</color>
<color name="primary_dark">#3700B3</color>
<color name="accent">#03DAC5</color>
<color name="background">#F5F5F5</color>
<color name="surface">#FFFFFF</color>
<color name="error">#B00020</color>
<color name="success">#4CAF50</color>
<color name="warning">#FF9800</color>
```

### Componentes Principales
- **Material Cards** con elevación y esquinas redondeadas
- **Chips** para filtros y categorías
- **Bottom Navigation** para navegación principal
- **Floating Action Button** para acciones rápidas
- **Snackbar** para mensajes temporales
- **ProgressBar** para indicadores de carga
- **Shimmer Effect** para skeleton screens

### Animaciones
- Transiciones entre pantallas
- Animaciones Lottie para estados vacíos
- Ripple effect en botones
- Fade in/out para carga de imágenes

---

## 🧪 Testing

### Pruebas Unitarias
```bash
./gradlew test
```

### Pruebas de Integración
```bash
./gradlew connectedAndroidTest
```

### Testing Manual
1. Login con credenciales de prueba:
   - Documento: `1234567890`
   - Contraseña: `1234567890`

2. Verificar flujos principales:
   - ✅ Login y autenticación
   - ✅ Carga de estudiantes
   - ✅ Visualización de tareas
   - ✅ Aplicación de filtros
   - ✅ Envío de evidencias
   - ✅ Recepción de notificaciones
   - ✅ Marcar notificaciones como leídas
   - ✅ Ver historial académico

---

## 📦 Generación de APK

### Debug APK
```bash
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Release APK (firmado)
```bash
./gradlew assembleRelease
# Requiere configurar keystore en gradle.properties
```

---

## 🐛 Troubleshooting

### Error: "Cannot resolve symbol 'R'"
- File → Invalidate Caches → Restart
- Build → Clean Project → Rebuild Project

### Error: "Gradle sync failed"
- Verificar conexión a Internet
- File → Sync Project with Gradle Files

### Notificaciones no llegan
- Verificar que `google-services.json` esté presente
- Verificar permisos de notificaciones en Android 13+
- Revisar token FCM en logs

### Error de compilación
```bash
# Limpiar y reconstruir
./gradlew clean
./gradlew build
```

---

## 📝 Changelog

### v1.0.0 (Marzo 2026)
- ✅ Implementación completa de autenticación
- ✅ Gestión de tareas con filtros avanzados
- ✅ Sistema de notificaciones push FCM
- ✅ Envío de evidencias multimedia
- ✅ Historial y estadísticas académicas
- ✅ UI/UX optimizada con Material Design 3
- ✅ Sincronización en background
- ✅ Caché local con Room Database

---

## 👥 Equipo de Desarrollo

**Proyecto desarrollado como parte del programa SENA - Fábrica de Software**

---

## 📄 Licencia

Este proyecto es de uso institucional educativo.

---

## 🔗 Enlaces Importantes

- **Backend Repository**: [Backend AdonisJS](https://github.com/tu-usuario/backend-repo)
- **Backend Production**: https://escuelaparapadres-backend-1.onrender.com
- **Documentación Técnica**: Ver carpeta `/docs` (si aplica)

---

## 📞 Soporte

Para reportar problemas o solicitar ayuda:
- **Email**: soporte@institucion.edu.co
- **Teléfono**: +57 300 123 4567

---

**Última actualización**: Marzo 13, 2026

