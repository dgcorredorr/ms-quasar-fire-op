# Usar una imagen base de Alpine Linux
FROM alpine:3.19.3

# Actualizar los repositorios e instalar dependencias necesarias
RUN apk update && apk upgrade && \
    apk add --no-cache curl tar bash openjdk17 maven

# Establecer JAVA_HOME y añadirlo al PATH
ENV JAVA_HOME /usr/lib/jvm/java-17-openjdk
ENV PATH $JAVA_HOME/bin:$PATH

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /usr/src/app

# Copiar el archivo de configuración de Maven
COPY pom.xml ./

# Copiar los archivos de la aplicación al directorio de trabajo del contenedor
COPY src ./src

# Instalar las dependencias de la aplicación (descargar las dependencias)
RUN mvn dependency:resolve

# Compilar la aplicación
RUN mvn package

# Exponer el puerto que la aplicación usa
EXPOSE 8080

# Comando para ejecutar la aplicación en modo desarrollo
CMD ["mvn", "spring-boot:run"]