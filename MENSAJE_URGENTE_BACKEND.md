# 🚨 PROBLEMA IDENTIFICADO: CONTADOR EN 0 PERO BACKEND TIENE TAREAS

## 🔍 **DIAGNÓSTICO CONFIRMADO**

### **Backend tiene las tareas:**
- **1 PENDIENTE:** "aqui fue"
- **3 VENCIDAS:** "taller avengers", "messi", "ESTA FUE", "cr7" 
- **1 CALIFICADA:** "taller avengers"
- **Total que DEBE mostrar el contador: 4 por atender**

### **App móvil muestra:**
- **0 pendientes** ❌ INCORRECTO

## 🎯 **CAUSA DEL PROBLEMA**

La app móvil no puede acceder a estas tareas porque **falta un endpoint específico** o el endpoint existente no devuelve las tareas en el formato correcto.

---

## 📡 **SOLUCIÓN INMEDIATA - ENDPOINT REQUERIDO**

### **Opción 1: Endpoint de Estadísticas (RECOMENDADO)**

```http
GET /api/movil/estudiantes/:id/estadisticas-tareas
Authorization: Bearer {JWT_TOKEN}
```

**Respuesta esperada:**
```json
{
  "success": true,
  "data": {
    "estudianteId": 1,
    "estadisticas": {
      "pendientes": 1,
      "vencidas": 3,
      "completadas": 0,
      "entregadas": 0,
      "calificadas": 1
    },
    "total": {
      "porAtender": 4,
      "completadas": 1
    }
  }
}
```

### **Opción 2: Corregir Endpoint Existente**

El endpoint `/api/movil/estudiantes/:id/tareas` debe devolver:

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "titulo": "aqui fue",
      "estado": "pendiente",
      "fechaVencimiento": "2026-02-15"
    },
    {
      "id": 2, 
      "titulo": "messi",
      "estado": "vencida",
      "fechaVencimiento": "2026-02-10"
    },
    {
      "id": 3,
      "titulo": "ESTA FUE", 
      "estado": "vencida",
      "fechaVencimiento": "2026-02-08"
    },
    {
      "id": 4,
      "titulo": "cr7",
      "estado": "vencida", 
      "fechaVencimiento": "2026-02-09"
    },
    {
      "id": 5,
      "titulo": "taller avengers",
      "estado": "calificada",
      "fechaVencimiento": "2026-02-07"
    }
  ]
}
```

---

## 🔧 **ACCIONES INMEDIATAS DEL BACKEND**

### **1. Crear/Verificar Rutas:**
```javascript
// En routes/movil.ts
router.get('/estudiantes/:id/tareas', [MovilController, 'obtenerTareasEstudiante'])
router.get('/estudiantes/:id/estadisticas-tareas', [MovilController, 'obtenerEstadisticasTareas']) // NUEVO
```

### **2. Implementar Controlador:**
```javascript
// En MovilController
async obtenerTareasEstudiante({ params, auth }: HttpContext) {
  const estudianteId = params.id
  const userId = auth.user!.id
  
  // Verificar que el acudiente puede ver las tareas de este estudiante
  const acudiente = await Acudiente.findBy('usuario_id', userId)
  const esVinculado = await EstudianteAcudiente
    .query()
    .where('estudiante_id', estudianteId)
    .where('acudiente_id', acudiente.id)
    .first()
    
  if (!esVinculado) {
    return response.status(403).json({
      success: false,
      message: 'No tienes acceso a las tareas de este estudiante'
    })
  }
  
  // Obtener tareas con estados correctos
  const tareas = await Asignacion
    .query()
    .preload('tarea')
    .where('estudiante_id', estudianteId)
    .where('periodo_id', periodoActivo.id)
    
  const tareasFormateadas = tareas.map(asignacion => ({
    id: asignacion.id,
    titulo: asignacion.tarea.titulo,
    estado: calcularEstadoTarea(asignacion), // Función que determine el estado
    fechaVencimiento: asignacion.fechaVencimiento,
    // ... otros campos necesarios
  }))
  
  return {
    success: true,
    data: tareasFormateadas
  }
}
```

### **3. Función para Calcular Estado:**
```javascript
function calcularEstadoTarea(asignacion) {
  const ahora = new Date()
  const fechaVencimiento = new Date(asignacion.fechaVencimiento)
  
  // Si tiene calificación → calificada
  if (asignacion.calificacion) {
    return 'calificada'
  }
  
  // Si tiene entrega → entregada/completada
  if (asignacion.entrega) {
    return 'entregada'
  }
  
  // Si pasó la fecha → vencida
  if (ahora > fechaVencimiento) {
    return 'vencida'
  }
  
  // Caso contrario → pendiente
  return 'pendiente'
}
```

---

## 🧪 **TESTING INMEDIATO**

### **Paso 1: Verificar Endpoint**
```bash
curl -H "Authorization: Bearer {JWT_TOKEN}" \
  https://escuelaparapadres-backend-1.onrender.com/api/movil/estudiantes/1/tareas
```

**Debe devolver las 5 tareas con estados correctos.**

### **Paso 2: Verificar Estados**
La respuesta debe tener:
- 1 tarea con `"estado": "pendiente"`
- 3 tareas con `"estado": "vencida"`  
- 1 tarea con `"estado": "calificada"`

### **Paso 3: Testing en App**
1. Compila la app con los cambios
2. Haz long press en "Tareas" 
3. Revisa logs - debe mostrar:
   ```
   📊 Total de tareas recibidas: 5
   📋 Tarea: "aqui fue" - Estado: pendiente
   📋 Tarea: "messi" - Estado: vencida
   📊 Conteo final: Pendientes: 1, Vencidas: 3
   🎯 Total por atender: 4
   ```

---

## ⚡ **RESULTADO ESPERADO**

Después de implementar esto:
- **Badge:** "4 por atender (3 vencidas)"
- **Alerta:** "⚠️ 3 vencidas, 1 pendientes"
- **Contador funcional:** ✅

---

**🚨 ACCIÓN REQUERIDA:** Backend debe implementar uno de los endpoints mencionados INMEDIATAMENTE.

**Prioridad:** ALTA - El contador es funcionalidad crítica para los usuarios.

**Estado:** Esperando implementación del backend.
