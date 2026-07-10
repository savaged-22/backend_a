# Etapa 1: Compilación
FROM maven:3.9.11-eclipse-temurin-21 AS builder

WORKDIR /app

# Copiar archivos de Maven
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Descargar dependencias
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# Copiar el código fuente
COPY src src

# Compilar la aplicación
RUN ./mvnw clean package -DskipTests

# Etapa 2: Ejecución
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]