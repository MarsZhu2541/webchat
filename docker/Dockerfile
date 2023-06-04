FROM openjdk:11
LABEL authors="marszhu"
ADD ../target/webchat-backend-1.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
