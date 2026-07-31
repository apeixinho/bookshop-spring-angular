# Bookshop payment-service

Mock hosted checkout (Stripe-like redirect) for the Bookshop monorepo.

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
