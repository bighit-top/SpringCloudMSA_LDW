FROM openjdk:11.0.15-slim
VOLUME /tmp
COPY apiEncryptionKey.jks apiEncryptionKey.jks
COPY target/config-service-0.0.1-SNAPSHOT.jar ConfigService.jar
ENTRYPOINT ["java", "-jar", "ConfigService.jar"]