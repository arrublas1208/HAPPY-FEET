#!/bin/bash
echo "Ejecutando HappyFeet Application..."

# Verificar que existe la clase compilada
if [ ! -f "target/classes/com/happyfeet/HappyFeetApplication.class" ]; then
    echo "Error: No se encuentra la aplicación compilada."
    echo "Ejecute compile.sh primero."
    exit 1
fi

# Ejecutar la aplicación
java -cp "lib/*:target/classes" com.happyfeet.HappyFeetApplication