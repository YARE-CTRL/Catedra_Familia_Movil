# 📱 PLAN COMPLETO - VISTAS PENDIENTES (7/10)

## ✅ ESTADO ACTUAL: 3/10 VISTAS IMPLEMENTADAS (30%)

### Vistas Completadas
| # | Vista | Archivos | Estado |
|---|-------|----------|--------|
| 1 | LoginActivity | 2 archivos | ✅ COMPLETO |
| 2 | MainActivity | 2 archivos | ✅ COMPLETO |
| 3 | OnboardingActivity | 9 archivos | ✅ COMPLETO |

---

## 🚧 VISTAS PENDIENTES: 7 VISTAS

### 🔴 PRIORIDAD ALTA (4 vistas)

#### 1. CambiarContrasenaActivity
**Archivos a crear:** 3
- `CambiarContrasenaActivity.java`
- `activity_cambiar_contrasena.xml`
- Validaciones regex

**Funcionalidad:**
- Cambio obligatorio en primer ingreso
- Validación de contraseña segura
- Requisitos visuales en tiempo real
- Integración con LoginActivity

**Estimación:** 4-6 horas

---

#### 2. RecuperarContrasenaActivity (3 pantallas)
**Archivos a crear:** 7
- `RecuperarContrasenaActivity.java`
- `VerificarCodigoActivity.java`
- `NuevaContrasenaActivity.java`
- `activity_recuperar_contrasena.xml`
- `activity_verificar_codigo.xml`
- `activity_nueva_contrasena.xml`
- `OtpInputView.java` (componente custom)

**Funcionalidad:**
- Solicitar código por email/SMS
- Input OTP de 6 dígitos
- Countdown de expiración (15 min)
- Reenvío de código (1 min cooldown)
- Nueva contraseña segura

**Estimación:** 8-10 horas

---

#### 3. TareasActivity
**Archivos a crear:** 5
- `TareasActivity.java`
- `TareasAdapter.java`
- `TareaViewHolder.java`
- `activity_tareas.xml`
- `item_tarea.xml`

**Funcionalidad:**
- RecyclerView con lista de tareas
- Chip Group para filtros (Todas/Pendientes/Completadas)
- Pull-to-refresh
- Estados visuales por color:
  - 🔴 Vencida
  - ⚠️ Próxima (3 días)
  - 🔵 Pendiente
  - ✅ Completada
- Navegación a detalle

**Estimación:** 6-8 horas

---

#### 4. TareaDetalleActivity
**Archivos a crear:** 6
- `TareaDetalleActivity.java`
- `ArchivosAdapter.java`
- `activity_tarea_detalle.xml`
- `item_archivo_preview.xml`
- `ImageCompressor.java` (utilidad)
- `FilePickerHelper.java` (utilidad)

**Funcionalidad:**
- ScrollView con detalle de tarea
- EditText multiline (500 chars)
- Selector de archivos (cámara/galería/archivos)
- RecyclerView horizontal para preview
- Compresión automática de imágenes
- Upload multipart con Retrofit
- Cola offline (PendingSubmissionDao)
- Máx 3 archivos, 2MB c/u

**Estimación:** 10-12 horas

---

### 🟡 PRIORIDAD MEDIA (3 vistas)

#### 5. SoporteActivity
**Archivos a crear:** 4
- `SoporteActivity.java`
- `FaqAdapter.java`
- `activity_soporte.xml`
- `item_faq.xml`
- `faq.json` (res/raw/)

**Funcionalidad:**
- RecyclerView expandible con FAQs
- Botón Email (Intent.ACTION_SENDTO)
- Botón WhatsApp (Intent.ACTION_VIEW)
- Formulario de reporte
- Funciona offline (FAQs cacheados)

**Estimación:** 4-5 horas

---

#### 6. HistorialActivity
**Archivos a crear:** 4
- `HistorialActivity.java`
- `HistorialAdapter.java`
- `activity_historial.xml`
- `item_entrega.xml`

**Funcionalidad:**
- RecyclerView con entregas pasadas
- Spinner selector de período
- Ver calificaciones y feedback
- Abrir evidencias enviadas
- Estadísticas del período

**Estimación:** 5-6 horas

---

#### 7. NotificacionesActivity
**Archivos a crear:** 4
- `NotificacionesActivity.java`
- `NotificacionesAdapter.java`
- `activity_notificaciones.xml`
- `item_notificacion.xml`

**Funcionalidad:**
- RecyclerView con notificaciones
- Separación No leídas / Anteriores
- Marcar como leída (API)
- Acciones por tipo:
  - Nueva tarea → Ver tarea
  - Recordatorio → Entregar
  - Calificación → Ver nota
- Eliminar todas leídas

**Estimación:** 5-6 horas

---

## 📅 CRONOGRAMA DE IMPLEMENTACIÓN

### Semana 1: Autenticación Completa
**Días 1-2:** CambiarContrasenaActivity  
**Días 3-5:** RecuperarContrasenaActivity (3 pantallas)

**Resultado semana 1:**
- ✅ 2 vistas nuevas (5/10 total = 50%)
- ✅ Flujo de autenticación completo

---

### Semana 2: Gestión de Tareas
**Días 1-2:** TareasActivity  
**Días 3-5:** TareaDetalleActivity

**Resultado semana 2:**
- ✅ 2 vistas nuevas (7/10 total = 70%)
- ✅ Funcionalidad core completa

---

### Semana 3: Utilidades y Pulido
**Día 1:** SoporteActivity  
**Día 2:** HistorialActivity  
**Día 3:** NotificacionesActivity  
**Días 4-5:** Testing general + correcciones

**Resultado semana 3:**
- ✅ 3 vistas nuevas (10/10 total = 100%)
- ✅ App completa y funcional

---

## 📦 DEPENDENCIAS ADICIONALES NECESARIAS

```gradle
dependencies {
    // ...existing...
    
    // Retrofit (API REST)
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'
    
    // Room Database (Caché offline)
    implementation 'androidx.room:room-runtime:2.6.1'
    annotationProcessor 'androidx.room:room-compiler:2.6.1'
    
    // DataStore (Preferencias)
    implementation 'androidx.datastore:datastore-preferences:1.0.0'
    
    // Compressor de imágenes
    implementation 'id.zelory:compressor:3.0.1'
    
    // Coil (Carga de imágenes)
    implementation 'io.coil-kt:coil:2.5.0'
    
    // SwipeRefreshLayout
    implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'
}
```

---

## 🎨 RECURSOS ADICIONALES NECESARIOS

### Iconos (res/drawable/)
- `ic_lock.xml` - Candado
- `ic_email.xml` - Email
- `ic_phone.xml` - Teléfono
- `ic_task.xml` - Tarea
- `ic_camera.xml` - Cámara
- `ic_gallery.xml` - Galería
- `ic_file.xml` - Archivo
- `ic_notification.xml` - Notificación
- `ic_history.xml` - Historial
- `ic_help.xml` - Ayuda
- `ic_send.xml` - Enviar
- `ic_check.xml` - Check/Completado
- `ic_warning.xml` - Advertencia
- `ic_error.xml` - Error

### Animaciones Lottie (res/raw/)
- `animation_success.json` - Éxito
- `animation_error.json` - Error
- `animation_loading.json` - Cargando
- `animation_empty.json` - Estado vacío

### JSON (res/raw/)
- `faq.json` - Preguntas frecuentes

---

## 📊 MÉTRICAS DE PROGRESO

### Estado Actual
- **Vistas completadas:** 3/10 (30%)
- **Archivos creados:** 22
- **Líneas de código:** ~2,000
- **Documentación:** 5 archivos MD

### Al Completar Todo
- **Vistas totales:** 10/10 (100%)
- **Archivos estimados:** ~60
- **Líneas de código:** ~8,000-10,000
- **Tiempo estimado:** 3 semanas

---

## ✅ CHECKLIST GENERAL

### Backend APIs (Por Implementar)
- [ ] POST /api/auth/cambiar-contrasena
- [ ] POST /api/auth/solicitar-recuperacion
- [ ] POST /api/auth/verificar-codigo
- [ ] POST /api/auth/restablecer-contrasena
- [ ] GET /api/asignaciones
- [ ] POST /api/entregas (multipart)
- [ ] GET /api/entregas
- [ ] GET /api/notificaciones
- [ ] PATCH /api/notificaciones/{id}/leer

### Testing (Por Realizar)
- [ ] Flujo completo de autenticación
- [ ] Navegación entre pantallas
- [ ] Funcionalidad offline
- [ ] Upload de archivos
- [ ] Estados de error
- [ ] Performance en dispositivos de gama baja

---

**📄 Documento creado:** 7 de Enero 2026  
**📱 Proyecto:** Cátedra de Familia - PARCHANDO JUNTOS  
**📊 Progreso actual:** 3/10 vistas (30%)  
**⏱️ Tiempo estimado restante:** 3 semanas (60-80 horas)  
**🎯 Próxima vista:** CambiarContrasenaActivity

---

## 🚀 PARA CONTINUAR

1. **Sincronizar Gradle** → Regenerar R.java
2. **Ejecutar app** → Verificar Onboarding funciona
3. **Implementar siguiente vista:** CambiarContrasenaActivity
4. **Documentar cada vista** en MD separado
5. **Testing continuo** después de cada vista

**¿Todo listo para continuar con CambiarContrasenaActivity?** 🎯

