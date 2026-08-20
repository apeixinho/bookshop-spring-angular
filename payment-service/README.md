# Catalog payment-service (mock hosted checkout)

[![Payment Service CI](https://github.com/apeixinho/catalog-eshop-demo/actions/workflows/payment-service.yml/badge.svg)](https://github.com/apeixinho/catalog-eshop-demo/actions/workflows/payment-service.yml)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F?logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)

Mock hosted checkout (Stripe-like redirect) for the Catalog monorepo. Not an IdP: the browser opens a session URL; catalog APIs still use JWT for purchase.

Spring Boot 4.1 (Spring Framework 7): modular auto-config pulls in `spring-boot-restclient` for `RestClient.Builder` and `spring-boot-starter-webmvc-test` for MockMvc tests.

## Flow

1. Backend creates a PENDING order, then `POST /api/v1/sessions` (shared secret).
2. SPA redirects to `checkoutUrl` (`GET /checkout/{sessionId}`).
3. Pay / Cancel posts to the payment-service, which calls catalog `POST /api/v1/checkout/payment-webhook` with `X-Payment-Secret`, then redirects to the SPA `/checkout/result`.

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
| Catalog webhook | `PAYMENT_WEBHOOK_URL` | `http://localhost:8090/api/v1/checkout/payment-webhook` |

In Compose, set `PAYMENT_WEBHOOK_URL=http://backend:8090/api/v1/checkout/payment-webhook` and keep the public base URL as `http://localhost:8091` so browsers can open the hosted page.

Related: [OAuth2 access policy](../docs/oauth2-access-policy.md), [dev and staging environments](../docs/dev-and-staging-environments.md).
