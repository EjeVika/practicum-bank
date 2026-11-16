FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar accounts-service.jar

ENTRYPOINT ["java", "-jar", "accounts-service.jar"]