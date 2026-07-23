declare global {
  interface BookshopRuntimeEnv {
    apiBaseUrl?: string;
    authIssuer?: string;
    oauthClientId?: string;
    oauthScopes?: string;
    oauthRedirectUri?: string;
  }

  interface Window {
    __BOOKSHOP_ENV__?: BookshopRuntimeEnv;
  }
}

export {};
