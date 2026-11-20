FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar cash-service.jar

ENTRYPOINT ["java", "-jar", "cash-service.jar"]