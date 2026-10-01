import React from 'react';
import type { ProductSummary } from '../types/product';
import { Tag, ArrowRight, Layers } from 'lucide-react';

interface ProductCardProps {
  product: ProductSummary;
  onSelect: (id: number) => void;
}

export const ProductCard: React.FC<ProductCardProps> = ({ product, onSelect }) => {
  const formatPrice = (price: number | null) => {
    if (!price) return 'Price on request';
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 0,
    }).format(price);
  };

  return (
    <div className="product-card">
      <div className="card-header">
        <span className="badge category-badge">{product.category}</span>
        <span className="brand-tag">{product.brand}</span>
      </div>

      <h3 className="product-name">{product.name}</h3>
      <p className="product-desc">{product.description}</p>

      <div className="card-meta">
        <div className="variant-pill">
          <Layers size={14} />
          <span>{product.variantCount} {product.variantCount === 1 ? 'Variant' : 'Variants'}</span>
        </div>
        {product.model && (
          <div className="model-pill">
            <Tag size={14} />
            <span>Model: {product.model}</span>
          </div>
        )}
      </div>

      <div className="card-footer">
        <div className="price-block">
          <span className="price-label">Observed from</span>
          <span className="price-value">{formatPrice(product.startingPrice)}</span>
        </div>

        <button
          type="button"
          className="details-button"
          onClick={() => onSelect(product.id)}
        >
          <span>View Details</span>
          <ArrowRight size={16} />
        </button>
      </div>
    </div>
  );
};
