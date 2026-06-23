FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY . .

RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/platform-core-app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]