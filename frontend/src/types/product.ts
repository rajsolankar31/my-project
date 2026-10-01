export interface ProductSummary {
  id: number;
  brand: string;
  name: string;
  model: string;
  category: string;
  description: string;
  variantCount: number;
  startingPrice: number | null;
}

export interface ProductVariant {
  id: number;
  sku: string | null;
  manufacturerModel: string | null;
  color: string | null;
  storage: string | null;
  size: string | null;
  attributes: Record<string, string | number | boolean>;
  suggestedPrice: number | null;
}

export interface ProductDetail {
  id: number;
  brand: string;
  name: string;
  model: string;
  category: string;
  description: string;
  variants: ProductVariant[];
  lastUpdated: string;
}

export interface ListingPrice {
  listingId: number;
  sourceName: string;
  sellerName: string;
  title: string;
  productUrl: string;
  price: number | null;
  shippingCost: number;
  totalEstimatedPrice: number | null;
  availability: string;
  lastObserved: string;
  isLowestPrice: boolean;
}

export interface PricePoint {
  timestamp: string;
  formattedDate: string;
  price: number;
  sourceName: string;
  sellerName: string;
}

export interface PriceSummary {
  currentObserved: number;
  lowestObserved: number;
  highestObserved: number;
  averageObserved: number;
  medianObserved: number;
  currency: string;
  observationCount: number;
  pricePosition: 'AT_LOWEST' | 'BELOW_AVERAGE' | 'NEAR_AVERAGE' | 'ABOVE_AVERAGE';
  percentDiffFromAverage: number;
  latestObservationTime: string;
}

export interface ProductPricesResponse {
  productId: number;
  summary: PriceSummary | null;
  timeline: PricePoint[];
  listings: ListingPrice[];
}

export interface ResolveProductResponse {
  resolved: boolean;
  message: string;
  sourceName: string | null;
  externalId: string | null;
  productId: number | null;
  product: ProductDetail | null;
  prices: ProductPricesResponse | null;
}
