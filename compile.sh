#!/bin/bash
echo "Compilando proyecto HappyFeet..."

# Crear directorios de salida
mkdir -p target/classes
mkdir -p target/test-classes

# Compilar código principal
echo "Compilando código fuente principal..."
find src/main/java -name "*.java" > sources.txt
javac -encoding UTF-8 -cp "lib/*" -d target/classes @sources.txt

if [ $? -ne 0 ]; then
    echo "Error en la compilación del código principal"
    exit 1
fi

# Copiar recursos
echo "Copiando recursos..."
if [ -d "src/main/resources" ]; then
    cp -r src/main/resources/* target/classes/
fi
cp database.properties target/classes/

# Compilar tests
echo "Compilando tests..."
if [ -d "src/test/java" ]; then
    find src/test/java -name "*.java" > test-sources.txt
    if [ -s test-sources.txt ]; then
        javac -encoding UTF-8 -cp "lib/*:target/classes" -d target/test-classes @test-sources.txt
    fi
fi

echo "Compilación completada exitosamente!"
echo "Para ejecutar: java -cp \"lib/*:target/classes\" com.happyfeet.HappyFeetApplication"

# Limpiar archivos temporales
rm -f sources.txt test-sources.txt