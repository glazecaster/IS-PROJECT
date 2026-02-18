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
javac -d bin -cp ".;src_old" src_old/app/App.java src_old/controller/*.java src_old/controlador/*.java src_old/modelo/*.java src_old/model/*.java src_old/vista/*.java src_old/view/*.java src_old/view/listeners/*.java

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