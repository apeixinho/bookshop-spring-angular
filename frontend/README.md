# Bookshop frontend

Angular 21 storefront for the Bookshop monorepo.

## Run locally

```bash
npm install --legacy-peer-deps
npm start
```

Open http://localhost:4200. Defaults talk to API `http://localhost:8090` and auth `http://localhost:9000`. Override at runtime via `/env.js` (`window.__BOOKSHOP_ENV__`) — used by Compose entrypoints.

## Auth

- Authorization Code + PKCE against `bookshop-spa`
- Access + refresh tokens in `localStorage`; refresh ~30s before expiry
- Checkout route is guarded; catalog is anonymous

## Assets

Product images under `public/assets/images/products/` (paths match DB `image_url`). Relative URLs are served from the SPA origin (leading `/` added in the products page).

## Docker

- Dev: `Dockerfile.dev` + `docker-entrypoint.dev.sh` (`ng serve`)
- Staging: `Dockerfile.staging` + nginx + `docker-entrypoint.staging.sh`
