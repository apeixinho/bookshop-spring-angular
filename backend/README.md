# Bookshop backend (catalog and checkout API)

Spring Boot 3.4 OAuth2 resource server for the Bookshop catalog and checkout API.

## Access policy

- Public GET: products, categories, countries, states, currency rates
- JWT + `SCOPE_bookshop.write`: `POST /api/v1/checkout/purchase` (and other shopper checkout paths)
- Shared secret (no JWT): `POST /api/v1/checkout/payment-webhook`
- Validates issuer and audience (`OAUTH_AUDIENCE`, default `bookshop-api`)

Full decision record: [OAuth2 access policy](../docs/oauth2-access-policy.md).

## Profiles

| Profile | DB | Flyway location |
|---------|----|-----------------|
| `dev` / `test` | H2 | `classpath:db/migration/h2` |
| `staging` | MariaDB `bookshop_db` | `classpath:db/migration/mariadb` |

H2 and MariaDB migrations must stay in sync (e.g. V4 order tables, V6 payment session columns). See [dev and staging environments](../docs/dev-and-staging-environments.md).

## Run

```bash
mvn spring-boot:run
mvn test
```

Compose sets `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI=http://auth-server:9000/oauth2/jwks` while `AUTH_ISSUER_URI` stays `http://localhost:9000`.
