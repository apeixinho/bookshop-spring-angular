export const environment = {
  production: true,
  apiBaseUrl: '/api-proxy',
  authIssuer: 'http://localhost:9000',
  oauthClientId: 'bookshop-spa',
  oauthScopes: 'openid profile bookshop.read bookshop.write',
  oauthRedirectUri: 'http://localhost:4200/auth/callback',
};
