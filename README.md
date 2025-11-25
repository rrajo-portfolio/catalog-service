# Catalog Service

## Purpose
Product catalog microservice responsible for storing product metadata, pricing, and lifecycle information so the rest of the platform can surface curated inventory. It proves how a Spring Boot service integrates relational persistence with search indexing while staying OAuth2-secured.

## Technology Focus
- Spring Boot 3.2 with Data JPA and Scheduler for CRUD operations plus recurring maintenance jobs.
- MySQL schema dedicated to catalog data, complemented by Elasticsearch documents to showcase full-text search sync strategies.
- Mail support and validation layers that highlight enterprise-ready features (notifications, Bean Validation, structured DTOs).
- OAuth2 Resource Server configuration connected to Keycloak so downstream services trust catalog endpoints via JWT scopes.
