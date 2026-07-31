#!/bin/sh
set -eu

cat > /usr/share/nginx/html/env.js <<EOF
window.__BOOKSHOP_ENV__ = {
  apiBaseUrl: "${API_BASE_URL:-http://localhost:8090}",
  authIssuer: "${AUTH_ISSUER_URI:-http://localhost:9000}",
  oauthClientId: "${OAUTH_CLIENT_ID:-bookshop-spa}",
  oauthScopes: "${OAUTH_SCOPES:-openid profile bookshop.read bookshop.write}",
  oauthRedirectUri: "${OAUTH_REDIRECT_URI:-http://localhost:4200/auth/callback}",
  oauthPostLogoutRedirectUri: "${OAUTH_POST_LOGOUT_REDIRECT_URI:-http://localhost:4200/products}"
};
EOF

echo "Wrote staging SPA runtime env to /usr/share/nginx/html/env.js"
exec nginx -g 'daemon off;'
