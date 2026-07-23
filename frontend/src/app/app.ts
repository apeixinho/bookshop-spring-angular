import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { CartService } from './cart/cart.service';
import { AuthService } from './auth/auth.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, MatToolbarModule, MatButtonModule],
  template: `
    <mat-toolbar color="primary">
      <a routerLink="/products" class="brand">Bookshop</a>
      <span class="spacer"></span>
      <a mat-button routerLink="/products" routerLinkActive="active">Catalog</a>
      <a mat-button routerLink="/cart" routerLinkActive="active">Cart ({{ cart.totalItems() }})</a>
      @if (auth.isAuthenticated()) {
        <button mat-button type="button" (click)="auth.logout()">Sign out</button>
      } @else {
        <button mat-button type="button" (click)="auth.login()">Sign in</button>
      }
    </mat-toolbar>
    <main>
      <router-outlet />
    </main>
  `,
  styles: `
    .brand {
      color: inherit;
      text-decoration: none;
      font-weight: 700;
      letter-spacing: 0.02em;
    }
    .spacer {
      flex: 1;
    }
    main {
      min-height: calc(100vh - 64px);
      background: linear-gradient(180deg, #f7f4ef 0%, #efe8dc 100%);
    }
    a.active {
      text-decoration: underline;
    }
  `,
})
export class App {
  readonly cart = inject(CartService);
  readonly auth = inject(AuthService);
}
