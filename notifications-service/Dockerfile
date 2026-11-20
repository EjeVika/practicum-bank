FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar notifications-service.jar

ENTRYPOINT ["java", "-jar", "notifications-service.jar"]