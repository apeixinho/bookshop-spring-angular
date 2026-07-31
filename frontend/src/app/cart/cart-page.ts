import { Component, inject } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CartService } from './cart.service';
import { LocaleService } from '../i18n/locale.service';

@Component({
  selector: 'app-cart-page',
  imports: [CurrencyPipe, RouterLink],
  template: `
    @if (cart.isEmpty()) {
      <section class="empty view-enter page-shell">
        <h1>{{ i18n.t('cart.emptyTitle') }}</h1>
        <p>{{ i18n.t('cart.emptyBody') }}</p>
        <a routerLink="/products" class="quiet-btn quiet-btn--outline">{{ i18n.t('cart.return') }}</a>
      </section>
    } @else {
      <section class="cart view-enter page-shell">
        <h1>{{ i18n.t('cart.title') }}</h1>
        <ul>
          @for (item of cart.items(); track item.product.id) {
            <li>
              <div class="thumb">
                <img
                  [src]="imageSrc(item.product.imageUrl)"
                  [alt]="item.product.name"
                />
              </div>
              <div class="details">
                <div>
                  <h2>{{ item.product.name }}</h2>
                  @if (item.product.description) {
                    <p>{{ item.product.description }}</p>
                  }
                </div>
                <div class="actions">
                  <div class="qty">
                    <button
                      type="button"
                      class="qty-btn"
                      [attr.aria-label]="i18n.t('cart.decrease')"
                      (click)="cart.updateQuantity(item.product.id, item.quantity - 1)"
                    >
                      −
                    </button>
                    <span class="qty-value">{{ item.quantity }}</span>
                    <button
                      type="button"
                      class="qty-btn"
                      [attr.aria-label]="i18n.t('cart.increase')"
                      (click)="cart.updateQuantity(item.product.id, item.quantity + 1)"
                    >
                      +
                    </button>
                  </div>
                  <div class="line">
                    <span class="price">{{
                      i18n.toDisplayMoney(item.product.unitPrice * item.quantity)
                        | currency
                          : i18n.currencyCode()
                          : 'symbol'
                          : '1.2-2'
                          : i18n.localeId()
                    }}</span>
                    <button
                      type="button"
                      class="quiet-btn"
                      (click)="cart.removeFromCart(item.product.id)"
                    >
                      {{ i18n.t('cart.remove') }}
                    </button>
                  </div>
                </div>
              </div>
            </li>
          }
        </ul>
        <div class="summary">
          <div>
            <p class="label">{{ i18n.t('cart.subtotal') }}</p>
            <p class="total">{{
              cart.subtotal()
                | currency: i18n.currencyCode() : 'symbol' : '1.2-2' : i18n.localeId()
            }}</p>
            <p class="note">{{ i18n.t('cart.shippingNote') }}</p>
          </div>
          <a routerLink="/checkout" class="quiet-btn quiet-btn--solid">{{ i18n.t('cart.checkout') }}</a>
        </div>
      </section>
    }
  `,
  styles: `
    .empty {
      max-width: 42rem;
      padding-block: 6rem;
      text-align: center;
    }

    @media (min-width: 640px) {
      .empty {
        padding-block: 8rem;
      }
    }

    .empty h1,
    .cart h1 {
      margin: 0 0 1rem;
      font-family: var(--font-display);
      font-weight: 500;
      font-size: 2.25rem;
      letter-spacing: -0.02em;
    }

    @media (min-width: 640px) {
      .empty h1,
      .cart h1 {
        font-size: 3rem;
      }
    }

    .empty p {
      margin: 0 auto 2.5rem;
      max-width: 24rem;
      color: var(--muted);
      line-height: 1.6;
    }

    .empty a {
      display: inline-block;
      text-decoration: none;
    }

    .cart {
      max-width: 48rem;
      padding-block: 3.5rem;
    }

    @media (min-width: 640px) {
      .cart {
        padding-block: 5rem;
      }

      .cart h1 {
        margin-bottom: 4rem;
      }
    }

    ul {
      list-style: none;
      margin: 0;
      padding: 0;
    }

    li {
      display: flex;
      gap: 1.25rem;
      padding-block: 2rem;
      border-bottom: 1px solid var(--border);
    }

    @media (min-width: 640px) {
      li {
        gap: 2rem;
      }
    }

    .thumb {
      width: 5rem;
      flex-shrink: 0;
      aspect-ratio: 3 / 4;
      overflow: hidden;
      background: var(--surface);
    }

    @media (min-width: 640px) {
      .thumb {
        width: 6rem;
      }
    }

    .thumb img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      display: block;
    }

    .details {
      flex: 1;
      min-width: 0;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      gap: 1rem;
    }

    h2 {
      margin: 0;
      font-family: var(--font-display);
      font-size: 1.25rem;
      font-weight: 500;
    }

    @media (min-width: 640px) {
      h2 {
        font-size: 1.5rem;
      }
    }

    .details p {
      margin: 0.35rem 0 0;
      font-size: 0.875rem;
      color: var(--muted);
      display: -webkit-box;
      -webkit-line-clamp: 1;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .actions {
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      justify-content: space-between;
      gap: 1rem;
    }

    .qty {
      display: flex;
      align-items: center;
      gap: 1rem;
    }

    .qty-btn {
      width: 2.75rem;
      height: 2.75rem;
      display: flex;
      align-items: center;
      justify-content: center;
      border: 1px solid var(--border);
      background: transparent;
      color: var(--muted);
      cursor: pointer;
      transition: color 0.3s ease, border-color 0.3s ease;
    }

    .qty-btn:hover {
      color: var(--fg);
      border-color: var(--accent);
    }

    .qty-value {
      font-family: var(--font-mono);
      font-size: 0.875rem;
      width: 1.5rem;
      text-align: center;
    }

    .line {
      display: flex;
      align-items: center;
      gap: 1.5rem;
    }

    .price,
    .total {
      font-family: var(--font-mono);
    }

    .price {
      font-size: 0.875rem;
    }

    .summary {
      margin-top: 3rem;
      padding-top: 2rem;
      border-top: 1px solid var(--border);
      display: flex;
      flex-direction: column;
      gap: 1.5rem;
    }

    @media (min-width: 640px) {
      .summary {
        flex-direction: row;
        align-items: flex-end;
        justify-content: space-between;
      }
    }

    .label {
      margin: 0 0 0.25rem;
      font-size: 0.75rem;
      letter-spacing: 0.12em;
      text-transform: uppercase;
      color: var(--muted);
    }

    .total {
      margin: 0;
      font-size: 1.5rem;
    }

    .note {
      margin: 0.5rem 0 0;
      font-size: 0.75rem;
      color: var(--muted);
    }

    .summary a {
      display: inline-block;
      text-decoration: none;
      text-align: center;
    }
  `,
})
export class CartPage {
  readonly cart = inject(CartService);
  readonly i18n = inject(LocaleService);

  imageSrc(url: string | null | undefined): string {
    if (!url) {
      return '/assets/images/products/placeholder.png';
    }
    if (url.startsWith('http://') || url.startsWith('https://') || url.startsWith('/')) {
      return url;
    }
    return `/${url}`;
  }
}
