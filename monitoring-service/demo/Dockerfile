# Etapa 1: Construirea (Build)
# Imaginea CORECTĂ care conține Maven și JDK 23
FROM maven:3-eclipse-temurin-23 AS build
WORKDIR /app
# Copiem fișierul pom.xml și descărcăm dependențele
COPY pom.xml .
RUN mvn dependency:go-offline
# Copiem restul codului sursă și construim aplicația
COPY src ./src
RUN mvn package -DskipTests

# Etapa 2: Rularea (Run)
# Imaginea CORECTĂ care conține JRE 23
FROM eclipse-temurin:23-jre
WORKDIR /app
# Copiem fișierul .jar construit în etapa anterioară
COPY --from=build /app/target/*.jar app.jar
# Expunem portul pe care rulează aplicația (8083 pentru user-service)
EXPOSE 8083
# Comanda de pornire a aplicației
ENTRYPOINT ["java", "-jar", "app.jar"]