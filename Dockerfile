# syntax=docker/dockerfile:1.7

FROM maven:3.9.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

COPY pom.xml .

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp package -Dmaven.test.skip=true && \
    JAR_FILE=$(find target -maxdepth 1 -type f -name "*.jar" ! -name "*.original" | head -n 1) && \
    cp "$JAR_FILE" app.jar

FROM bellsoft/liberica-runtime-container:jre-17-musl

WORKDIR /app

ENV SERVER_PORT=8080
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0"

COPY --from=build /app/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
