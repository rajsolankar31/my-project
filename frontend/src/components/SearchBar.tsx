import React, { useState } from 'react';
import { Search, X, Sparkles } from 'lucide-react';

interface SearchBarProps {
  onSearch: (query: string) => void;
  initialQuery?: string;
  loading?: boolean;
}

export const SearchBar: React.FC<SearchBarProps> = ({ onSearch, initialQuery = '', loading = false }) => {
  const [query, setQuery] = useState(initialQuery);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSearch(query);
  };

  const handleClear = () => {
    setQuery('');
    onSearch('');
  };

  return (
    <div className="search-section">
      <div className="search-mode-tabs">
        <button type="button" className="mode-tab active">
          Product Search
        </button>
        <button type="button" className="mode-tab disabled" title="Available in Phase 7">
          <Sparkles size={14} /> Requirement Mode (Phase 7)
        </button>
      </div>

      <form onSubmit={handleSubmit} className="search-form">
        <div className="search-input-wrapper">
          <Search className="search-icon" size={20} />
          <input
            type="text"
            className="search-input"
            placeholder="Search by brand, product name, model (e.g. Sony WH-1000XM5, iPhone, MacBook)..."
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
          {query && (
            <button
              type="button"
              className="clear-button"
              onClick={handleClear}
              aria-label="Clear search"
            >
              <X size={18} />
            </button>
          )}
        </div>
        <button type="submit" className="search-submit-button" disabled={loading}>
          {loading ? 'Searching...' : 'Search'}
        </button>
      </form>
    </div>
  );
};
