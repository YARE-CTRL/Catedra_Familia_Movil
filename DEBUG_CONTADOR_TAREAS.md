# 🐛 GUÍA DE DEBUG - CONTADOR DE TAREAS SIGUE EN 0

## 🎯 **ESTADO ACTUAL**
- ✅ **Filtros funcionan** correctamente (ya confirmado)
- ❌ **Contador muestra 0** aunque hay tareas
- 🔧 **Debug implementado** para diagnosticar el problema

---

## 🛠️ **PASOS PARA DIAGNOSTICAR**

### **Paso 1: Verificar Logs de Debug**

1. **Abre Android Studio** y ve a la ventana "Logcat"
2. **Filtra por "MainActivity"** para ver solo los logs relevantes
3. **Ejecuta la app** y haz login
4. **Busca estos logs específicos:**

```
D/MainActivity: ✅ Token encontrado: eyJhbGciOiJIUzI1NiIs...
D/MainActivity: 🔄 Cargando estadísticas de tareas para: [Nombre del hijo]
D/MainActivity: 📡 Llamando API: /estudiantes/[ID]/tareas?estado=pendiente
D/MainActivity: 📡 Response pendientes - Código: [200/404/500/etc]
```

### **Paso 2: Usar el Botón de Debug**

1. **En la app, mantén presionado** el botón "Tareas" por 2-3 segundos
2. **Aparecerá un dialog** que dice "Debug - Recarga de Estadísticas"
3. **Toca OK** y revisa inmediatamente los logs
4. **El contador debería actualizarse** después de unos segundos

### **Paso 3: Interpretar los Resultados**

#### **✅ Escenario 1: Token Missing**
```
W/MainActivity: ⚠️ No hay token de autorización para cargar estadísticas
```
**Solución:** Hay problema con el login, el token no se guardó correctamente.

#### **✅ Escenario 2: API Error 404**
```
D/MainActivity: 📡 Response pendientes - Código: 404
W/MainActivity: ⚠️ Error al cargar tareas pendientes - Code: 404
```
**Solución:** El endpoint `/movil/estudiantes/:id/tareas` no existe o está mal configurado en el backend.

#### **✅ Escenario 3: API Error 401**
```
D/MainActivity: 📡 Response pendientes - Código: 401
```
**Solución:** Token expirado o inválido, necesitas hacer login nuevamente.

#### **✅ Escenario 4: Data Null**
```
D/MainActivity: 📡 Response body success: true
W/MainActivity: ⚠️ Response data es null para pendientes
```
**Solución:** El backend devuelve success=true pero data=null, revisar la estructura de respuesta.

#### **✅ Escenario 5: Todo OK pero contador 0**
```
D/MainActivity: 📋 [Nombre] - Pendientes: 0
D/MainActivity: ⚠️ [Nombre] - Vencidas: 0
D/MainActivity: 🔄 Actualizando badges - Revisando 1 hijos:
D/MainActivity: 📊 [Nombre] - P:0 V:0
D/MainActivity: 🎯 Totales - Pendientes: 0, Vencidas: 0, Total: 0
```
**Solución:** La API responde correctamente pero realmente no hay tareas. Verificar en el backend si hay tareas asignadas al estudiante.

---

## 🔍 **DIAGNÓSTICOS ESPECÍFICOS**

### **Verificar ID del Estudiante**
Los logs mostrarán algo como:
```
D/MainActivity: 📡 Llamando API: /estudiantes/123/tareas?estado=pendiente
```
**Confirma que el ID 123 corresponde al estudiante correcto en la base de datos.**

### **Verificar Estructura de Respuesta**
El backend debe devolver:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "titulo": "Tarea ejemplo",
      "estado": "pendiente"
    }
  ]
}
```

### **Verificar Estados de Tareas**
Los estados válidos son:
- `"pendiente"` - Para tareas dentro del plazo
- `"vencida"` - Para tareas fuera del plazo
- `"entregada"` / `"completada"` - Para tareas enviadas
- `"calificada"` - Para tareas evaluadas

---

## 🚀 **ACCIONES INMEDIATAS**

### **Para Ti (Frontend):**
1. ✅ **Ejecuta el debug** con long press en "Tareas"
2. ✅ **Revisa los logs** completos en Logcat
3. ✅ **Comparte los logs** específicos que veas
4. ✅ **Verifica el ID** del estudiante en los logs

### **Para el Backend:**
Una vez que tengas los logs, podremos identificar si necesitan:
1. 🔧 **Crear/corregir** el endpoint `/movil/estudiantes/:id/tareas`
2. 🔧 **Verificar autenticación** JWT
3. 🔧 **Revisar estructura** de respuesta
4. 🔧 **Confirmar datos** en base de datos

---

## 📋 **LOGS ESPERADOS (FUNCIONANDO CORRECTAMENTE)**

```
D/MainActivity: ✅ Token encontrado: eyJhbGciOiJIUzI1NiIs...
D/MainActivity: 🔄 Cargando estadísticas de tareas para: Juan Carlos García
D/MainActivity: 📡 Llamando API: /estudiantes/1/tareas?estado=pendiente
D/MainActivity: 📡 Response pendientes - Código: 200
D/MainActivity: 📡 Response body success: true
D/MainActivity: 📋 Juan Carlos García - Pendientes: 2
D/MainActivity: 📡 Llamando API: /estudiantes/1/tareas?estado=vencida
D/MainActivity: 📡 Response vencidas - Código: 200
D/MainActivity: ⚠️ Juan Carlos García - Vencidas: 1
D/MainActivity: ✅ Juan Carlos García - Completadas: 5
D/MainActivity: 📊 Juan Carlos García - Resumen:
D/MainActivity:    📋 Pendientes: 2
D/MainActivity:    ⚠️ Vencidas: 1
D/MainActivity:    ✅ Completadas: 5
D/MainActivity:    🎯 Estado: al_dia → con_vencidas
D/MainActivity: 🔄 UI actualizada para Juan Carlos García
D/MainActivity: 🔄 Actualizando badges - Revisando 1 hijos:
D/MainActivity:    📊 Juan Carlos García - P:2 V:1
D/MainActivity: 🎯 Totales - Pendientes: 2, Vencidas: 1, Total: 3
D/MainActivity: 📋 Badge actualizado: 3 por atender (1 vencidas)
```

---

## ⚡ **MENSAJE PARA EL BACKEND**

Una vez ejecutes el debug y veas los logs, necesitamos confirmar:

### **Endpoints que debe tener:**
```http
GET /api/movil/estudiantes/:id/tareas?estado=pendiente
GET /api/movil/estudiantes/:id/tareas?estado=vencida
GET /api/movil/estudiantes/:id/tareas?estado=completada
```

### **Headers requeridos:**
```http
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

### **Respuesta esperada:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "titulo": "Nombre de la tarea",
      "estado": "pendiente",
      "fechaVencimiento": "2026-02-15"
    }
  ]
}
```

---

**🎯 Ejecuta el debug y comparte los logs para continuar el diagnóstico!**

**Fecha:** 12 de febrero de 2026  
**Estado:** Esperando logs de debug para diagnóstico
