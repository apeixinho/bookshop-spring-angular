import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../environments/environment';
import { LocaleService } from '../i18n/locale.service';

interface TokenResponse {
  access_token: string;
  id_token?: string;
  refresh_token?: string;
  expires_in: number;
  token_type: string;
  scope?: string;
}

export interface AuthUser {
  username: string;
  subject: string;
}

const PKCE_VERIFIER_KEY = 'bookshop.pkce.verifier';
const OAUTH_STATE_KEY = 'bookshop.oauth.state';
const ACCESS_TOKEN_KEY = 'bookshop.access_token';
const ID_TOKEN_KEY = 'bookshop.id_token';
const REFRESH_TOKEN_KEY = 'bookshop.refresh_token';
const EXPIRES_AT_KEY = 'bookshop.expires_at';
const FLASH_KEY = 'bookshop.flash';
/** Refresh this many ms before access-token expiry */
const EXPIRY_SKEW_MS = 30_000;

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly i18n = inject(LocaleService);

  private readonly accessToken = signal<string | null>(this.readStoredToken());
  private readonly idToken = signal<string | null>(localStorage.getItem(ID_TOKEN_KEY));
  private refreshInFlight: Promise<string | null> | null = null;
  /** Bumped on logout so in-flight refresh cannot restore the session. */
  private sessionEpoch = 0;

  readonly isAuthenticated = computed(() => !!this.accessToken() && !this.isExpired());

  readonly currentUser = computed<AuthUser | null>(() => {
    if (!this.isAuthenticated()) {
      return null;
    }
    const claims = this.decodeClaims(this.idToken()) ?? this.decodeClaims(this.accessToken());
    if (!claims) {
      return null;
    }
    const username =
      (typeof claims['preferred_username'] === 'string' && claims['preferred_username']) ||
      (typeof claims['name'] === 'string' && claims['name']) ||
      (typeof claims['sub'] === 'string' && claims['sub']) ||
      'Account';
    const subject = typeof claims['sub'] === 'string' ? claims['sub'] : username;
    return { username, subject };
  });

  getAccessToken(): string | null {
    if (this.isExpired()) {
      return null;
    }
    return this.accessToken();
  }

  /** Returns a valid access token, refreshing when close to expiry or already expired. */
  async ensureValidAccessToken(): Promise<string | null> {
    if (this.accessToken() && !this.needsRefresh()) {
      return this.accessToken();
    }
    const refresh = localStorage.getItem(REFRESH_TOKEN_KEY);
    if (!refresh) {
      if (this.isExpired()) {
        this.clearSession();
      }
      return this.accessToken() && !this.isExpired() ? this.accessToken() : null;
    }
    return this.refreshAccessToken(refresh);
  }

  async login(returnUrl = '/checkout'): Promise<void> {
    const verifier = this.createVerifier();
    const challenge = await this.createChallenge(verifier);
    const state = crypto.randomUUID();
    sessionStorage.setItem(PKCE_VERIFIER_KEY, verifier);
    sessionStorage.setItem(OAUTH_STATE_KEY, JSON.stringify({ state, returnUrl }));

    const params = new HttpParams()
      .set('response_type', 'code')
      .set('client_id', environment.oauthClientId)
      .set('redirect_uri', environment.oauthRedirectUri)
      .set('scope', environment.oauthScopes)
      .set('state', state)
      .set('code_challenge', challenge)
      .set('code_challenge_method', 'S256')
      .set('ui_locales', this.i18n.language());

    window.location.href = `${environment.authIssuer}/oauth2/authorize?${params.toString()}`;
  }

  async handleCallback(code: string, state: string): Promise<void> {
    const stored = sessionStorage.getItem(OAUTH_STATE_KEY);
    const verifier = sessionStorage.getItem(PKCE_VERIFIER_KEY);
    if (!stored || !verifier) {
      throw new Error('Missing PKCE session');
    }
    const parsed = JSON.parse(stored) as { state: string; returnUrl: string };
    if (parsed.state !== state) {
      throw new Error('Invalid OAuth state');
    }

    const body = new HttpParams()
      .set('grant_type', 'authorization_code')
      .set('code', code)
      .set('redirect_uri', environment.oauthRedirectUri)
      .set('client_id', environment.oauthClientId)
      .set('code_verifier', verifier);

    const token = await firstValueFrom(
      this.http.post<TokenResponse>(`${environment.authIssuer}/oauth2/token`, body.toString(), {
        headers: new HttpHeaders({ 'Content-Type': 'application/x-www-form-urlencoded' }),
      }),
    );

    sessionStorage.removeItem(PKCE_VERIFIER_KEY);
    sessionStorage.removeItem(OAUTH_STATE_KEY);
    this.persistTokens(token);
    await this.router.navigateByUrl(parsed.returnUrl || '/checkout');
  }

  /**
   * Clears SPA tokens and ends the auth-server session (OIDC RP-initiated logout).
   * Without the IdP logout, Sign in would silently re-authenticate via the SSO cookie.
   */
  logout(): void {
    this.sessionEpoch++;
    this.refreshInFlight = null;
    const idToken = localStorage.getItem(ID_TOKEN_KEY) ?? this.idToken();
    this.clearSession();
    sessionStorage.setItem(FLASH_KEY, 'toast.signedOut');

    const postLogout =
      environment.oauthPostLogoutRedirectUri ||
      `${window.location.origin}/products`;

    let params = new HttpParams()
      .set('client_id', environment.oauthClientId)
      .set('post_logout_redirect_uri', postLogout);
    if (idToken) {
      params = params.set('id_token_hint', idToken);
    }

    window.location.href = `${environment.authIssuer}/connect/logout?${params.toString()}`;
  }

  private async refreshAccessToken(refreshToken: string): Promise<string | null> {
    if (this.refreshInFlight) {
      return this.refreshInFlight;
    }
    const epoch = this.sessionEpoch;
    this.refreshInFlight = (async () => {
      try {
        const body = new HttpParams()
          .set('grant_type', 'refresh_token')
          .set('refresh_token', refreshToken)
          .set('client_id', environment.oauthClientId);

        const token = await firstValueFrom(
          this.http.post<TokenResponse>(`${environment.authIssuer}/oauth2/token`, body.toString(), {
            headers: new HttpHeaders({ 'Content-Type': 'application/x-www-form-urlencoded' }),
          }),
        );
        if (epoch !== this.sessionEpoch) {
          return null;
        }
        this.persistTokens(token);
        return this.accessToken();
      } catch {
        if (epoch === this.sessionEpoch) {
          this.clearSession();
        }
        return null;
      } finally {
        this.refreshInFlight = null;
      }
    })();
    return this.refreshInFlight;
  }

  private persistTokens(token: TokenResponse): void {
    localStorage.setItem(ACCESS_TOKEN_KEY, token.access_token);
    if (token.id_token) {
      localStorage.setItem(ID_TOKEN_KEY, token.id_token);
      this.idToken.set(token.id_token);
    }
    if (token.refresh_token) {
      localStorage.setItem(REFRESH_TOKEN_KEY, token.refresh_token);
    }
    // Store true expiry; skew is applied only in needsRefresh / ensureValidAccessToken.
    const expiresAt = Date.now() + Math.max(token.expires_in, 60) * 1000;
    localStorage.setItem(EXPIRES_AT_KEY, String(expiresAt));
    this.accessToken.set(token.access_token);
  }

  private clearSession(): void {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(ID_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(EXPIRES_AT_KEY);
    this.accessToken.set(null);
    this.idToken.set(null);
  }

  private isExpired(): boolean {
    const raw = localStorage.getItem(EXPIRES_AT_KEY);
    if (!raw) {
      // No expiry metadata (legacy) — require a fresh login when a token is present
      return this.accessToken() != null;
    }
    return Date.now() >= Number(raw);
  }

  private needsRefresh(): boolean {
    const raw = localStorage.getItem(EXPIRES_AT_KEY);
    if (!raw) {
      return this.accessToken() != null;
    }
    return Date.now() >= Number(raw) - EXPIRY_SKEW_MS;
  }

  private readStoredToken(): string | null {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
  }

  private decodeClaims(token: string | null): Record<string, unknown> | null {
    if (!token) {
      return null;
    }
    try {
      const payload = token.split('.')[1];
      if (!payload) {
        return null;
      }
      const json = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
      return JSON.parse(json) as Record<string, unknown>;
    } catch {
      return null;
    }
  }

  private createVerifier(): string {
    const array = new Uint8Array(32);
    crypto.getRandomValues(array);
    return this.base64Url(array);
  }

  private async createChallenge(verifier: string): Promise<string> {
    const data = new TextEncoder().encode(verifier);
    const digest = await crypto.subtle.digest('SHA-256', data);
    return this.base64Url(new Uint8Array(digest));
  }

  private base64Url(bytes: Uint8Array): string {
    let binary = '';
    bytes.forEach((b) => (binary += String.fromCharCode(b)));
    return btoa(binary).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
  }
}
