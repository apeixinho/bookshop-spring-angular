#!/bin/sh
set -eu

cat > /app/public/env.js <<EOF
window.__CATALOG_ENV__ = {
  apiBaseUrl: "${API_BASE_URL:-http://localhost:8090}",
  authIssuer: "${AUTH_ISSUER_URI:-http://localhost:9000}",
  oauthClientId: "${OAUTH_CLIENT_ID:-catalog-spa}",
  oauthScopes: "${OAUTH_SCOPES:-openid profile catalog.read catalog.write}",
  oauthRedirectUri: "${OAUTH_REDIRECT_URI:-http://localhost:4200/auth/callback}",
  oauthPostLogoutRedirectUri: "${OAUTH_POST_LOGOUT_REDIRECT_URI:-http://localhost:4200/products}"
};
EOF

echo "Catalog SPA runtime env written to /app/public/env.js"
exec npm run start -- --host 0.0.0.0 --port 4200
