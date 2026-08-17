export const environment = {
  production: true,
  apiBaseUrl: '/api-proxy',
  authIssuer: 'http://localhost:9000',
  oauthClientId: 'catalog-spa',
  oauthScopes: 'openid profile catalog.read catalog.write',
  oauthRedirectUri: 'http://localhost:4200/auth/callback',
  oauthPostLogoutRedirectUri: 'http://localhost:4200/products',
};
