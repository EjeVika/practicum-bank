FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar bank-gateway.jar

ENTRYPOINT ["java", "-jar", "bank-gateway.jar"]