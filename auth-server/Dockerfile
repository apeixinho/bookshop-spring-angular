# syntax=docker/dockerfile:1
FROM eclipse-temurin:21-jdk-alpine AS build
RUN apk add --no-cache maven
WORKDIR /opt/app
COPY pom.xml .
COPY src src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /opt/app
RUN apk add --no-cache curl \
  && addgroup --system javauser && adduser -S -s /usr/sbin/nologin -G javauser javauser \
  && mkdir -p /opt/app/data \
  && chown -R javauser:javauser /opt/app/data
COPY --from=build --chown=javauser:javauser /opt/app/target/*.jar app.jar
USER javauser
ENV AUTH_JWK_PATH=/opt/app/data/auth-jwk.json
EXPOSE 9000
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:9000/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
