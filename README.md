<p align="center">
  <a href="https://spring.io/projects/spring-boot" target="blank"><img src="https://static-00.iconduck.com/assets.00/spring-icon-1024x1023-ljxx8bf7.png" height="120" alt="Spring Boot Logo" /></a>
  <span style="display:inline-block; width: 50px;"></span>
  <a href="https://mercadolibre.com/" target="blank"><img src="https://companieslogo.com/img/orig/MELI-ec0c0e4f.png?t=1720244492" height="125" alt="Rebel Alliance Logo" /></a>
  <span style="display:inline-block; width: 50px;"></span>
  <a href="https://www.starwars.com" target="blank"><img src="https://www.pngkey.com/png/full/297-2971509_star-wars-rebel-symbol-png.png" height="125" alt="Rebel Alliance Logo" /></a>
</p>

# **Microservicio Operación Fuego de Quásar**
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)&nbsp;
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)&nbsp;
![Spring Boot](https://img.shields.io/badge/spring%20boot-%236DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white)&nbsp;
![MongoDB](https://img.shields.io/badge/MongoDB-%234ea94b.svg?style=for-the-badge&logo=mongodb&logoColor=white)&nbsp;
![ElasticSearch](https://img.shields.io/badge/-ElasticSearch-005571?style=for-the-badge&logo=elasticsearch)&nbsp;
![Git](https://img.shields.io/badge/git-%23F05032.svg?style=for-the-badge&logo=git&logoColor=white)&nbsp;
![Docker](https://img.shields.io/badge/docker-%232496ED.svg?style=for-the-badge&logo=docker&logoColor=white)&nbsp;
![Swagger](https://img.shields.io/badge/-Swagger-%85EA2D?style=for-the-badge&logo=swagger&logoColor=white)&nbsp;
## **Descripción**
El microservicio "Operación Fuego de Quásar" es una solución desarrollada en Java utilizando el framework Spring Boot 3 y MongoDB como base de datos. Este microservicio se encarga de procesar y analizar la información recibida de varios satélites para determinar la posición y el mensaje original emitido por una fuente desconocida. Implementa un enfoque reactivo utilizando Spring WebFlux para manejar de manera eficiente las solicitudes concurrentes y proporcionar respuestas rápidas.

El microservicio incluye capacidades avanzadas de monitoreo y trazabilidad mediante la integración con Elastic APM, lo que permite registrar errores y realizar un seguimiento detallado de las operaciones. Además, utiliza MongoDB no solo como base de datos principal, sino también para el registro de errores y la trazabilidad de las operaciones.

Para mejorar el rendimiento, el microservicio implementa un manejo de caché en las colecciones de MongoDB, lo que permite reducir la latencia y mejorar la eficiencia en el acceso a los datos. También cuenta con un manejo centralizado de errores y el uso de interceptores para guardar la trazabilidad de las operaciones.

## **Pre-requisitos**
Para clonar y ejecutar esta aplicación, necesitará [Git](https://git-scm.com), [Java 17.0.11](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html), [Maven](https://maven.apache.org/download.cgi) (Opcional) y [Docker](https://docs.docker.com/engine/install/) instalados en su computadora. 

Desde su línea de comando:

```bash
# Clonar repositorio
$ git clone https://github.com/dgcorredorr/ms-template-spring-webflux

# Entrar al repositorio local
$ cd ms-template-spring-webflux

# Instalar dependencias
$ ./mvnw clean install
```

**NOTA: Antes de correr el proyecto, asegúrese de ejecutar con Docker el archivo compose.yaml.**

```bash
$ docker compose -f "compose.yaml" up -d --build
```

```bash
# Correr aplicación en modo desarrollo
$ ./mvnw clean spring-boot:run
```

Luego podrá acceder desde el [navegador](http://localhost:8081/) para validar que se visualice correctamente la documentación de Swagger del proyecto.

## **Ejecutar pruebas unitarias y SonarQube**

Si no cuenta con un servidor SonarQube remoto, tiene la posibilidad de ejecutarlo mediante un contendedor Docker con el siguiente comando:

```bash
$ docker run -d --name sonarqube -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true -p 9000:9000 sonarqube:latest
```

Desde su línea de comando:

```bash
# Comando para ejecutar pruebas unitarias con cobertura y generar informe herramienta SonarQube
$ mvn clean verify sonar:sonar -Dsonar.login=<SONARQUBE_LOGIN> -Dsonar.password=<SONARQUBE_PASSWORD>
-Dsonar.projectKey=ms-template-spring-webflux -Dsonar.projectName='ms-template-spring-webflux' -Dsonar.host.url=<SONARQUBE_URL>
```
Una vez finalizado el proceso, podrá acceder desde el [navegador](http://localhost:9000/projects?sort=name) para validar que se visualice correctamente el informe de SonarQube.

## **Módulos del proyecto**

- **Common**:
  Módulo transversal en el cual se define la configuración, librerías, enumeradores, útilidades.

- **Application**:
  Modulo en el cual se definen los paths o funcionalidades que expone el servicio

- **Core**:
  Módulo en el cual se implementa la lógica de negocio (use cases)

- **Provider**:
  Módulo que controla la conexión a legados, base de datos y servicios con los cuales se tiene comunicación

## **Autores**
Los diferentes autores y encargados de cada operación de la aplicación para inquietudes son:

| Operación             | Autor                  | Correo                    |
| --------------------- |------------------------|---------------------------|
| General               | David Corredor Ramírez | dgcorredorr@gmail.com     |