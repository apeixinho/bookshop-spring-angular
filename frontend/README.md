# Bookshop frontend (Angular storefront)

Angular 21 storefront for the Bookshop monorepo.

## Run locally

```bash
npm install --legacy-peer-deps
npm start
```

Open http://localhost:4200. Defaults talk to API `http://localhost:8090` and auth `http://localhost:9000`. Override at runtime via `/env.js` (`window.__BOOKSHOP_ENV__`) — used by Compose entrypoints.

## Auth

- Authorization Code + PKCE against `bookshop-spa`
- Access token in memory; refresh/id tokens in `sessionStorage`; refresh ~30s before expiry
- Checkout routes are guarded; catalog is anonymous
- Guest cart lines (`productId` + `quantity`) persist in `localStorage` across login redirects; cleared after successful payment
- After purchase, the SPA redirects to the mock payment page, then `/checkout/result`

See [OAuth2 access policy](../docs/oauth2-access-policy.md).

## Assets

Product images under `public/assets/images/products/` (paths match DB `image_url`). Relative URLs are served from the SPA origin (leading `/` added in the products page).

## Docker

- Dev: `Dockerfile.dev` + `docker-entrypoint.dev.sh` (`ng serve`)
- Staging: `Dockerfile.staging` + nginx + `docker-entrypoint.staging.sh`
