import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from './auth.service';

@Component({
  selector: 'app-auth-callback',
  template: `
    <section>
      @if (error()) {
        <p>{{ error() }}</p>
      } @else {
        <p>Signing you in…</p>
      }
    </section>
  `,
  styles: `
    section {
      padding: 2rem;
      text-align: center;
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
