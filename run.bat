@echo off
echo Ejecutando HappyFeet Application...

REM Verificar que existe la clase compilada
if not exist "target\classes\com\happyfeet\HappyFeetApplication.class" (
    echo Error: No se encuentra la aplicación compilada.
    echo Ejecute compile.bat primero.
    exit /b 1
)

REM Ejecutar la aplicación
java -cp "lib\*;target\classes" com.happyfeet.HappyFeetApplication