# ADR-001: Spring Authorization Server + Resource Server

## Status

Accepted (access policy revised: public catalog, JWT checkout)

## Context

The luv2shop reference backend has no Spring Security. We need an in-repo identity provider and JWT protection for sensitive operations, while keeping browse/search/geo open for a typical e-commerce SPA (public catalog, authenticate at checkout).

## Decision

1. Run a **Spring Authorization Server** as `auth-server` (port 9000).
2. Configure the bookshop **backend** as an **OAuth2 Resource Server** validating JWTs via the issuer JWKS.
3. Register a public SPA client `bookshop-spa` using Authorization Code + PKCE.
4. Issue JWTs (RS256) with audience `bookshop-api` and scopes `openid`, `profile`, `bookshop.read`, `bookshop.write`.
5. **Access policy:**
   - **Permit anonymous:** catalog, product search/detail, categories, countries/states (geo), and `/actuator/health` (probes) for Docker healthchecks.
   - **Require Bearer JWT:** `POST /api/v1/checkout/purchase` only (scope `bookshop.write` where enforced).
6. Demo users are seeded for local/dev (e.g. `user` / `password`); staging uses JDBC-backed users in MariaDB `bookshop_auth`.
7. Access tokens include audience `bookshop-api`; the resource server validates `jwt.audiences`.
8. SPA renews via refresh_token (no silent-renew iframe / `silent-renew.html`).

## Consequences

- Shoppers can browse and search without logging in; checkout purchase requires PKCE login + Bearer token.
- Backend `SecurityFilterChain` must permit public API paths and authenticate checkout purchase.
- Backend and auth-server share a fixed issuer/client/audience contract so they can be developed in parallel.
- Compose networking uses service hostname `auth-server` for server-to-server JWKS; browsers use `localhost:9000`.
- Staging Compose mounts `deploy/mariadb/init` so `bookshop_auth` exists beside `bookshop_db`.

## References

- [Environments](ENVIRONMENTS.md)
- https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html
- https://www.baeldung.com/spring-security-oauth-resource-server
- https://docs.spring.io/spring-authorization-server/reference/
