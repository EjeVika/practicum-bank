FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar bank-ui.jar

ENTRYPOINT ["java", "-jar", "bank-ui.jar"]