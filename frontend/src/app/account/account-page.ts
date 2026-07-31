import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { LocaleService } from '../i18n/locale.service';

@Component({
  selector: 'app-account-page',
  imports: [RouterLink],
  template: `
    <section class="account view-enter page-shell">
      <p class="eyebrow">{{ i18n.t('nav.account') }}</p>
      <h1>{{ i18n.t('account.title') }}</h1>
      @if (auth.currentUser(); as user) {
        <dl>
          <div>
            <dt>{{ i18n.t('account.username') }}</dt>
            <dd>{{ user.username }}</dd>
          </div>
        </dl>
      } @else {
        <p class="muted">{{ i18n.t('account.notSignedIn') }}</p>
        <button type="button" class="quiet-btn quiet-btn--outline" (click)="auth.login('/account')">
          {{ i18n.t('nav.signIn') }}
        </button>
      }
      <a routerLink="/products" class="quiet-btn quiet-btn--outline back">{{
        i18n.t('account.back')
      }}</a>
    </section>
  `,
  styles: `
    .account {
      max-width: 36rem;
      padding-block: 3.5rem;
    }

    @media (min-width: 640px) {
      .account {
        padding-block: 5rem;
      }
    }

    .eyebrow {
      margin: 0 0 0.75rem;
      font-size: 0.75rem;
      letter-spacing: 0.12em;
      text-transform: uppercase;
      color: var(--muted);
    }

    h1 {
      margin: 0 0 2.5rem;
      font-family: var(--font-display);
      font-weight: 500;
      font-size: 2.5rem;
      letter-spacing: -0.02em;
    }

    dl {
      margin: 0 0 2.5rem;
      display: grid;
      gap: 1.5rem;
    }

    dl > div {
      border-bottom: 1px solid var(--border);
      padding-bottom: 1rem;
    }

    dt {
      margin: 0 0 0.35rem;
      font-size: 0.75rem;
      letter-spacing: 0.08em;
      text-transform: uppercase;
      color: var(--muted);
    }

    dd {
      margin: 0;
      font-size: 1.05rem;
    }

    .muted {
      color: var(--muted);
      margin-bottom: 1.5rem;
    }

    .back {
      display: inline-block;
      text-decoration: none;
      margin-top: 1rem;
    }
  `,
})
export class AccountPage {
  readonly auth = inject(AuthService);
  readonly i18n = inject(LocaleService);
}
