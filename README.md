# Bookshop (Spring + Angular)

Greenfield monorepo: Angular 21 storefront, Spring Boot resource server, and Spring Authorization Server.

## Layout

| Path | Role |
|------|------|
| `frontend/` | Angular 21 SPA (signals, PKCE + refresh, locale/FX) |
| `backend/` | Bookshop API (OAuth2 resource server, Flyway, translation tables) |
| `auth-server/` | Spring Authorization Server (issuer `http://localhost:9000`) |
| `payment-service/` | Mock hosted checkout (port `8091`; webhook finalizes orders) |
| `compose.dev.yml` | Local stack (H2, in-memory auth users) |
| `compose.staging.yml` | Staging-like stack (MariaDB for API + auth) |
| `docs/` | Environments, ADR, OpenAPI |

## Quick start (local JVM)

```bash
# Terminal 1 — auth
cd auth-server && mvn spring-boot:run

# Terminal 2 — mock payment
cd payment-service && mvn spring-boot:run

# Terminal 3 — API
cd backend && mvn spring-boot:run

# Terminal 4 — SPA
cd frontend && npm start
```

- SPA: http://localhost:4200  
- API: http://localhost:8090  
- Auth: http://localhost:9000  
- Payment: http://localhost:8091  

Demo logins (local/dev/staging seed only): `user` / `password`, `admin` / `password`.

Catalog GETs are public (`?lang=` for translated names). Checkout requires PKCE login and scope `bookshop.write`, plus an `Idempotency-Key` header. The API binds the order to the JWT `sub`, builds lines from `{productId, quantity}`, prices from catalog USD × fixed FX rates for `currencyCode`, upserts the customer by oauth subject, and creates a **PENDING** order without decrementing stock. The SPA redirects to the hosted payment page; after Pay, a signed webhook decrements stock and sets `PAID` (or `CANCELLED` on cancel / stock failure).

## Docker Compose

Do **not** run both stacks at once (shared host ports `4200` / `8090` / `8091` / `9000`). They are separate Compose projects with distinct image tags (`*:dev` / `*:staging`).

```bash
cp .env.example .env

# Dev — ng serve on :4200, H2, in-memory auth
docker compose -f compose.dev.yml up --build

# Staging — nginx SPA (:4200→80), MariaDB bookshop_db + bookshop_auth
docker compose -f compose.staging.yml up --build
```

On Windows with Podman, use `podman compose` the same way if `docker` is not available.

Staging frontend is nginx on container port **80** (`4200:80`). Dev frontend is `ng serve` on **4200** (`4200:4200`). An empty reply on `:4200` usually means the nginx image was started behind the dev port map.

### Runtime SPA config

Compose injects `/env.js` (`API_BASE_URL`, `AUTH_ISSUER_URI`, `OAUTH_*`) so the browser talks to host-published ports. Token `iss` stays `http://localhost:9000`; the backend fetches JWKS from `http://auth-server:9000/oauth2/jwks` inside the network.

### Product images

Catalog `image_url` values are relative (`assets/images/products/...`). Files live under `frontend/public/assets/images/` (copied from the luv2shop reference). The SPA prefixes relative paths with `/` so nginx/`ng serve` can resolve them.

## Auth notes

- JWT audience `bookshop-api` is set by the auth-server and validated by the backend (`spring.security.oauth2.resourceserver.jwt.audiences`).
- SPA stores access + refresh tokens and refreshes ~30s before expiry (no `silent-renew.html`; that redirect was removed).
- Auth-server persists the RSA JWK under `bookshop.auth.jwk-path` (default `./data/auth-jwk.json`) so restarts keep accepting issued tokens.
- Staging auth uses MariaDB database `bookshop_auth` (Flyway + JDBC users/clients). Dev uses in-memory beans (`@Profile("!staging")`).

## Docs

- [Environments](docs/ENVIRONMENTS.md) — dev vs staging, MariaDB, Flyway, JWK
- [ADR-001 Auth](docs/ADR-001-auth.md)
- [OpenAPI](docs/openapi.yaml) — path sketch; request schemas lag the live checkout DTO
