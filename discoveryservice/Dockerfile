FROM openjdk:11.0.15-slim
VOLUME /tmp
COPY target/discoveryservice-0.0.1-SNAPSHOT.jar DiscoveryService.jar
ENTRYPOINT ["java", "-jar", "DiscoveryService.jar"]