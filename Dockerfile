FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY . .
RUN mvn -B -DskipTests package
FROM eclipse-temurin:17-jre
RUN useradd --system --uid 10001 treasury
USER treasury
COPY --from=build /workspace/treasury-boot/target/treasury-boot-1.0.0-SNAPSHOT.jar /app/service.jar
EXPOSE 8080
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75.0","-jar","/app/service.jar"]

