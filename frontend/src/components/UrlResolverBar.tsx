import React, { useState } from 'react';
import { Link2, ArrowRight, Check, AlertCircle } from 'lucide-react';

interface UrlResolverBarProps {
  onResolve: (url: string) => void;
  loading: boolean;
  error?: string | null;
}

export const UrlResolverBar: React.FC<UrlResolverBarProps> = ({ onResolve, loading, error }) => {
  const [url, setUrl] = useState('');

  const sampleUrls = [
    { label: 'Sony XM5 · Amazon', url: 'https://www.amazon.in/dp/B09XS7JWHH' },
    { label: 'Sony XM5 · Flipkart', url: 'https://www.flipkart.com/sony-wh-1000xm5/p/ITM82918829' },
    { label: 'iPhone 16 · Amazon', url: 'https://www.amazon.in/dp/B0DGJ9M8XY' },
    { label: 'iPhone 16 · Reliance', url: 'https://www.reliancedigital.in/apple-iphone-16/p/494421980' },
    { label: 'MacBook Pro M3 · Croma', url: 'https://www.croma.com/apple-macbook-pro-14/p/302781' },
    { label: 'Bose QC45 · Amazon', url: 'https://www.amazon.in/dp/B098FKXT8L' },
  ];

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (url.trim()) {
      onResolve(url.trim());
    }
  };

  const handleSampleClick = (sampleUrl: string) => {
    setUrl(sampleUrl);
    onResolve(sampleUrl);
  };

  return (
    <div className="url-resolver-container">
      <form onSubmit={handleSubmit} className="url-resolver-form">
        <div className="url-input-group">
          <Link2 size={18} className="url-icon" />
          <input
            type="text"
            className="url-input"
            placeholder="Paste a product link — Amazon, Flipkart, Croma, or Reliance Digital..."
            value={url}
            onChange={(e) => setUrl(e.target.value)}
          />
        </div>
        <button type="submit" className="resolve-btn" disabled={loading || !url.trim()}>
          {loading ? 'Resolving...' : (
            <>
              <span>Resolve Product</span>
              <ArrowRight size={16} />
            </>
          )}
        </button>
      </form>

      {error && (
        <div className="resolve-error-msg">
          <AlertCircle size={15} />
          <span>{error}</span>
        </div>
      )}

      <div className="sample-links-tray">
        <span className="tray-label">Quick test supported links:</span>
        <div className="sample-pills">
          {sampleUrls.map((s) => (
            <button
              key={s.label}
              type="button"
              className="sample-pill"
              onClick={() => handleSampleClick(s.url)}
              disabled={loading}
            >
              <Check size={12} className="check-icon" />
              <span>{s.label}</span>
            </button>
          ))}
        </div>
      </div>
    </div>
  );
};
