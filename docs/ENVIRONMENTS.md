# Environments

## Dev (`compose.dev.yml` / local JVM)

| Service | Storage | Notes |
|---------|---------|--------|
| auth-server | In-memory users + clients | Profile `!staging` (`DevAuthDataConfig`); JWK file at `bookshop.auth.jwk-path` (default `./data/auth-jwk.json`) |
| backend | H2 (`classpath:db/migration/h2`) | Profile `dev` |
| frontend | `ng serve` in container | Writes `/env.js` at start |

Flyway on the auth-server is **disabled** by default (`spring.flyway.enabled: false`) so the default/dev profile does not need MariaDB.

## Do not run both Compose files together

`compose.dev.yml` and `compose.staging.yml` both publish host ports `4200`, `8090`, and `9000`. They are separate Compose projects (`bookshop-dev` / `bookshop-staging`) with distinct image tags, but only one stack should be up at a time.

**Port mismatch tip:** staging frontend is nginx on container port **80** (`4200:80`). Dev frontend is `ng serve` on container port **4200** (`4200:4200`). If `:4200` returns an empty reply while the frontend container looks “up”, the wrong image is probably running (nginx behind a `4200:4200` map).

## Staging (`compose.staging.yml`)

| Service | Storage | Notes |
|---------|---------|--------|
| MariaDB | `bookshop_db` + `bookshop_auth` | Init script `deploy/mariadb/init/01-create-auth-db.sql` |
| auth-server | JDBC + Flyway on `bookshop_auth` | Profile `staging` (`StagingAuthDataConfig`) |
| backend | Flyway MariaDB migrations | Profile `staging` |
| frontend | nginx (`Dockerfile.staging`) | Port `4200→80`, runtime `/env.js` |

Required env (see `.env.example`):

- `MARIADB_*` — shared credentials for both databases  
- `AUTH_MARIADB_DATABASE=bookshop_auth` — auth-server datasource  
- `AUTH_ISSUER_URI=http://localhost:9000` — JWT `iss` (browser + resource server)  
- `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI=http://auth-server:9000/oauth2/jwks` — set in Compose for the API  

Fresh MariaDB volumes are required for the init script to create `bookshop_auth`. If you change init SQL after first boot, remove the `mariadb_data_staging` volume and recreate.

```bash
docker compose -f compose.staging.yml down -v
docker compose -f compose.staging.yml up --build
```

Persist the auth-server JWK across container recreation (e.g. bind-mount `AUTH_JWK_PATH` / `./data`) or issued tokens become invalid after restart.

## H2 vs MariaDB Flyway V4

Backend keeps **separate** migration trees:

- `backend/src/main/resources/db/migration/h2/`
- `backend/src/main/resources/db/migration/mariadb/`

`V4__create-order-tables.sql` aligns unique keys (`customer.email`, billing/shipping address IDs) and FKs on both dialects. MariaDB also declares a few explicit secondary indexes that H2 omits. Prefer `compose.staging.yml` when validating MariaDB-specific index behavior.

## JWT audience

Auth-server customizer adds `aud: bookshop-api`. Backend validates via:

```yaml
spring.security.oauth2.resourceserver.jwt.audiences: ${OAUTH_AUDIENCE:bookshop-api}
```

Compose passes `OAUTH_AUDIENCE` to both services.

## SPA tokens

`AuthService` persists access token, refresh token, and true expiry in `localStorage`; skew applies only when deciding to refresh. `authGuard` / `ensureValidAccessToken()` renew via the refresh_token grant (no iframe silent-renew page).
