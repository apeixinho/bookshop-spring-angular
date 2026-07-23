import { Component, inject } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { CartService } from './cart.service';

@Component({
  selector: 'app-cart-page',
  imports: [CurrencyPipe, RouterLink, MatButtonModule],
  template: `
    <section class="cart">
      <h1>Your cart</h1>
      @if (cart.isEmpty()) {
        <p>Cart is empty. <a routerLink="/products">Continue shopping</a></p>
      } @else {
        <ul>
          @for (item of cart.items(); track item.product.id) {
            <li>
              <div>
                <strong>{{ item.product.name }}</strong>
                <span>{{ item.product.unitPrice | currency }} × {{ item.quantity }}</span>
              </div>
              <div class="actions">
                <button mat-button type="button" (click)="cart.updateQuantity(item.product.id, item.quantity - 1)">
                  −
                </button>
                <button mat-button type="button" (click)="cart.updateQuantity(item.product.id, item.quantity + 1)">
                  +
                </button>
                <button mat-button type="button" (click)="cart.removeFromCart(item.product.id)">Remove</button>
              </div>
            </li>
          }
        </ul>
        <p class="total">Subtotal: {{ cart.subtotal() | currency }}</p>
        <a mat-flat-button color="primary" routerLink="/checkout">Checkout</a>
      }
    </section>
  `,
  styles: `
    .cart {
      max-width: 720px;
      margin: 0 auto;
      padding: 1.5rem;
    }
    ul {
      list-style: none;
      padding: 0;
    }
    li {
      display: flex;
      justify-content: space-between;
      gap: 1rem;
      padding: 0.75rem 0;
      border-bottom: 1px solid #ddd5c6;
    }
    .total {
      font-size: 1.1rem;
      font-weight: 600;
    }
  `,
})
export class CartPage {
  readonly cart = inject(CartService);
}
