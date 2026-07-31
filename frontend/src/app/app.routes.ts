import { Routes } from '@angular/router';
import { authGuard } from './auth/auth.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'products' },
  {
    path: 'products',
    loadComponent: () => import('./products/products-page').then((m) => m.ProductsPage),
  },
  {
    path: 'cart',
    loadComponent: () => import('./cart/cart-page').then((m) => m.CartPage),
  },
  {
    path: 'checkout/result',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./checkout/checkout-result-page').then((m) => m.CheckoutResultPage),
  },
  {
    path: 'checkout',
    canActivate: [authGuard],
    loadComponent: () => import('./checkout/checkout-page').then((m) => m.CheckoutPage),
  },
  {
    path: 'account',
    canActivate: [authGuard],
    loadComponent: () => import('./account/account-page').then((m) => m.AccountPage),
  },
  {
    path: 'auth/callback',
    loadComponent: () => import('./auth/auth-callback.page').then((m) => m.AuthCallbackPage),
  },
  { path: '**', redirectTo: 'products' },
];
