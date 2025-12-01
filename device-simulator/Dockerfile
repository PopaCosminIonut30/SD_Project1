# Etapa 1: Construirea (Build)
# Imaginea CORECTĂ care conține Maven și JDK 23
FROM maven:3-eclipse-temurin-23 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# Etapa 2: Rularea (Run)
# Imaginea CORECTĂ care conține JRE 23
FROM eclipse-temurin:23-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# Expunem portul 8081 (device-service)
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]