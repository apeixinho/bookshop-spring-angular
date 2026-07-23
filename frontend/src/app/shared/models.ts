export interface Product {
  id: number;
  sku: string;
  name: string;
  description: string;
  unitPrice: number;
  imageUrl: string;
  active: boolean;
  unitsInStock: number;
  category?: ProductCategory;
}

export interface ProductCategory {
  id: number;
  categoryName: string;
}

export interface Country {
  id: number;
  code: string;
  name: string;
}

export interface State {
  id: number;
  name: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}
