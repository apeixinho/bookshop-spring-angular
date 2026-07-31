import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CartService } from '../cart/cart.service';
import { LocaleService } from '../i18n/locale.service';

@Component({
  selector: 'app-checkout-result-page',
  imports: [RouterLink],
  template: `
    <section class="result view-enter page-shell">
      @if (status() === 'success') {
        <p class="eyebrow">{{ i18n.t('checkout.confirmed') }}</p>
        <h1>{{ i18n.t('checkout.paidTitle') }}</h1>
        <p class="lead">{{ i18n.t('checkout.paidBody') }}</p>
        @if (tracking()) {
          <div class="tracking">
            <p class="eyebrow">{{ i18n.t('checkout.tracking') }}</p>
            <p class="tracking-value">{{ tracking() }}</p>
          </div>
        }
        <a routerLink="/products" class="quiet-btn quiet-btn--outline">{{
          i18n.t('checkout.continue')
        }}</a>
      } @else if (status() === 'cancelled') {
        <h1>{{ i18n.t('checkout.paymentCancelledTitle') }}</h1>
        <p class="lead">{{ i18n.t('checkout.paymentCancelledBody') }}</p>
        <a routerLink="/checkout" class="quiet-btn quiet-btn--outline">{{
          i18n.t('checkout.tryAgain')
        }}</a>
      } @else {
        <h1>{{ i18n.t('checkout.paymentFailedTitle') }}</h1>
        <p class="lead">{{ i18n.t('checkout.paymentFailedBody') }}</p>
        <a routerLink="/checkout" class="quiet-btn quiet-btn--outline">{{
          i18n.t('checkout.tryAgain')
        }}</a>
      }
    </section>
  `,
  styles: `
    .result {
      max-width: 42rem;
      padding-block: 5rem;
      text-align: center;
      margin-inline: auto;
    }

    h1 {
      margin: 0 0 1rem;
      font-family: var(--font-display);
      font-weight: 500;
      font-size: 2.25rem;
    }

    .eyebrow {
      margin: 0 0 1rem;
      font-size: 0.75rem;
      letter-spacing: 0.12em;
      text-transform: uppercase;
      color: var(--muted);
    }

    .lead {
      margin: 0 auto 2rem;
      max-width: 28rem;
      color: var(--muted);
      line-height: 1.6;
    }

    .tracking {
      display: inline-block;
      border: 1px solid var(--border);
      padding: 1.25rem 2rem;
      margin-bottom: 2.5rem;
    }

    .tracking-value {
      margin: 0;
      font-family: var(--font-mono);
      font-size: 1.125rem;
      letter-spacing: 0.06em;
    }

    a {
      display: inline-block;
      text-decoration: none;
    }
  `,
})
export class CheckoutResultPage implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly cart = inject(CartService);
  readonly i18n = inject(LocaleService);

  readonly status = signal<'success' | 'cancelled' | 'failed'>('failed');
  readonly tracking = signal<string | null>(null);

  ngOnInit(): void {
    const params = this.route.snapshot.queryParamMap;
    const raw = (params.get('status') ?? 'failed').toLowerCase();
    const status = raw === 'success' ? 'success' : raw === 'cancelled' ? 'cancelled' : 'failed';
    this.status.set(status);

    const tracking =
      params.get('tracking') ?? sessionStorage.getItem('bookshop.pending.tracking');
    this.tracking.set(tracking);
    sessionStorage.removeItem('bookshop.pending.tracking');

    if (status === 'success') {
      this.cart.clearCart();
    }
  }
}
