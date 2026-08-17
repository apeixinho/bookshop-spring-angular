# Catalog E-Shop backend (product catalog and checkout API)

[![Backend CI](https://github.com/apeixinho/catalog-eshop-demo/actions/workflows/backend.yml/badge.svg)](https://github.com/apeixinho/catalog-eshop-demo/actions/workflows/backend.yml)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F?logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)

Spring Boot 4.1 OAuth2 resource server for Catalog E-Shop products API and checkout API.

## Access policy

- Public GET: products, categories, countries, states, currency rates
- JWT + `SCOPE_catalog.write`: `POST /api/v1/checkout/purchase` (and other shopper checkout paths)
- Shared secret (no JWT): `POST /api/v1/checkout/payment-webhook`
- Validates issuer and audience (`OAUTH_AUDIENCE`, default `catalog-api`)

Full decision record: [OAuth2 access policy](../docs/oauth2-access-policy.md).

## Profiles

| Profile | DB | Flyway location |
|---------|----|-----------------|
| `dev` / `test` | H2 | `classpath:db/migration/h2` |
| `staging` | MariaDB `catalog_db` | `classpath:db/migration/mariadb` |

H2 and MariaDB migrations must stay in sync (e.g. V4 order tables, V6 payment session columns). Timestamp columns use `TIMESTAMP(6)` on H2 and `DATETIME(6)` on MariaDB — H2 2.4.x (shipped with Spring Boot 4) removed the non-standard `DATETIME` keyword. See [dev and staging environments](../docs/dev-and-staging-environments.md).

## Run

```bash
mvn spring-boot:run
mvn test
```

Compose sets `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI=http://auth-server:9000/oauth2/jwks` while `AUTH_ISSUER_URI` stays `http://localhost:9000`.
