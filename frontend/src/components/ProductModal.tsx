import React, { useState, useEffect } from 'react';
import type { ProductDetail, ProductPricesResponse } from '../types/product';
import { PriceComparisonTable } from './PriceComparisonTable';
import { PriceTimelineChart } from './PriceTimelineChart';
import { getProductPrices } from '../services/api';
import { X, CheckCircle, Clock, ShieldAlert, Cpu, Palette, HardDrive, Maximize2, Tag, LineChart, Layers, RefreshCw } from 'lucide-react';

interface ProductModalProps {
  product: ProductDetail;
  initialPrices?: ProductPricesResponse | null;
  onClose: () => void;
}

export const ProductModal: React.FC<ProductModalProps> = ({ product, initialPrices = null, onClose }) => {
  const [selectedVariantIndex, setSelectedVariantIndex] = useState(0);
  const [activeTab, setActiveTab] = useState<'prices' | 'timeline' | 'specs'>('prices');
  const [pricesData, setPricesData] = useState<ProductPricesResponse | null>(initialPrices);
  const [pricesLoading, setPricesLoading] = useState<boolean>(!initialPrices);

  const selectedVariant = product.variants[selectedVariantIndex] || product.variants[0];

  useEffect(() => {
    if (!initialPrices) {
      setPricesLoading(true);
      getProductPrices(product.id)
        .then((data) => setPricesData(data))
        .catch((err) => console.error('Failed to load prices:', err))
        .finally(() => setPricesLoading(false));
    }
  }, [product.id, initialPrices]);

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [onClose]);

  const formatPrice = (price: number | null) => {
    if (!price) return 'Price on request';
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 0,
    }).format(price);
  };

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-container large" onClick={(e) => e.stopPropagation()}>
        <button type="button" className="modal-close-button" onClick={onClose} aria-label="Close modal">
          <X size={20} />
        </button>

        {/* Modal Top Header */}
        <div className="modal-header">
          <div className="modal-badges">
            <span className="badge category-badge">{product.category}</span>
            <span className="badge brand-badge">{product.brand}</span>
            {pricesData?.summary?.pricePosition === 'AT_LOWEST' && (
              <span className="badge lowest-deal-badge">All-Time Low</span>
            )}
          </div>
          <h2 className="modal-title">{product.name}</h2>
          <p className="modal-subtitle">Model: <strong>{product.model}</strong></p>
          <p className="modal-description">{product.description}</p>
        </div>

        {/* Modal Navigation Tabs */}
        <div className="modal-tab-bar">
          <button
            type="button"
            className={`modal-tab ${activeTab === 'prices' ? 'active' : ''}`}
            onClick={() => setActiveTab('prices')}
          >
            <Tag size={16} />
            <span>Store Comparison ({pricesData?.listings?.length || 0})</span>
          </button>
          <button
            type="button"
            className={`modal-tab ${activeTab === 'timeline' ? 'active' : ''}`}
            onClick={() => setActiveTab('timeline')}
          >
            <LineChart size={16} />
            <span>Price Timeline & Stats</span>
          </button>
          <button
            type="button"
            className={`modal-tab ${activeTab === 'specs' ? 'active' : ''}`}
            onClick={() => setActiveTab('specs')}
          >
            <Layers size={16} />
            <span>Specifications & Variants</span>
          </button>
        </div>

        {/* Modal Tab Content */}
        <div className="modal-tab-content">
          {pricesLoading ? (
            <div className="tab-loading-state">
              <RefreshCw size={28} className="spinning" />
              <p>Analyzing multi-source prices & history...</p>
            </div>
          ) : (
            <>
              {/* Tab 1: Store Comparison */}
              {activeTab === 'prices' && (
                <div className="tab-pane">
                  <div className="pane-header">
                    <div>
                      <h4 className="section-heading">Cross-Source Price Comparison</h4>
                      <p className="section-subtext">Current listings observed from supported verified shopping sources.</p>
                    </div>
                  </div>
                  <PriceComparisonTable listings={pricesData?.listings || []} />
                </div>
              )}

              {/* Tab 2: Price Timeline */}
              {activeTab === 'timeline' && (
                <div className="tab-pane">
                  <div className="pane-header">
                    <div>
                      <h4 className="section-heading">Historical Price Intelligence</h4>
                      <p className="section-subtext">Empirical timeline observations recorded over time. No projected or fabricated prices.</p>
                    </div>
                  </div>
                  <PriceTimelineChart
                    timeline={pricesData?.timeline || []}
                    summary={pricesData?.summary || null}
                  />
                </div>
              )}

              {/* Tab 3: Specifications & Variants */}
              {activeTab === 'specs' && (
                <div className="tab-pane">
                  {/* Variants Selector */}
                  {product.variants.length > 0 && (
                    <div className="variants-section">
                      <h4 className="section-heading">Canonical Variants ({product.variants.length})</h4>
                      <div className="variants-selector">
                        {product.variants.map((v, idx) => (
                          <button
                            key={v.id}
                            type="button"
                            className={`variant-option-btn ${selectedVariantIndex === idx ? 'active' : ''}`}
                            onClick={() => setSelectedVariantIndex(idx)}
                          >
                            <div className="variant-title">
                              {v.color || ''} {v.storage ? `• ${v.storage}` : ''} {v.size ? `(${v.size})` : ''}
                              {!v.color && !v.storage && !v.size && `Variant #${idx + 1}`}
                            </div>
                            <div className="variant-sub">
                              {v.suggestedPrice ? formatPrice(v.suggestedPrice) : (v.sku || 'Standard SKU')}
                            </div>
                          </button>
                        ))}
                      </div>
                    </div>
                  )}

                  {/* Selected Variant Attributes */}
                  {selectedVariant && (
                    <div className="specs-section">
                      <h4 className="section-heading">Variant Details & Specifications</h4>
                      <div className="specs-meta-grid">
                        {selectedVariant.sku && (
                          <div className="spec-item">
                            <span className="spec-label"><Cpu size={14} /> SKU</span>
                            <span className="spec-val">{selectedVariant.sku}</span>
                          </div>
                        )}
                        {selectedVariant.manufacturerModel && (
                          <div className="spec-item">
                            <span className="spec-label"><Cpu size={14} /> Mfg Model</span>
                            <span className="spec-val">{selectedVariant.manufacturerModel}</span>
                          </div>
                        )}
                        {selectedVariant.color && (
                          <div className="spec-item">
                            <span className="spec-label"><Palette size={14} /> Color</span>
                            <span className="spec-val">{selectedVariant.color}</span>
                          </div>
                        )}
                        {selectedVariant.storage && (
                          <div className="spec-item">
                            <span className="spec-label"><HardDrive size={14} /> Storage</span>
                            <span className="spec-val">{selectedVariant.storage}</span>
                          </div>
                        )}
                        {selectedVariant.size && (
                          <div className="spec-item">
                            <span className="spec-label"><Maximize2 size={14} /> Form / Size</span>
                            <span className="spec-val">{selectedVariant.size}</span>
                          </div>
                        )}
                        {selectedVariant.suggestedPrice && (
                          <div className="spec-item highlight">
                            <span className="spec-label">Benchmark Price</span>
                            <span className="spec-val">{formatPrice(selectedVariant.suggestedPrice)}</span>
                          </div>
                        )}
                      </div>

                      {selectedVariant.attributes && Object.keys(selectedVariant.attributes).length > 0 && (
                        <div className="dynamic-attributes">
                          <h5 className="sub-heading">Hardware & Feature Parameters</h5>
                          <div className="attributes-table">
                            {Object.entries(selectedVariant.attributes).map(([key, value]) => (
                              <div key={key} className="attr-row">
                                <span className="attr-key">
                                  {key.replace(/([A-Z])/g, ' $1').replace(/^./, (s) => s.toUpperCase())}
                                </span>
                                <span className="attr-value">{String(value)}</span>
                              </div>
                            ))}
                          </div>
                        </div>
                      )}
                    </div>
                  )}
                </div>
              )}
            </>
          )}
        </div>

        {/* Lineage & Decision Support Disclaimer */}
        <div className="lineage-footer">
          <div className="lineage-info">
            <CheckCircle size={14} className="text-success" />
            <span>Canonical Data Verified</span>
            <span className="dot">•</span>
            <Clock size={14} />
            <span>Last updated: {new Date(product.lastUpdated).toLocaleDateString()}</span>
          </div>
          <div className="lineage-disclaimer">
            <ShieldAlert size={14} />
            <span>Decision Support Principle: We report observed empirical data with source attribution. We do not make definitive authenticity claims.</span>
          </div>
        </div>
      </div>
    </div>
  );
};
