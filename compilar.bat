@echo off
REM Script para compilar la app sin errores de JAVA_HOME
REM Fecha: 16 febrero 2026

echo ========================================
echo   COMPILANDO CATEDRA FAM - v1.0.1
echo ========================================
echo.

REM Limpiar JAVA_HOME si existe
set JAVA_HOME=

echo [1/3] Limpiando build anterior...
call gradlew clean --no-daemon

echo.
echo [2/3] Compilando APK Debug...
call gradlew assembleDebug --no-daemon

echo.
echo [3/3] Verificando APK generado...
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo.
    echo ========================================
    echo   ✅ COMPILACION EXITOSA
    echo ========================================
    echo.
    echo APK generado en:
    echo app\build\outputs\apk\debug\app-debug.apk
    echo.
    dir "app\build\outputs\apk\debug\app-debug.apk"
    echo.
    echo Para instalar en dispositivo:
    echo adb install -r app\build\outputs\apk\debug\app-debug.apk
    echo.
) else (
    echo.
    echo ========================================
    echo   ❌ ERROR EN COMPILACION
    echo ========================================
    echo.
    echo Revisar errores arriba
    echo.
)

pause
