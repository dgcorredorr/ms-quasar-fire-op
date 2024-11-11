# Etapa 1: Construcción de la aplicación
FROM maven:3.8.5-openjdk-17 AS builder

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar los archivos de configuración de Maven y las dependencias
COPY pom.xml .
COPY src ./src

# Compilar la aplicación y empaquetarla en un archivo JAR
RUN mvn clean package -DskipTests

# Etapa 2: Creación de la imagen de producción
FROM openjdk:17-jdk-alpine

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el archivo JAR desde la etapa de construcción
COPY --from=builder /app/target/*.jar app.jar

# Exponer el puerto que la aplicación utiliza
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app/app.jar"]