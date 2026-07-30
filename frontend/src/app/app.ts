import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CartService } from './cart/cart.service';
import { AuthService } from './auth/auth.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="shell">
      <header class="site-header">
        <div class="header-inner page-shell">
          <a routerLink="/products" class="brand">Bookshop</a>
          <nav class="main-nav" aria-label="Primary">
            <a
              routerLink="/products"
              routerLinkActive="active"
              class="nav-link"
              >Catalog</a
            >
            <a routerLink="/cart" routerLinkActive="active" class="nav-link">
              Cart
              @if (cart.totalItems() > 0) {
                <span class="cart-count">({{ cart.totalItems() }})</span>
              }
            </a>
            @if (auth.isAuthenticated()) {
              <button
                type="button"
                class="nav-link nav-button"
                (click)="auth.logout()"
              >
                Sign out
              </button>
            } @else {
              <button type="button" class="nav-link nav-button" (click)="auth.login()">
                Sign in
              </button>
            }
          </nav>
        </div>
      </header>

      <main class="main">
        <router-outlet />
      </main>

      <footer class="site-footer">
        <div class="footer-inner page-shell">
          <span>Independent bookshop · Est. 1987</span>
          <span>42 Museum Lane, London</span>
        </div>
      </footer>
    </div>
  `,
  styles: `
    .shell {
      min-height: 100dvh;
      display: flex;
      flex-direction: column;
      font-family: var(--font-sans);
      color: var(--fg);
      background: var(--bg);
    }

    .site-header {
      border-bottom: 1px solid var(--border);
    }

    .header-inner {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 1.5rem;
      padding-block: 1.25rem;
    }

    .brand {
      font-family: var(--font-display);
      font-size: 1.25rem;
      letter-spacing: -0.02em;
      text-decoration: none;
      transition: color 0.3s ease;
    }

    .brand:hover {
      color: var(--accent);
    }

    @media (min-width: 640px) {
      .brand {
        font-size: 1.5rem;
      }
    }

    .main-nav {
      display: flex;
      align-items: center;
      gap: 1.5rem;
      font-size: 0.875rem;
      letter-spacing: 0.04em;
    }

    @media (min-width: 640px) {
      .main-nav {
        gap: 2.5rem;
      }
    }

    .nav-link {
      position: relative;
      color: var(--muted);
      text-decoration: none;
      transition: color 0.3s ease;
      background: none;
      border: 0;
      padding: 0;
      font: inherit;
      letter-spacing: inherit;
      cursor: pointer;
    }

    .nav-link::after {
      content: '';
      position: absolute;
      left: 0;
      bottom: -2px;
      width: 100%;
      height: 1px;
      background: var(--accent);
      transform: scaleX(0);
      transform-origin: left;
      transition: transform 0.35s ease;
    }

    .nav-link:hover,
    .nav-link.active {
      color: var(--fg);
    }

    .nav-link:hover::after,
    .nav-link.active::after {
      transform: scaleX(1);
    }

    .cart-count {
      font-family: var(--font-mono);
      font-size: 0.75rem;
      margin-left: 0.35rem;
      color: var(--muted);
    }

    .main {
      flex: 1;
    }

    .site-footer {
      border-top: 1px solid var(--border);
      margin-top: auto;
    }

    .footer-inner {
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      gap: 0.5rem;
      padding-block: 2rem;
      font-size: 0.75rem;
      letter-spacing: 0.04em;
      color: var(--muted);
    }

    @media (min-width: 640px) {
      .footer-inner {
        flex-direction: row;
      }
    }
  `,
})
export class App {
  readonly cart = inject(CartService);
  readonly auth = inject(AuthService);
}
