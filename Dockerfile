FROM node:20-alpine AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

FROM maven:3.9-eclipse-temurin-17 AS backend-build
WORKDIR /app
COPY backend/ backend/
COPY --from=frontend-build /app/frontend/dist/ backend/src/main/resources/static/
WORKDIR /app/backend
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S examonline && adduser -S examonline -G examonline && mkdir -p /app/data && chown -R examonline:examonline /app
COPY --from=backend-build /app/backend/target/*.jar /app/app.jar
ENV JAVA_OPTS=""
EXPOSE 8080
USER examonline
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
