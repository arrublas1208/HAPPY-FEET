@echo off
echo Compilando proyecto HappyFeet...

REM Crear directorios de salida
if not exist "target\classes" mkdir target\classes
if not exist "target\test-classes" mkdir target\test-classes

REM Compilar código principal
echo Compilando código fuente principal...
javac -cp "lib\*" -d target\classes -sourcepath src\main\java src\main\java\com\happyfeet\*.java src\main\java\com\happyfeet\**\*.java

if %errorlevel% neq 0 (
    echo Error en la compilación del código principal
    exit /b 1
)

REM Copiar recursos
echo Copiando recursos...
if exist "src\main\resources" xcopy /s /y "src\main\resources\*" "target\classes\"
copy "database.properties" "target\classes\"

REM Compilar tests
echo Compilando tests...
javac -cp "lib\*;target\classes" -d target\test-classes -sourcepath src\test\java src\test\java\**\*.java

echo Compilación completada exitosamente!
echo Para ejecutar: java -cp "lib\*;target\classes" com.happyfeet.HappyFeetApplication