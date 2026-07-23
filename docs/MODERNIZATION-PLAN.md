# Bookshop Modernization Plan

Living plan for the greenfield monorepo in `bookshop-spring-angular`.

## Locked decisions

- **Deliverable repo:** this monorepo only; `angular-springboot-luv2shop` and `modern-angular-project` are read-only references.
- **Auth:** In-repo Spring Authorization Server + Bookshop API as OAuth2 Resource Server (JWT).
- **Access policy:** Public catalog/search/geo; **JWT + `bookshop.write` required only for checkout** (`POST /api/v1/checkout/**`). Actuator health remains public for Compose.
- **Domain:** Keep luv2shop entity model (Product, ProductCategory, Customer, Order, OrderItem, Address, Country, State).
- **Parallelism:** Backend and Auth developed against the shared auth contract below.

## Shared auth contract

| Setting | Value |
|---------|--------|
| Issuer | `http://localhost:9000` (token `iss` claim; Compose JWKS via `auth-server:9000`) |
| SPA client_id | `bookshop-spa` (public, PKCE) |
| Audience | `bookshop-api` |
| Scopes | `openid`, `profile`, `bookshop.read`, `bookshop.write` |

## Success criteria

1. Compose (or local JVM) starts auth-server, backend, frontend.
2. Catalog GETs work without JWT; checkout without JWT returns 401; checkout with write-scoped JWT succeeds past security.
3. SPA browses anonymously; PKCE login at checkout; Bearer token on purchase.
4. Domain matches luv2shop ER model.
5. FE uses standalone + signals + Vitest/Material patterns.
6. CI builds/tests frontend, backend, and auth-server.
7. Backend validates JWT `aud`; SPA refreshes access tokens; staging uses MariaDB for API + auth.

## Known intentional differences

- **H2 vs MariaDB Flyway V4:** H2 migrations omit some unique keys/indexes present on MariaDB. Expected for local/dev; see [ENVIRONMENTS.md](ENVIRONMENTS.md).
- **Demo passwords:** `user`/`password`, `admin`/`password` are intentional for local and staging demo seeds only.

## Out of scope

External IdPs, Stripe, admin UI, RBAC beyond scopes.
