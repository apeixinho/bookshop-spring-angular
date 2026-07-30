import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from './auth.service';

@Component({
  selector: 'app-auth-callback',
  template: `
    <section class="callback view-enter page-shell">
      @if (error()) {
        <p class="error">{{ error() }}</p>
      } @else {
        <p>Signing you in…</p>
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
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    const code = this.route.snapshot.queryParamMap.get('code');
    const state = this.route.snapshot.queryParamMap.get('state');
    if (!code || !state) {
      this.error.set('Missing authorization code');
      return;
    }
    void this.auth.handleCallback(code, state).catch((err: unknown) => {
      this.error.set(err instanceof Error ? err.message : 'Login failed');
    });
  }
}
