# Catalog Service

Spring Boot 3.2 microservice exposing product catalog APIs with persistence in MySQL and search indexing in Elasticsearch.

## Commands

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

## Environment

- `SPRING_DATASOURCE_URL`
- `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI`
- `SERVICES_USERS_BASE_URL`
