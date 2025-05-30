FROM openjdk:17

COPY target/backend-service.jar backend-service.jar

ENTRYPOINT ["java", "-jar", "backend-service.jar"]

EXPOSE 8080