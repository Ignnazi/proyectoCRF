# 🔥 Hot Reload - Configuración de Desarrollo

Este proyecto está configurado para **reflejar cambios automáticamente** sin necesidad de reiniciar Docker manualmente.

## ✨ Características

- ✅ **Spring Boot DevTools** habilitado
- ✅ **Bind Mounts** configurados en Docker
- ✅ **Hot Reload** automático cuando compiles
- ✅ Scripts de ayuda para desarrollo

---

## 🚀 Cómo usar

### Opción 1: Script de desarrollo (Recomendado)

```bash
# Levantar contenedores con hot reload
./desarrollo.sh start

# Compilar backend (los cambios se aplican automáticamente)
./desarrollo.sh compile

# Ver logs en tiempo real
./desarrollo.sh logs

# Reiniciar solo el backend
./desarrollo.sh restart

# Detener todo
./desarrollo.sh stop
```

### Opción 2: Comandos manuales

```bash
# 1. Levantar contenedores
docker-compose up --build -d

# 2. Compilar backend cuando hagas cambios
cd backend
./mvnw clean package -DskipTests

# 3. Ver logs
docker logs -f proyectoCRF_backend
```

---

## 🔄 Flujo de trabajo

1. **Levanta los contenedores UNA VEZ:**
   ```bash
   ./desarrollo.sh start
   ```

2. **Edita tu código** en el IDE (IntelliJ, VS Code, etc.)

3. **Compila el proyecto:**
   - **Desde IntelliJ:** Build → Build Project (⌘F9)
   - **Desde terminal:** `./desarrollo.sh compile`
   - **Desde backend:** `cd backend && ./mvnw package -DskipTests`

4. **Los cambios se reflejan automáticamente** en el contenedor Docker (en 1-2 segundos)

5. **Recarga tu navegador** para ver los cambios en el frontend

---

## 📋 Verificar que funciona

1. Haz un cambio en cualquier archivo Java (por ejemplo, agrega un log)
2. Compila el proyecto
3. Observa los logs del backend:
   ```bash
   ./desarrollo.sh logs
   ```
4. Deberías ver algo como:
   ```
   Restarting due to class changes...
   ```

---

## ⚙️ Cómo funciona

### 1. Spring Boot DevTools
- Detecta cambios en las clases compiladas
- Reinicia automáticamente el contexto de la aplicación
- Configurado en `application.properties`

### 2. Bind Mounts en Docker
El `docker-compose.yml` mapea:
```yaml
volumes:
  - ./backend/src:/app/src      # Código fuente
  - ./backend/target:/app/target # Clases compiladas
```

Cuando compilas localmente, Docker ve los archivos nuevos inmediatamente.

### 3. Dockerfile Optimizado
- Usa `eclipse-temurin:21-jdk` (no JRE) para DevTools
- Habilita restart automático
- Configurado para polling cada 1 segundo

---

## 🛠️ Configuración de tu IDE

### IntelliJ IDEA

1. **Build automático:**
   - Preferences → Build, Execution, Deployment → Compiler
   - ✅ Marca "Build project automatically"

2. **Compilar con atajo:**
   - ⌘F9 (Mac) o Ctrl+F9 (Windows/Linux)
   - Build → Build Project

### VS Code

1. Instala la extensión "Java Extension Pack"
2. Los cambios se compilan automáticamente al guardar

---

## 🐛 Solución de problemas

### Los cambios no se reflejan

1. **Verifica que el volumen esté mapeado:**
   ```bash
   docker inspect proyectoCRF_backend | grep Mounts -A 20
   ```

2. **Asegúrate de compilar después de cada cambio:**
   ```bash
   cd backend && ./mvnw package -DskipTests
   ```

3. **Revisa los logs del backend:**
   ```bash
   ./desarrollo.sh logs
   ```

### DevTools no reinicia

1. **Verifica que DevTools esté en el pom.xml:**
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-devtools</artifactId>
       <optional>true</optional>
   </dependency>
   ```

2. **Comprueba la configuración en application.properties:**
   ```properties
   spring.devtools.restart.enabled=true
   ```

### Reinicio manual

Si algo falla, siempre puedes reiniciar:
```bash
./desarrollo.sh restart
```

O reconstruir todo:
```bash
docker-compose down
docker-compose up --build -d
```

---

## 📚 Recursos

- [Spring Boot DevTools Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.devtools)
- [Docker Volumes](https://docs.docker.com/storage/volumes/)

---

## 🎯 Resumen

✅ **Ya no necesitas** `docker-compose down && docker-compose up --build` cada vez

✅ **Solo compila** y los cambios se aplican automáticamente

✅ **Desarrollo más rápido** - cambios en 1-2 segundos en lugar de 1-2 minutos

