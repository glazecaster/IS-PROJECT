@echo off
echo ========================================
echo Compilando ComeUCV - Sistema Integral
echo ========================================
echo.

cd /d "c:\Users\Rosa Amaro\Desktop\IS-PROJECT"

echo Creando carpeta bin...
if not exist bin mkdir bin

echo.
echo Compilando archivos...
javac -d bin -cp ".;src" src/app/App.java src/controller/*.java src/controlador/*.java src/modelo/*.java src/model/*.java src/vista/*.java src/view/*.java src/view/listeners/*.java

if %errorlevel% equ 0 (
    echo.
    echo ✅ Compilación exitosa!
    echo.
    echo ========================================
    echo Ejecutando ComeUCV...
    echo ========================================
    echo.
    java -cp bin app.App
) else (
    echo.
    echo ❌ Error en la compilación
    echo Revise los errores arriba
    pause
)