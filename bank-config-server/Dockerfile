FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar bank-config-server.jar

ENTRYPOINT ["java", "-jar", "bank-config-server.jar"]