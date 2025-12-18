#!/bin/bash

# Script para desarrollo con hot reload

echo "🚀 Iniciando modo de desarrollo con hot reload..."
echo ""

cd "$(dirname "$0")"

case "$1" in
    start)
        echo "📦 Levantando contenedores con hot reload..."
        docker-compose up --build -d
        echo ""
        echo "✅ Contenedores levantados!"
        echo ""
        echo "📝 Para ver los logs del backend:"
        echo "   docker logs -f proyectoCRF_backend"
        echo ""
        echo "💡 Ahora, cuando compiles el backend localmente (en tu IDE o con './backend/recompilar.sh'),"
        echo "   los cambios se reflejarán automáticamente en el contenedor."
        ;;

    stop)
        echo "🛑 Deteniendo contenedores..."
        docker-compose down
        echo "✅ Contenedores detenidos"
        ;;

    restart)
        echo "🔄 Reiniciando backend..."
        docker-compose restart backend
        echo "✅ Backend reiniciado"
        ;;

    logs)
        echo "📋 Mostrando logs del backend (Ctrl+C para salir)..."
        docker logs -f proyectoCRF_backend
        ;;

    compile)
        echo "🔨 Compilando backend localmente..."
        cd backend
        ./mvnw clean package -DskipTests
        if [ $? -eq 0 ]; then
            echo ""
            echo "✅ Backend compilado! Los cambios se reflejarán automáticamente en Docker."
        else
            echo ""
            echo "❌ Error al compilar"
            exit 1
        fi
        ;;

    *)
        echo "Uso: $0 {start|stop|restart|logs|compile}"
        echo ""
        echo "  start    - Levantar contenedores con hot reload"
        echo "  stop     - Detener contenedores"
        echo "  restart  - Reiniciar solo el backend"
        echo "  logs     - Ver logs del backend en tiempo real"
        echo "  compile  - Compilar backend (los cambios se aplican automáticamente)"
        echo ""
        exit 1
        ;;
esac

