import { Component, OnInit, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CatalogApiService } from '../shared/catalog-api.service';
import { Page, Product, ProductCategory } from '../shared/models';
import { CartService } from '../cart/cart.service';

const PAGE_SIZE = 8;

@Component({
  selector: 'app-products-page',
  imports: [CurrencyPipe, FormsModule],
  template: `
    <div class="catalog view-enter">
      <section class="filters page-shell">
        <div class="filter-row">
          <label class="search">
            <span class="sr-only">Search</span>
            <input
              class="gallery-input"
              type="search"
              [(ngModel)]="search"
              (keydown.enter)="onSearch()"
              placeholder="Search titles…"
            />
          </label>
          <label class="category">
            <span class="sr-only">Category</span>
            <select
              class="gallery-select"
              [(ngModel)]="categoryId"
              (ngModelChange)="onCategoryChange()"
            >
              <option [ngValue]="null">All categories</option>
              @for (category of categories(); track category.id) {
                <option [ngValue]="category.id">{{ category.categoryName }}</option>
              }
            </select>
          </label>
        </div>
      </section>

      <section class="grid-wrap page-shell">
        @if (products().length === 0) {
          <p class="empty">No works match your search.</p>
        } @else {
          <div class="grid">
            @for (product of products(); track product.id; let i = $index) {
              <article class="product animate-fade-up" [style.animation-delay.ms]="i * 70">
                <div class="product-image-wrap frame">
                  <img
                    class="animate-fade-in"
                    [src]="imageSrc(product.imageUrl)"
                    [alt]="product.name"
                    loading="lazy"
                  />
                </div>
                <div class="meta">
                  <h2>{{ product.name }}</h2>
                  @if (product.description) {
                    <p class="desc">{{ product.description }}</p>
                  }
                  <div class="row">
                    <span class="price">{{ product.unitPrice | currency }}</span>
                    <button class="quiet-btn add" type="button" (click)="add(product)">
                      Add to cart
                    </button>
                  </div>
                </div>
              </article>
            }
          </div>

          @if (totalPages() > 1) {
            <nav class="pagination" aria-label="Catalog pages">
              <button
                type="button"
                class="quiet-btn page-btn"
                [disabled]="pageIndex() === 0"
                (click)="goToPage(pageIndex() - 1)"
              >
                Previous
              </button>
              <span class="page-status">
                <span class="mono">{{ pageIndex() + 1 }}</span>
                <span class="of">/</span>
                <span class="mono">{{ totalPages() }}</span>
              </span>
              <button
                type="button"
                class="quiet-btn page-btn"
                [disabled]="pageIndex() >= totalPages() - 1"
                (click)="goToPage(pageIndex() + 1)"
              >
                Next
              </button>
            </nav>
          }
        }
      </section>
    </div>
  `,
  styles: `
    .filter-row {
      display: flex;
      flex-direction: column;
      gap: 1rem;
      border-bottom: 1px solid var(--border);
      padding-block: 1.5rem;
    }

    @media (min-width: 640px) {
      .filter-row {
        flex-direction: row;
        align-items: flex-end;
        gap: 2rem;
        padding-block: 2rem;
      }
    }

    .search {
      flex: 1;
    }

    .category {
      width: 100%;
    }

    @media (min-width: 640px) {
      .category {
        width: 12rem;
      }
    }

    .grid-wrap {
      padding-bottom: 5rem;
      padding-top: 2rem;
    }

    @media (min-width: 640px) {
      .grid-wrap {
        padding-bottom: 7rem;
        padding-top: 2.5rem;
      }
    }

    .empty {
      margin: 4rem 0;
      text-align: center;
      font-family: var(--font-display);
      font-size: 1.5rem;
      color: var(--muted);
    }

    .grid {
      display: grid;
      grid-template-columns: 1fr;
      column-gap: 2rem;
      row-gap: 3.5rem;
    }

    @media (min-width: 640px) {
      .grid {
        grid-template-columns: repeat(2, 1fr);
        row-gap: 4rem;
      }
    }

    @media (min-width: 1024px) {
      .grid {
        grid-template-columns: repeat(3, 1fr);
      }
    }

    @media (min-width: 1280px) {
      .grid {
        grid-template-columns: repeat(4, 1fr);
      }
    }

    .frame {
      aspect-ratio: 3 / 4;
      margin-bottom: 1.25rem;
      background: var(--surface);
    }

    .frame img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      display: block;
    }

    .meta h2 {
      margin: 0;
      font-family: var(--font-display);
      font-size: 1.25rem;
      font-weight: 500;
      line-height: 1.2;
      letter-spacing: -0.02em;
    }

    @media (min-width: 640px) {
      .meta h2 {
        font-size: 1.5rem;
      }
    }

    .desc {
      margin: 0.35rem 0 0;
      font-size: 0.875rem;
      color: var(--muted);
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .row {
      display: flex;
      align-items: baseline;
      justify-content: space-between;
      gap: 1rem;
      padding-top: 0.75rem;
    }

    .price {
      font-family: var(--font-mono);
      font-size: 0.875rem;
    }

    .add {
      border-bottom: 1px solid transparent;
      padding-bottom: 0.125rem;
    }

    .add:hover {
      border-bottom-color: var(--accent);
    }

    .pagination {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 1.5rem;
      margin-top: 3.5rem;
      padding-top: 2rem;
      border-top: 1px solid var(--border);
    }

    .page-btn:disabled {
      opacity: 0.35;
      cursor: not-allowed;
    }

    .page-status {
      display: flex;
      align-items: baseline;
      gap: 0.35rem;
      font-size: 0.875rem;
      color: var(--muted);
    }

    .mono {
      font-family: var(--font-mono);
      color: var(--fg);
    }

    .of {
      opacity: 0.6;
    }
  `,
})
export class ProductsPage implements OnInit {
  private readonly api = inject(CatalogApiService);
  private readonly cart = inject(CartService);

  readonly products = signal<Product[]>([]);
  readonly categories = signal<ProductCategory[]>([]);
  readonly pageIndex = signal(0);
  readonly totalPages = signal(0);
  readonly totalElements = signal(0);

  search = '';
  categoryId: number | null = null;

  ngOnInit(): void {
    this.api.getCategories().subscribe((categories) => this.categories.set(categories));
    this.load();
  }

  onSearch(): void {
    this.pageIndex.set(0);
    this.load();
  }

  onCategoryChange(): void {
    this.pageIndex.set(0);
    this.load();
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages()) {
      return;
    }
    this.pageIndex.set(page);
    this.load();
  }

  load(): void {
    const page = this.pageIndex();
    const request =
      this.categoryId != null
        ? this.api.searchByCategory(this.categoryId, page, PAGE_SIZE)
        : this.search.trim()
          ? this.api.searchByName(this.search.trim(), page, PAGE_SIZE)
          : this.api.getProducts(page, PAGE_SIZE);

    request.subscribe((result) => this.applyPage(result));
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

  private applyPage(result: Page<Product>): void {
    this.products.set(result.content ?? []);
    const meta = result.page;
    this.pageIndex.set(meta?.number ?? result.number ?? 0);
    this.totalPages.set(meta?.totalPages ?? result.totalPages ?? 0);
    this.totalElements.set(meta?.totalElements ?? result.totalElements ?? 0);
  }
}
