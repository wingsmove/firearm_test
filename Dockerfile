FROM node:24-alpine AS frontend-build

WORKDIR /workspace/frontend

COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci

COPY frontend/ ./
RUN npm run build

FROM maven:3.9.11-eclipse-temurin-17-alpine AS backend-build

WORKDIR /workspace

COPY pom.xml ./
COPY src ./src
COPY --from=frontend-build /workspace/frontend/dist ./src/main/resources/static

RUN mvn --batch-mode --no-transfer-progress clean package

FROM eclipse-temurin:17-jre-alpine

RUN addgroup -S app && adduser -S app -G app

WORKDIR /app

COPY --from=backend-build /workspace/target/firearm-test-1.0.0-SNAPSHOT.jar ./app.jar

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC"

EXPOSE 8080

USER app

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
