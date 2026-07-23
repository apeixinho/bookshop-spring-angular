import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Country, Page, Product, ProductCategory, State } from './models';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CatalogApiService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiBaseUrl}/api/v1`;

  getProducts(page = 0, size = 8): Observable<Page<Product>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Product>>(`${this.base}/products`, { params });
  }

  getProduct(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.base}/products/${id}`);
  }

  searchByName(name: string, page = 0, size = 8): Observable<Page<Product>> {
    const params = new HttpParams().set('name', name).set('page', page).set('size', size);
    return this.http.get<Page<Product>>(`${this.base}/products/search/findByNameContaining`, { params });
  }

  searchByCategory(id: number, page = 0, size = 8): Observable<Page<Product>> {
    const params = new HttpParams().set('id', id).set('page', page).set('size', size);
    return this.http.get<Page<Product>>(`${this.base}/products/search/findByCategoryId`, { params });
  }

  getCategories(): Observable<ProductCategory[]> {
    return this.http.get<ProductCategory[]>(`${this.base}/product-category`);
  }

  getCountries(): Observable<Country[]> {
    return this.http.get<Country[]>(`${this.base}/countries`);
  }

  getStates(countryCode: string): Observable<State[]> {
    const params = new HttpParams().set('code', countryCode);
    return this.http.get<State[]>(`${this.base}/states/search/findByCountryCode`, { params });
  }

  purchase(body: unknown): Observable<{ orderTrackingNumber: string }> {
    return this.http.post<{ orderTrackingNumber: string }>(`${this.base}/checkout/purchase`, body);
  }
}
