# Etapa de build
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY . /app

RUN gradle clean build -x validateStructure -x test --no-daemon

# Etapa runtime
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copiar el jar generado y renombrarlo
COPY --from=builder /app/build/libs/*.jar app.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
