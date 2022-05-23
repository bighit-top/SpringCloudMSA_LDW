FROM openjdk:11.0.15-slim
VOLUME /tmp
COPY target/order-service-0.0.1-SNAPSHOT.jar OrderService.jar
ENTRYPOINT ["java", "-jar", "OrderService.jar"]