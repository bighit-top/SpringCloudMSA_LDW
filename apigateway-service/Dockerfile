FROM openjdk:11.0.15-slim
VOLUME /tmp
COPY target/apigateway-service-0.0.1-SNAPSHOT.jar ApigatewayService.jar
ENTRYPOINT ["java", "-jar", "ApigatewayService.jar"]