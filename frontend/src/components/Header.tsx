import React from 'react';
import { Layers, ShieldCheck } from 'lucide-react';

export const Header: React.FC = () => {
  return (
    <header className="site-header">
      <div className="header-container">
        <div className="logo-group">
          <div className="logo-icon">
            <Layers size={24} />
          </div>
          <div>
            <h1 className="logo-title">SaleMate</h1>
            <p className="logo-subtitle">Product Intelligence Platform</p>
          </div>
        </div>

        <div className="header-badge">
          <ShieldCheck size={16} />
          <span>Decision Support System</span>
        </div>
      </div>
    </header>
  );
};
