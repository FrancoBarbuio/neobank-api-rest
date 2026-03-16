# Imagen oficial de Maven con Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copiado de los archivos de configuración y el código fuente
COPY pom.xml .
COPY src ./src
# Compilado del proyecto e ignoramos los tests
RUN mvn clean package -DskipTests

# imagen ligera solo tiene el entorno de ejecución de Java 21
FROM eclipse-temurin:21-jre
WORKDIR /app
# Copiamos el archivo .jar
COPY --from=build /app/target/*.jar app.jar
# Expone el puerto de nuestra API
EXPOSE 8080
# Como arrancar la aplicacion
ENTRYPOINT ["java", "-jar", "app.jar"]