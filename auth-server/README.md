# Bookshop auth-server (OAuth2 Authorization Server)

Spring Authorization Server (Boot 3.4) issuing JWTs for the Bookshop SPA and API.

## Profiles

| Profile | Users / clients | Database |
|---------|-----------------|----------|
| default / `dev` | In-memory (`DevAuthDataConfig`) | None (Flyway off) |
| `staging` | JDBC + Flyway (`StagingAuthDataConfig`) | MariaDB `bookshop_auth` |

Demo accounts (seed only): `user` / `password`, `admin` / `password`.

## Contract

- Issuer: `AUTH_ISSUER_URI` (default `http://localhost:9000`)
- Client: `bookshop-spa` (public, PKCE, refresh_token); redirect/post-logout URIs for each comma-separated `FRONTEND_ORIGIN`
- Audience claim: `bookshop-api`
- Scopes: `openid`, `profile`, `bookshop.read`, `bookshop.write`
- Signing key: RSA JWK at `bookshop.auth.jwk-path` / `AUTH_JWK_PATH` (generated once if missing)

## Run

```bash
mvn spring-boot:run
# staging locally needs MariaDB + SPRING_PROFILES_ACTIVE=staging
```

Compose: see root `compose.dev.yml` / `compose.staging.yml` and [dev and staging environments](../docs/dev-and-staging-environments.md). Access policy: [OAuth2 access policy](../docs/oauth2-access-policy.md).
