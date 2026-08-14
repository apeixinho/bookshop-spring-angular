# Bookshop payment-service (mock hosted checkout)

Mock hosted checkout (Stripe-like redirect) for the Bookshop monorepo. Not an IdP: the browser opens a session URL; bookshop APIs still use JWT for purchase.

## Flow

1. Backend creates a PENDING order, then `POST /api/v1/sessions` (shared secret).
2. SPA redirects to `checkoutUrl` (`GET /checkout/{sessionId}`).
3. Pay / Cancel posts to the payment-service, which calls bookshop `POST /api/v1/checkout/payment-webhook` with `X-Payment-Secret`, then redirects to the SPA `/checkout/result`.

## Run

```bash
mvn spring-boot:run
```

- Port: `8091`
- Create session: `POST /api/v1/sessions` with header `X-Payment-Secret`
- Hosted page: `GET /checkout/{sessionId}`

## Config

| Property | Env | Default |
|----------|-----|---------|
| API / webhook secret | `PAYMENT_API_SECRET` / `PAYMENT_WEBHOOK_SECRET` | `dev-payment-secret` |
| Public base URL | `PAYMENT_PUBLIC_BASE_URL` | `http://localhost:8091` |
| Bookshop webhook | `PAYMENT_WEBHOOK_URL` | `http://localhost:8090/api/v1/checkout/payment-webhook` |

In Compose, set `PAYMENT_WEBHOOK_URL=http://backend:8090/api/v1/checkout/payment-webhook` and keep the public base URL as `http://localhost:8091` so browsers can open the hosted page.

Related: [OAuth2 access policy](../docs/oauth2-access-policy.md), [dev and staging environments](../docs/dev-and-staging-environments.md).
