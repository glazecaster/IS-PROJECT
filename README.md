ComeUCV – Sistema de Gestión del Comedor Universitario

Aplicación de escritorio desarrollada en Java con Swing para la gestión del comedor universitario. El sistema permite administrar usuarios, control de saldos (monedero), cálculo de costos (CCB) y configuración de menús con perfil administrativo. La persistencia se maneja mediante archivos de texto planos.

Misión
Proporcionar una herramienta práctica y funcional para la administración del servicio de comedor, facilitando el control de usuarios, menús y cobros diarios mediante una interfaz gráfica sencilla e intuitiva.

Visión
Convertirse en una solución escalable para la gestión de comedores universitarios, mejorando progresivamente la experiencia de usuario y migrando hacia un sistema con base de datos que permita mayor trazabilidad y robustez.

Creadores:
Equipo: N11

Integrantes:

- Javier Ramirez C.I:31707800

- Nil Colmenares C.I:31451324

- José Velázquez C.I:32352830

- Santiago Flores C.I: 32606881


Tecnologías utilizadas:
- Java JDK 17+

- Swing para la interfaz gráfica

- JUnit 5 para pruebas unitarias (junit-platform-console-standalone-1.10.2)

- Archivos de texto (.txt) como mecanismo de persistencia

- VS Code con extensiones de Java (opcional)

Estructura del proyecto:
- src_old/ - Código fuente bajo arquitectura MVC

- test/ - Pruebas unitarias y datos de prueba

- lib/ - Librerías externas (JUnit)

- data/ - Archivos de datos del sistema

- .vscode/ - Configuración para VS Code

Datos del sistema:
Los archivos de datos están centralizados en la carpeta data/:

- base_datos_comedor.txt

- base_datos_menus.txt

- base_datos_menu_semana.txt

- base_datos_costos.txt

- base_datos_ingredientes.txt

- base_datos_recetas.txt

- usuarios.txt

El sistema busca los archivos primero en data/ y, si no los encuentra, recurre a rutas alternativas como test/ o la raíz del proyecto.

Cómo ejecutar la aplicación:
Abres la carpeta src_old, y en la carpeta app, ejecutas el archivo App.java

Como ejecutar las pruebas:
Tenemos la extension test runner de java que nos permite ejecutar las pruebas

Uso básico del sistema:
1. Inicia la aplicación ejecutando app.App

2. Inicia sesión con un usuario registrado o crea uno nuevo

3. Como comensal puedes consultar tu saldo y simular consumos

4. Como administrador puedes configurar los menús semanales y gestionar costos

Notas adicionales:
Este proyecto fue desarrollado con fines académicos como parte de la formación en Ingeniería de Software.