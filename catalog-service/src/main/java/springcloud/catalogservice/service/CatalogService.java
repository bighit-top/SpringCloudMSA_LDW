package springcloud.catalogservice.service;

import springcloud.catalogservice.entity.CatalogEntity;

public interface CatalogService {
    Iterable<CatalogEntity> getAllCatalogs();
}
