declare global {
  interface CatalogRuntimeEnv {
    apiBaseUrl?: string;
    authIssuer?: string;
    oauthClientId?: string;
    oauthScopes?: string;
    oauthRedirectUri?: string;
    oauthPostLogoutRedirectUri?: string;
  }

  interface Window {
    __CATALOG_ENV__?: CatalogRuntimeEnv;
  }
}

export {};
