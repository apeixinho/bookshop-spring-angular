/**
 * Default environment for local `ng serve` outside Docker.
 * Compose/runtime overrides come from /env.js → window.__BOOKSHOP_ENV__.
 */
const runtime: BookshopRuntimeEnv =
  (typeof window !== 'undefined' && window.__BOOKSHOP_ENV__) || {};

export const environment = {
  production: false,
  apiBaseUrl: runtime.apiBaseUrl || 'http://localhost:8090',
  authIssuer: runtime.authIssuer || 'http://localhost:9000',
  oauthClientId: runtime.oauthClientId || 'bookshop-spa',
  oauthScopes: runtime.oauthScopes || 'openid profile bookshop.read bookshop.write',
  oauthRedirectUri: runtime.oauthRedirectUri || 'http://localhost:4200/auth/callback',
  oauthPostLogoutRedirectUri:
    runtime.oauthPostLogoutRedirectUri || 'http://localhost:4200/products',
};
