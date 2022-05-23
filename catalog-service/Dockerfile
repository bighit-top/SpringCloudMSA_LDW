FROM openjdk:11.0.15-slim
VOLUME /tmp
COPY target/catalog-service-0.0.1-SNAPSHOT.jar CatalogService.jar
ENTRYPOINT ["java", "-jar", "CatalogService.jar"]