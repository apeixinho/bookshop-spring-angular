# syntax=docker/dockerfile:1
FROM eclipse-temurin:21-jdk-alpine AS build
RUN apk add --no-cache maven
WORKDIR /opt/app
COPY pom.xml .
COPY src src
ARG SPRING_PROFILES_ACTIVE=dev
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /opt/app
RUN apk add --no-cache curl \
  && addgroup --system javauser && adduser -S -s /usr/sbin/nologin -G javauser javauser
COPY --from=build --chown=javauser:javauser /opt/app/target/*.jar app.jar
USER javauser
EXPOSE 8090
ARG SPRING_PROFILES_ACTIVE=dev
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8090/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
