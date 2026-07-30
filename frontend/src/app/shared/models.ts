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

/** Spring Data page payload (flat or VIA_DTO nested `page` metadata). */
export interface Page<T> {
  content: T[];
  totalElements?: number;
  totalPages?: number;
  number?: number;
  size?: number;
  page?: {
    size: number;
    number: number;
    totalElements: number;
    totalPages: number;
  };
}
