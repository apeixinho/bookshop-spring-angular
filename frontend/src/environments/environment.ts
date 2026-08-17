/**
 * Default environment for local `ng serve` outside Docker.
 * Compose/runtime overrides come from /env.js → window.__CATALOG_ENV__.
 */
const runtime: CatalogRuntimeEnv =
  (typeof window !== 'undefined' && window.__CATALOG_ENV__) || {};

export const environment = {
  production: false,
  apiBaseUrl: runtime.apiBaseUrl || 'http://localhost:8090',
  authIssuer: runtime.authIssuer || 'http://localhost:9000',
  oauthClientId: runtime.oauthClientId || 'catalog-spa',
  oauthScopes: runtime.oauthScopes || 'openid profile catalog.read catalog.write',
  oauthRedirectUri: runtime.oauthRedirectUri || 'http://localhost:4200/auth/callback',
  oauthPostLogoutRedirectUri:
    runtime.oauthPostLogoutRedirectUri || 'http://localhost:4200/products',
};
