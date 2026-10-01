import type { ProductSummary, ProductDetail, ProductPricesResponse, ResolveProductResponse } from '../types/product';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

export async function searchProducts(query: string = ''): Promise<ProductSummary[]> {
  const url = new URL(`${API_BASE_URL}/products/search`);
  if (query.trim()) {
    url.searchParams.set('q', query.trim());
  }

  const response = await fetch(url.toString(), {
    headers: {
      'Accept': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch products: ${response.status} ${response.statusText}`);
  }

  return response.json();
}

export async function getProductById(id: number): Promise<ProductDetail> {
  const response = await fetch(`${API_BASE_URL}/products/${id}`, {
    headers: {
      'Accept': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch product details: ${response.status} ${response.statusText}`);
  }

  return response.json();
}

export async function getProductPrices(id: number): Promise<ProductPricesResponse> {
  const response = await fetch(`${API_BASE_URL}/products/${id}/prices`, {
    headers: {
      'Accept': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch price intelligence: ${response.status} ${response.statusText}`);
  }

  return response.json();
}

export async function resolveProductUrl(url: string): Promise<ResolveProductResponse> {
  const response = await fetch(`${API_BASE_URL}/products/resolve`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
    body: JSON.stringify({ url: url.trim() }),
  });

  if (!response.ok) {
    throw new Error(`Failed to resolve URL: ${response.status} ${response.statusText}`);
  }

  return response.json();
}
