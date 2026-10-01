import React from 'react';
import type { ListingPrice } from '../types/product';
import { ExternalLink, CheckCircle2, Clock, AlertCircle } from 'lucide-react';

interface PriceComparisonTableProps {
  listings: ListingPrice[];
}

export const PriceComparisonTable: React.FC<PriceComparisonTableProps> = ({ listings }) => {
  const formatPrice = (price: number | null) => {
    if (price === null || price === undefined) return 'Unavailable';
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 0,
    }).format(price);
  };

  const formatRelativeTime = (isoString: string) => {
    try {
      const date = new Date(isoString);
      const diffHours = Math.round((Date.now() - date.getTime()) / (1000 * 60 * 60));
      if (diffHours < 1) return 'Just observed';
      if (diffHours < 24) return `${diffHours}h ago`;
      const diffDays = Math.round(diffHours / 24);
      return `${diffDays}d ago`;
    } catch {
      return 'Recently';
    }
  };

  if (!listings || listings.length === 0) {
    return (
      <div className="empty-listings-box">
        <AlertCircle size={20} className="text-muted" />
        <p>No active store listings observed for this item yet.</p>
      </div>
    );
  }

  return (
    <div className="comparison-container">
      <div className="table-responsive">
        <table className="comparison-table">
          <thead>
            <tr>
              <th>Store / Source</th>
              <th>Seller</th>
              <th>Status</th>
              <th>Observed Price</th>
              <th>Shipping</th>
              <th>Freshness</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {listings.map((l) => (
              <tr key={l.listingId} className={l.isLowestPrice ? 'highlight-row' : ''}>
                <td className="store-cell">
                  <div className="store-badge-group">
                    <span className="store-name">{l.sourceName}</span>
                    {l.isLowestPrice && (
                      <span className="lowest-badge">
                        <CheckCircle2 size={12} /> Lowest Price
                      </span>
                    )}
                  </div>
                </td>
                <td className="seller-cell">{l.sellerName || 'Direct'}</td>
                <td>
                  <span className={`status-pill ${l.availability === 'IN_STOCK' ? 'in-stock' : 'out-of-stock'}`}>
                    {l.availability === 'IN_STOCK' ? 'In Stock' : 'Check Store'}
                  </span>
                </td>
                <td className="price-cell">
                  <span className="obs-price">{formatPrice(l.price)}</span>
                </td>
                <td className="shipping-cell">
                  {l.shippingCost > 0 ? formatPrice(l.shippingCost) : 'Free Delivery'}
                </td>
                <td className="freshness-cell">
                  <span className="freshness-text">
                    <Clock size={12} /> {formatRelativeTime(l.lastObserved)}
                  </span>
                </td>
                <td className="action-cell">
                  <a
                    href={l.productUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="visit-store-btn"
                  >
                    <span>Visit Store</span>
                    <ExternalLink size={13} />
                  </a>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="comparison-note">
        <span>* Observed prices are timestamped from store listings. Final checkout totals may vary based on bank discounts, coupons, and location.</span>
      </div>
    </div>
  );
};
