FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd -r -u 1001 qc && mkdir -p /app/storage && chown qc /app/storage
COPY --from=build /app/target/qurbaniconnect-1.0.0.jar app.jar
USER qc
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
