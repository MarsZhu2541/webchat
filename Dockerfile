FROM openjdk:11
LABEL authors="marszhu"
ADD ./target/webchat-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
