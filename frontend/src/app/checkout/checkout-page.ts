import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CurrencyPipe } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { CartService } from '../cart/cart.service';
import { CatalogApiService } from '../shared/catalog-api.service';
import { Country, State } from '../shared/models';

@Component({
  selector: 'app-checkout-page',
  imports: [
    ReactiveFormsModule,
    CurrencyPipe,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  template: `
    <section class="checkout">
      <h1>Checkout</h1>
      <p>Authenticated purchase — total {{ cart.subtotal() | currency }}</p>

      <form [formGroup]="form" (ngSubmit)="submit()">
        <div class="row">
          <mat-form-field appearance="outline">
            <mat-label>First name</mat-label>
            <input matInput formControlName="firstName" />
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Last name</mat-label>
            <input matInput formControlName="lastName" />
          </mat-form-field>
        </div>
        <mat-form-field appearance="outline" class="full">
          <mat-label>Email</mat-label>
          <input matInput type="email" formControlName="email" />
        </mat-form-field>
        <mat-form-field appearance="outline" class="full">
          <mat-label>Street</mat-label>
          <input matInput formControlName="street" />
        </mat-form-field>
        <div class="row">
          <mat-form-field appearance="outline">
            <mat-label>City</mat-label>
            <input matInput formControlName="city" />
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Zip</mat-label>
            <input matInput formControlName="zipCode" />
          </mat-form-field>
        </div>
        <div class="row">
          <mat-form-field appearance="outline">
            <mat-label>Country</mat-label>
            <mat-select formControlName="country" (selectionChange)="onCountry($event.value)">
              @for (country of countries(); track country.id) {
                <mat-option [value]="country.code">{{ country.name }}</mat-option>
              }
            </mat-select>
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>State</mat-label>
            <mat-select formControlName="state">
              @for (state of states(); track state.id) {
                <mat-option [value]="state.name">{{ state.name }}</mat-option>
              }
            </mat-select>
          </mat-form-field>
        </div>
        <button mat-flat-button color="primary" type="submit" [disabled]="form.invalid || cart.isEmpty() || submitting()">
          Place order
        </button>
      </form>

      @if (trackingNumber()) {
        <p class="success">Order placed! Tracking number: {{ trackingNumber() }}</p>
      }
      @if (error()) {
        <p class="error">{{ error() }}</p>
      }
    </section>
  `,
  styles: `
    .checkout {
      max-width: 720px;
      margin: 0 auto;
      padding: 1.5rem;
    }
    form {
      display: grid;
      gap: 0.5rem;
    }
    .row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 0.75rem;
    }
    .full {
      width: 100%;
    }
    .success {
      color: #2f5d50;
      font-weight: 600;
    }
    .error {
      color: #a12828;
    }
  `,
})
export class CheckoutPage implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(CatalogApiService);
  readonly cart = inject(CartService);

  readonly countries = signal<Country[]>([]);
  readonly states = signal<State[]>([]);
  readonly trackingNumber = signal<string | null>(null);
  readonly error = signal<string | null>(null);
  readonly submitting = signal(false);

  readonly form = this.fb.nonNullable.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    street: ['', Validators.required],
    city: ['', Validators.required],
    zipCode: ['', Validators.required],
    country: ['', Validators.required],
    state: ['', Validators.required],
  });

  ngOnInit(): void {
    this.api.getCountries().subscribe((countries) => this.countries.set(countries));
  }

  onCountry(code: string): void {
    this.api.getStates(code).subscribe((states) => this.states.set(states));
  }

  submit(): void {
    if (this.form.invalid || this.cart.isEmpty()) {
      return;
    }
    this.submitting.set(true);
    this.error.set(null);
    const value = this.form.getRawValue();
    const address = {
      street: value.street,
      city: value.city,
      state: value.state,
      country: value.country,
      zipCode: value.zipCode,
    };
    const orderItems = this.cart.items().map((item) => ({
      imageUrl: item.product.imageUrl,
      quantity: item.quantity,
      unitPrice: item.product.unitPrice,
      productId: item.product.id,
    }));
    const body = {
      customer: {
        firstName: value.firstName,
        lastName: value.lastName,
        email: value.email,
      },
      shippingAddress: address,
      billingAddress: address,
      order: {
        totalQuantity: this.cart.totalItems(),
        totalPrice: this.cart.subtotal(),
      },
      orderItems,
    };

    this.api.purchase(body).subscribe({
      next: (response) => {
        this.trackingNumber.set(response.orderTrackingNumber);
        this.cart.clearCart();
        this.submitting.set(false);
      },
      error: () => {
        this.error.set('Purchase failed. Sign in again or check the API.');
        this.submitting.set(false);
      },
    });
  }
}
