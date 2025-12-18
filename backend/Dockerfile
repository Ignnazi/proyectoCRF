FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -q -B dependency:go-offline

COPY src ./src
RUN mvn -q -B clean package -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar ./app.jar

# Spring Boot por defecto escucha en 8080; docker-compose mapea 8080:8080
EXPOSE 8080

# Habilitar DevTools con opciones optimizadas para desarrollo
ENTRYPOINT ["java", \
    "-Dspring.devtools.restart.enabled=true", \
    "-Dspring.devtools.restart.poll-interval=1000", \
    "-Dspring.devtools.restart.quiet-period=500", \
    "-jar", "app.jar"]

# Nota: si cambias la estructura de la BD en los scripts dentro de ./sql, Docker solo los ejecuta
# la primera vez que se crea el contenedor de MySQL. Para re-aplicar los scripts de inicialización,
# elimina el contenedor y el volumen de la DB antes de levantar con `docker-compose up --build`.
