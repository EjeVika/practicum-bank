FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar transfer-service.jar

ENTRYPOINT ["java", "-jar", "transfer-service.jar"]