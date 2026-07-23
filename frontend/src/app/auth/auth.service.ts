import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../environments/environment';

interface TokenResponse {
  access_token: string;
  id_token?: string;
  refresh_token?: string;
  expires_in: number;
  token_type: string;
  scope?: string;
}

const PKCE_VERIFIER_KEY = 'bookshop.pkce.verifier';
const OAUTH_STATE_KEY = 'bookshop.oauth.state';
const ACCESS_TOKEN_KEY = 'bookshop.access_token';
const REFRESH_TOKEN_KEY = 'bookshop.refresh_token';
const EXPIRES_AT_KEY = 'bookshop.expires_at';
/** Refresh this many ms before access-token expiry */
const EXPIRY_SKEW_MS = 30_000;

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  private readonly accessToken = signal<string | null>(this.readStoredToken());
  private refreshInFlight: Promise<string | null> | null = null;

  readonly isAuthenticated = computed(() => !!this.accessToken() && !this.isExpired());

  getAccessToken(): string | null {
    if (this.isExpired()) {
      return null;
    }
    return this.accessToken();
  }

  /** Returns a valid access token, refreshing when close to expiry. */
  async ensureValidAccessToken(): Promise<string | null> {
    if (this.accessToken() && !this.isExpired()) {
      return this.accessToken();
    }
    const refresh = localStorage.getItem(REFRESH_TOKEN_KEY);
    if (!refresh) {
      this.clearSession();
      return null;
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
      .set('code_challenge_method', 'S256');

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

  logout(): void {
    this.clearSession();
    void this.router.navigateByUrl('/products');
  }

  private async refreshAccessToken(refreshToken: string): Promise<string | null> {
    if (this.refreshInFlight) {
      return this.refreshInFlight;
    }
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
        this.persistTokens(token);
        return this.accessToken();
      } catch {
        this.clearSession();
        return null;
      } finally {
        this.refreshInFlight = null;
      }
    })();
    return this.refreshInFlight;
  }

  private persistTokens(token: TokenResponse): void {
    localStorage.setItem(ACCESS_TOKEN_KEY, token.access_token);
    if (token.refresh_token) {
      localStorage.setItem(REFRESH_TOKEN_KEY, token.refresh_token);
    }
    const expiresAt = Date.now() + Math.max(token.expires_in, 60) * 1000 - EXPIRY_SKEW_MS;
    localStorage.setItem(EXPIRES_AT_KEY, String(expiresAt));
    this.accessToken.set(token.access_token);
  }

  private clearSession(): void {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(EXPIRES_AT_KEY);
    this.accessToken.set(null);
  }

  private isExpired(): boolean {
    const raw = localStorage.getItem(EXPIRES_AT_KEY);
    if (!raw) {
      // No expiry metadata (legacy) — require a fresh login when a token is present
      return this.accessToken() != null;
    }
    return Date.now() >= Number(raw);
  }

  private readStoredToken(): string | null {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
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
