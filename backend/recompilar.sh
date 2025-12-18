#!/bin/bash

echo "🔨 Recompilando el backend..."
echo ""

cd "$(dirname "$0")"

# Dar permisos de ejecución a mvnw si no los tiene
chmod +x mvnw

# Limpiar y compilar
./mvnw clean package -DskipTests

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Backend recompilado exitosamente!"
    echo ""
    echo "Para ejecutar el backend, usa:"
    echo "  ./mvnw spring-boot:run"
    echo ""
    echo "O ejecuta directamente el JAR:"
    echo "  java -jar target/user-0.0.1-SNAPSHOT.jar"
else
    echo ""
    echo "❌ Error al recompilar el backend"
    exit 1
fi

