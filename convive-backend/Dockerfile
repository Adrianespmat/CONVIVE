# Etapa de compilación
FROM maven:3.9.9-eclipse-temurin-17 AS compilacion
WORKDIR /app

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:17-jre
WORKDIR /app

RUN useradd --system --create-home --uid 1001 convive
COPY --from=compilacion /app/target/convive-backend-0.1.0-SNAPSHOT.jar app.jar
RUN chown -R convive:convive /app

USER convive
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
