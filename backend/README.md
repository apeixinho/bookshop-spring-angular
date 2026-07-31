# Bookshop backend

Spring Boot 3.4 OAuth2 resource server for the Bookshop catalog and checkout API.

## Access policy

- Public GET: products, categories, countries, states
- JWT + `SCOPE_bookshop.write`: `POST /api/v1/checkout/**`
- Validates issuer and audience (`OAUTH_AUDIENCE`, default `bookshop-api`)

## Profiles

| Profile | DB | Flyway location |
|---------|----|-----------------|
| `dev` / `test` | H2 | `classpath:db/migration/h2` |
| `staging` | MariaDB `bookshop_db` | `classpath:db/migration/mariadb` |

H2 and MariaDB V4 share unique keys and FKs; MariaDB adds a few secondary indexes. See [docs/ENVIRONMENTS.md](../docs/ENVIRONMENTS.md).

## Run

```bash
mvn spring-boot:run
mvn test
```

Compose sets `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI=http://auth-server:9000/oauth2/jwks` while `AUTH_ISSUER_URI` stays `http://localhost:9000`.
