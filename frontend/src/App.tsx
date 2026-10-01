import { useState, useEffect, useMemo } from 'react';
import { Header } from './components/Header';
import { SearchBar } from './components/SearchBar';
import { UrlResolverBar } from './components/UrlResolverBar';
import { CategoryPills } from './components/CategoryPills';
import { ProductCard } from './components/ProductCard';
import { ProductModal } from './components/ProductModal';
import { searchProducts, getProductById, resolveProductUrl } from './services/api';
import type { ProductSummary, ProductDetail, ProductPricesResponse } from './types/product';
import { AlertCircle, RefreshCw, Sparkles, Database, Link2, Search, CheckCircle2 } from 'lucide-react';
import './App.css';

export function App() {
  const [products, setProducts] = useState<ProductSummary[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [selectedCategory, setSelectedCategory] = useState<string>('ALL');

  // Search vs URL Resolver Mode
  const [searchMode, setSearchMode] = useState<'SEARCH' | 'URL'>('SEARCH');
  const [urlResolveLoading, setUrlResolveLoading] = useState<boolean>(false);
  const [urlResolveError, setUrlResolveError] = useState<string | null>(null);
  const [resolvedSuccessMsg, setResolvedSuccessMsg] = useState<string | null>(null);

  // Detail Modal State
  const [selectedProduct, setSelectedProduct] = useState<ProductDetail | null>(null);
  const [selectedPrices, setSelectedPrices] = useState<ProductPricesResponse | null>(null);
  const [detailLoading, setDetailLoading] = useState<boolean>(false);

  const fetchProducts = async (query: string = '') => {
    try {
      setLoading(true);
      setError(null);
      const data = await searchProducts(query);
      setProducts(data);
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : 'Failed to connect to backend API';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProducts('');
  }, []);

  const handleSearch = (query: string) => {
    setSearchQuery(query);
    fetchProducts(query);
  };

  const handleOpenDetail = async (id: number) => {
    try {
      setDetailLoading(true);
      const detail = await getProductById(id);
      setSelectedProduct(detail);
      setSelectedPrices(null); // will load inside modal
    } catch (err: unknown) {
      alert(err instanceof Error ? err.message : 'Could not load product details.');
    } finally {
      setDetailLoading(false);
    }
  };

  const handleResolveUrl = async (url: string) => {
    try {
      setUrlResolveLoading(true);
      setUrlResolveError(null);
      setResolvedSuccessMsg(null);

      const res = await resolveProductUrl(url);
      if (res.resolved && res.product) {
        setResolvedSuccessMsg(`Matched: ${res.product.name} (${res.sourceName})`);
        setSelectedProduct(res.product);
        setSelectedPrices(res.prices);
      } else {
        setUrlResolveError(res.message || 'Could not resolve product link.');
      }
    } catch (err: unknown) {
      setUrlResolveError(err instanceof Error ? err.message : 'Failed to resolve URL.');
    } finally {
      setUrlResolveLoading(false);
    }
  };

  const handleCloseDetail = () => {
    setSelectedProduct(null);
    setSelectedPrices(null);
  };

  // Distinct categories
  const categories = useMemo(() => {
    const set = new Set<string>();
    products.forEach((p) => {
      if (p.category) set.add(p.category);
    });
    return Array.from(set);
  }, [products]);

  // Filtered by category
  const displayedProducts = useMemo(() => {
    if (selectedCategory === 'ALL') return products;
    return products.filter((p) => p.category.toLowerCase() === selectedCategory.toLowerCase());
  }, [products, selectedCategory]);

  return (
    <div className="app-layout">
      <Header />

      <main className="main-content">
        <div className="hero-section">
          <div className="hero-content">
            <span className="hero-tag">
              <Sparkles size={14} /> Phase 2 & 3: Multi-Source Listings & Price Intelligence
            </span>
            <h2 className="hero-title">
              Cross-Source Comparisons & <span className="highlight-text">Price History</span>
            </h2>
            <p className="hero-description">
              Compare observed prices across Amazon India, Flipkart, Croma, and Reliance Digital. Inspect timestamped price trends and structured hardware specs.
            </p>
          </div>

          {/* Mode Switcher */}
          <div className="main-mode-toggle">
            <button
              type="button"
              className={`mode-btn ${searchMode === 'SEARCH' ? 'active' : ''}`}
              onClick={() => setSearchMode('SEARCH')}
            >
              <Search size={15} />
              <span>Search Product Catalog</span>
            </button>
            <button
              type="button"
              className={`mode-btn ${searchMode === 'URL' ? 'active' : ''}`}
              onClick={() => setSearchMode('URL')}
            >
              <Link2 size={15} />
              <span>Resolve Store Link (Amazon / Flipkart / Croma)</span>
            </button>
          </div>

          {/* Mode 1: Search */}
          {searchMode === 'SEARCH' && (
            <>
              <SearchBar onSearch={handleSearch} initialQuery={searchQuery} loading={loading} />
              <CategoryPills
                categories={categories}
                selectedCategory={selectedCategory}
                onSelect={setSelectedCategory}
              />
            </>
          )}

          {/* Mode 2: URL Resolver */}
          {searchMode === 'URL' && (
            <UrlResolverBar
              onResolve={handleResolveUrl}
              loading={urlResolveLoading}
              error={urlResolveError}
            />
          )}

          {resolvedSuccessMsg && (
            <div className="resolved-success-pill">
              <CheckCircle2 size={16} />
              <span>{resolvedSuccessMsg}</span>
            </div>
          )}
        </div>

        {/* Results Section */}
        <section className="results-container">
          <div className="results-header">
            <div className="results-count">
              <Database size={16} />
              <span>
                Showing <strong>{displayedProducts.length}</strong> {displayedProducts.length === 1 ? 'product' : 'products'}
                {searchQuery ? ` matching "${searchQuery}"` : ''}
              </span>
            </div>

            <button
              type="button"
              className="refresh-btn"
              onClick={() => fetchProducts(searchQuery)}
              disabled={loading}
              title="Refresh results from backend"
            >
              <RefreshCw size={14} className={loading ? 'spinning' : ''} />
              <span>Refresh</span>
            </button>
          </div>

          {error && (
            <div className="error-banner">
              <AlertCircle size={20} />
              <div>
                <h4>Connection Notice</h4>
                <p>{error}. Please ensure Spring Boot backend is running on <code>http://localhost:8080</code>.</p>
              </div>
            </div>
          )}

          {loading && products.length === 0 ? (
            <div className="loading-state">
              <RefreshCw size={32} className="spinning" />
              <p>Fetching canonical products & price feeds from database...</p>
            </div>
          ) : displayedProducts.length === 0 ? (
            <div className="empty-state">
              <Database size={48} className="empty-icon" />
              <h3>No products found</h3>
              <p>No products matched your search or category filter. Try a broader search term like &ldquo;Sony&rdquo;, &ldquo;Apple&rdquo;, or &ldquo;Headphones&rdquo;.</p>
              <button
                type="button"
                className="reset-search-btn"
                onClick={() => {
                  setSearchQuery('');
                  setSelectedCategory('ALL');
                  fetchProducts('');
                }}
              >
                Reset Search
              </button>
            </div>
          ) : (
            <div className="products-grid">
              {displayedProducts.map((product) => (
                <ProductCard
                  key={product.id}
                  product={product}
                  onSelect={handleOpenDetail}
                />
              ))}
            </div>
          )}
        </section>
      </main>

      {/* Product Detail & Intelligence Modal */}
      {selectedProduct && (
        <ProductModal
          product={selectedProduct}
          initialPrices={selectedPrices}
          onClose={handleCloseDetail}
        />
      )}

      {detailLoading && (
        <div className="loading-overlay">
          <RefreshCw size={32} className="spinning" />
        </div>
      )}

      <footer className="site-footer">
        <p>SaleMate Product Intelligence Platform &bull; Multi-Source Pricing &bull; Spring Boot 3 &bull; React &bull; PostgreSQL</p>
      </footer>
    </div>
  );
}

export default App;
