import { Injectable, computed, signal } from '@angular/core';
import { Product } from '../shared/models';

export interface CartItem {
  product: Product;
  quantity: number;
}

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly cartItems = signal<CartItem[]>([]);

  readonly items = this.cartItems.asReadonly();
  readonly totalItems = computed(() =>
    this.cartItems().reduce((sum, item) => sum + item.quantity, 0),
  );
  readonly subtotal = computed(() =>
    this.cartItems().reduce((sum, item) => sum + Number(item.product.unitPrice) * item.quantity, 0),
  );
  readonly isEmpty = computed(() => this.cartItems().length === 0);

  addToCart(product: Product, quantity = 1): void {
    const items = [...this.cartItems()];
    const index = items.findIndex((item) => item.product.id === product.id);
    if (index >= 0) {
      items[index] = { ...items[index], quantity: items[index].quantity + quantity };
    } else {
      items.push({ product, quantity });
    }
    this.cartItems.set(items);
  }

  updateQuantity(productId: number, quantity: number): void {
    if (quantity <= 0) {
      this.removeFromCart(productId);
      return;
    }
    this.cartItems.set(
      this.cartItems().map((item) =>
        item.product.id === productId ? { ...item, quantity } : item,
      ),
    );
  }

  removeFromCart(productId: number): void {
    this.cartItems.set(this.cartItems().filter((item) => item.product.id !== productId));
  }

  clearCart(): void {
    this.cartItems.set([]);
  }
}
