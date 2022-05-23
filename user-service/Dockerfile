FROM openjdk:11.0.15-slim
VOLUME /tmp
COPY target/user-service-0.0.1-SNAPSHOT.jar UserService.jar
ENTRYPOINT ["java", "-jar", "UserService.jar"]