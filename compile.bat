@echo off
title Compilador ComeUCV
echo ========================================
echo    COMPILADOR DEL SISTEMA COMECUCV
echo ========================================

echo Limpiando compilaciones anteriores...
if exist bin rmdir /s /q bin
mkdir bin
mkdir bin\controlador
mkdir bin\modelo
mkdir bin\vista

echo Creando carpeta para archivos de datos...
if not exist test mkdir test

echo.
echo ========================================
echo Compilando todos los archivos...
echo ========================================

javac -d bin -sourcepath src src\modelo\*.java src\vista\*.java src\controlador\*.java src\Main.java

if %errorlevel% neq 0 goto error

echo.
echo ========================================
echo Compilacion EXITOSA!
echo ========================================

echo.
echo Ejecutando sistema ComeUCV...
echo.
java -cp bin Main

if %errorlevel% neq 0 (
    echo.
    echo ERROR: No se pudo ejecutar el programa
    pause
    exit /b 1
)

goto fin

:error
echo.
echo ERROR: Fallo en la compilacion
echo Verifica que todos los archivos esten en las carpetas correctas
echo.
pause
exit /b 1

:fin
pause