import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from './auth.service';
import { LocaleService } from '../i18n/locale.service';

@Component({
  selector: 'app-auth-callback',
  template: `
    <section class="callback view-enter page-shell">
      @if (error()) {
        <p class="error">{{ error() }}</p>
      } @else {
        <p>{{ i18n.t('auth.signingIn') }}</p>
      }
    </section>
  `,
  styles: `
    .callback {
      padding-block: 6rem;
      text-align: center;
      color: var(--muted);
    }

    .error {
      color: #a12828;
    }
  `,
})
export class AuthCallbackPage implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly auth = inject(AuthService);
  readonly i18n = inject(LocaleService);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    const oauthError = this.route.snapshot.queryParamMap.get('error');
    if (oauthError) {
      const desc = this.route.snapshot.queryParamMap.get('error_description');
      const key = `auth.error.${oauthError}`;
      const translated = this.i18n.t(key);
      this.error.set(translated !== key ? translated : this.i18n.t('auth.error.generic'));
      if (desc) {
        // Prefer i18n message; description is only a fallback detail for unknown errors.
        if (translated === key) {
          this.error.set(`${this.i18n.t('auth.error.generic')} (${desc})`);
        }
      }
      return;
    }

    const code = this.route.snapshot.queryParamMap.get('code');
    const state = this.route.snapshot.queryParamMap.get('state');
    if (!code || !state) {
      this.error.set(this.i18n.t('auth.error.missingCode'));
      return;
    }
    void this.auth.handleCallback(code, state).catch(() => {
      this.error.set(this.i18n.t('auth.error.loginFailed'));
    });
  }
}
