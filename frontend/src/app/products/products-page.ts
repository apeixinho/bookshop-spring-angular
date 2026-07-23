import { Component, OnInit, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { CatalogApiService } from '../shared/catalog-api.service';
import { Product, ProductCategory } from '../shared/models';
import { CartService } from '../cart/cart.service';

@Component({
  selector: 'app-products-page',
  imports: [
    CurrencyPipe,
    FormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  template: `
    <section class="catalog">
      <header>
        <h1>Bookshop catalog</h1>
        <p>Browse books, mugs, and more — login only needed at checkout.</p>
      </header>

      <div class="filters">
        <mat-form-field appearance="outline">
          <mat-label>Search</mat-label>
          <input matInput [(ngModel)]="search" (keyup.enter)="load()" />
        </mat-form-field>
        <mat-form-field appearance="outline">
          <mat-label>Category</mat-label>
          <mat-select [(ngModel)]="categoryId" (selectionChange)="load()">
            <mat-option [value]="null">All</mat-option>
            @for (category of categories(); track category.id) {
              <mat-option [value]="category.id">{{ category.categoryName }}</mat-option>
            }
          </mat-select>
        </mat-form-field>
        <button mat-stroked-button type="button" (click)="load()">Apply</button>
      </div>

      <div class="grid">
        @for (product of products(); track product.id) {
          <article class="card">
            <img [src]="imageSrc(product.imageUrl)" [alt]="product.name" />
            <h2>{{ product.name }}</h2>
            <p>{{ product.description }}</p>
            <strong>{{ product.unitPrice | currency }}</strong>
            <button mat-flat-button color="primary" type="button" (click)="add(product)">
              Add to cart
            </button>
          </article>
        } @empty {
          <p>No products found.</p>
        }
      </div>
    </section>
  `,
  styles: `
    .catalog {
      padding: 1.5rem;
      max-width: 1100px;
      margin: 0 auto;
    }
    header h1 {
      margin-bottom: 0.25rem;
      font-family: Georgia, 'Times New Roman', serif;
    }
    .filters {
      display: flex;
      flex-wrap: wrap;
      gap: 0.75rem;
      align-items: center;
      margin: 1rem 0 1.5rem;
    }
    .grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
      gap: 1rem;
    }
    .card {
      display: grid;
      gap: 0.5rem;
      padding: 0.75rem;
      background: #fff;
      border: 1px solid #ddd5c6;
    }
    img {
      width: 100%;
      aspect-ratio: 1;
      object-fit: cover;
      background: #eee;
    }
  `,
})
export class ProductsPage implements OnInit {
  private readonly api = inject(CatalogApiService);
  private readonly cart = inject(CartService);

  readonly products = signal<Product[]>([]);
  readonly categories = signal<ProductCategory[]>([]);
  search = '';
  categoryId: number | null = null;

  ngOnInit(): void {
    this.api.getCategories().subscribe((categories) => this.categories.set(categories));
    this.load();
  }

  load(): void {
    const request =
      this.categoryId != null
        ? this.api.searchByCategory(this.categoryId)
        : this.search.trim()
          ? this.api.searchByName(this.search.trim())
          : this.api.getProducts();
    request.subscribe((page) => this.products.set(page.content ?? []));
  }

  add(product: Product): void {
    this.cart.addToCart(product);
  }

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
