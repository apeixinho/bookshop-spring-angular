#!/bin/sh
set -eu

cat > /app/public/env.js <<EOF
window.__BOOKSHOP_ENV__ = {
  apiBaseUrl: "${API_BASE_URL:-http://localhost:8090}",
  authIssuer: "${AUTH_ISSUER_URI:-http://localhost:9000}",
  oauthClientId: "${OAUTH_CLIENT_ID:-bookshop-spa}",
  oauthScopes: "${OAUTH_SCOPES:-openid profile bookshop.read bookshop.write}",
  oauthRedirectUri: "${OAUTH_REDIRECT_URI:-http://localhost:4200/auth/callback}",
  oauthPostLogoutRedirectUri: "${OAUTH_POST_LOGOUT_REDIRECT_URI:-http://localhost:4200/products}"
};
EOF

echo "Bookshop SPA runtime env written to /app/public/env.js"
exec npm run start -- --host 0.0.0.0 --port 4200
