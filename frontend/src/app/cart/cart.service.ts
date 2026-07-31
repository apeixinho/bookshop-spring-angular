import { Injectable, computed, inject, signal, effect, untracked } from '@angular/core';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Product } from '../shared/models';
import { NotificationService } from '../shared/notification.service';
import { LocaleService } from '../i18n/locale.service';
import { CatalogApiService } from '../shared/catalog-api.service';

export interface CartItem {
  product: Product;
  quantity: number;
}

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly cartItems = signal<CartItem[]>([]);
  private readonly notifications = inject(NotificationService);
  private readonly i18n = inject(LocaleService);
  private readonly api = inject(CatalogApiService);

  readonly items = this.cartItems.asReadonly();
  readonly totalItems = computed(() =>
    this.cartItems().reduce((sum, item) => sum + item.quantity, 0),
  );
  readonly subtotal = computed(() => {
    this.i18n.currencyCode();
    return this.cartItems().reduce(
      (sum, item) => sum + this.i18n.lineTotal(Number(item.product.unitPrice), item.quantity),
      0,
    );
  });
  readonly isEmpty = computed(() => this.cartItems().length === 0);

  constructor() {
    effect(() => {
      this.i18n.language();
      const items = untracked(() => this.cartItems());
      if (items.length === 0) {
        return;
      }
      const ids = items.map((item) => item.product.id);
      forkJoin(
        ids.map((id) => this.api.getProduct(id).pipe(catchError(() => of(null)))),
      ).subscribe((products) => {
        const byId = new Map(
          products.filter((p): p is Product => p != null).map((p) => [p.id, p]),
        );
        this.cartItems.update((current) =>
          current.flatMap((item) => {
            const fresh = byId.get(item.product.id);
            if (!fresh) {
              return [item];
            }
            if (!fresh.active || fresh.unitsInStock <= 0) {
              return [];
            }
            return [
              {
                quantity: Math.min(item.quantity, fresh.unitsInStock),
                product: fresh,
              },
            ];
          }),
        );
      });
    });
  }

  addToCart(product: Product, quantity = 1): void {
    if (!product.active || product.unitsInStock <= 0) {
      this.notifications.info(this.i18n.t('toast.outOfStock'));
      return;
    }
    const items = [...this.cartItems()];
    const index = items.findIndex((item) => item.product.id === product.id);
    if (index >= 0) {
      const nextQty = Math.min(items[index].quantity + quantity, product.unitsInStock);
      items[index] = { ...items[index], product, quantity: nextQty };
    } else {
      items.push({ product, quantity: Math.min(quantity, product.unitsInStock) });
    }
    this.cartItems.set(items);
    this.notifications.success(this.i18n.t('toast.addedToCart', { name: product.name }));
  }

  updateQuantity(productId: number, quantity: number): void {
    if (quantity <= 0) {
      this.removeFromCart(productId);
      return;
    }
    this.cartItems.set(
      this.cartItems().map((item) => {
        if (item.product.id !== productId) {
          return item;
        }
        return {
          ...item,
          quantity: Math.min(quantity, item.product.unitsInStock || quantity),
        };
      }),
    );
  }

  removeFromCart(productId: number): void {
    this.cartItems.set(this.cartItems().filter((item) => item.product.id !== productId));
  }

  clearCart(): void {
    this.cartItems.set([]);
  }
}
