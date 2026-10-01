import React, { useState, useMemo } from 'react';
import type { PricePoint, PriceSummary } from '../types/product';
import { TrendingDown, TrendingUp, Minus, Calendar, Info } from 'lucide-react';

interface PriceTimelineChartProps {
  timeline: PricePoint[];
  summary: PriceSummary | null;
}

export const PriceTimelineChart: React.FC<PriceTimelineChartProps> = ({ timeline, summary }) => {
  const [activePoint, setActivePoint] = useState<PricePoint | null>(null);
  const [timeFilter, setTimeFilter] = useState<'ALL' | '60D' | '30D'>('ALL');

  const filteredTimeline = useMemo(() => {
    if (!timeline || timeline.length === 0) return [];
    if (timeFilter === 'ALL') return timeline;

    const days = timeFilter === '30D' ? 30 : 60;
    const cutoff = Date.now() - days * 24 * 60 * 60 * 1000;
    const filtered = timeline.filter((p) => new Date(p.timestamp).getTime() >= cutoff);
    return filtered.length > 0 ? filtered : timeline;
  }, [timeline, timeFilter]);

  const formatPrice = (val: number | null | undefined) => {
    if (val === null || val === undefined) return '—';
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 0,
    }).format(val);
  };

  // SVG Chart Geometry Calculations
  const chartData = useMemo(() => {
    if (filteredTimeline.length === 0) return null;

    const prices = filteredTimeline.map((p) => p.price);
    const minPrice = Math.min(...prices);
    const maxPrice = Math.max(...prices);

    // Add padding to range
    const priceRange = maxPrice === minPrice ? 1000 : maxPrice - minPrice;
    const yMin = Math.max(0, minPrice - priceRange * 0.15);
    const yMax = maxPrice + priceRange * 0.15;

    const width = 640;
    const height = 240;
    const padX = 50;
    const padY = 30;

    const points = filteredTimeline.map((p, idx) => {
      const x = padX + (idx / Math.max(1, filteredTimeline.length - 1)) * (width - 2 * padX);
      const y = height - padY - ((p.price - yMin) / (yMax - yMin)) * (height - 2 * padY);
      return { x, y, point: p };
    });

    const polylineStr = points.map((pt) => `${pt.x},${pt.y}`).join(' ');

    // Area string
    const firstX = points[0].x;
    const lastX = points[points.length - 1].x;
    const bottomY = height - padY;
    const areaStr = `${polylineStr} ${lastX},${bottomY} ${firstX},${bottomY}`;

    // Average line Y
    let avgY: number | null = null;
    if (summary?.averageObserved) {
      avgY = height - padY - ((summary.averageObserved - yMin) / (yMax - yMin)) * (height - 2 * padY);
    }

    return {
      width,
      height,
      points,
      polylineStr,
      areaStr,
      yMin,
      yMax,
      avgY,
      minPrice,
      maxPrice,
    };
  }, [filteredTimeline, summary]);

  if (!timeline || timeline.length === 0) {
    return (
      <div className="empty-timeline-box">
        <Info size={20} />
        <p>No historical price observations available for this product.</p>
      </div>
    );
  }

  const getPositionBadge = (pos?: string, pct?: number) => {
    if (pos === 'AT_LOWEST') {
      return (
        <span className="price-status-badge lowest">
          <TrendingDown size={14} /> All-Time Lowest Observed Price
        </span>
      );
    }
    if (pos === 'BELOW_AVERAGE') {
      return (
        <span className="price-status-badge good">
          <TrendingDown size={14} /> Below Average ({pct ? `${Math.abs(pct)}%` : 'Good Deal'})
        </span>
      );
    }
    if (pos === 'ABOVE_AVERAGE') {
      return (
        <span className="price-status-badge high">
          <TrendingUp size={14} /> Above Average ({pct ? `+${pct}%` : 'Higher Price'})
        </span>
      );
    }
    return (
      <span className="price-status-badge neutral">
        <Minus size={14} /> Near Observed Average
      </span>
    );
  };

  return (
    <div className="price-timeline-container">
      {/* Summary Stat Cards */}
      {summary && (
        <div className="timeline-stat-cards">
          <div className="stat-card highlight">
            <span className="stat-label">Current Observed</span>
            <span className="stat-value">{formatPrice(summary.currentObserved)}</span>
            <div className="stat-badge-row">{getPositionBadge(summary.pricePosition, summary.percentDiffFromAverage)}</div>
          </div>
          <div className="stat-card">
            <span className="stat-label">Lowest Observed</span>
            <span className="stat-value">{formatPrice(summary.lowestObserved)}</span>
            <span className="stat-sub">Historical trough</span>
          </div>
          <div className="stat-card">
            <span className="stat-label">Average Observed</span>
            <span className="stat-value">{formatPrice(summary.averageObserved)}</span>
            <span className="stat-sub">Across {summary.observationCount} observations</span>
          </div>
          <div className="stat-card">
            <span className="stat-label">Highest Observed</span>
            <span className="stat-value">{formatPrice(summary.highestObserved)}</span>
            <span className="stat-sub">Historical peak</span>
          </div>
        </div>
      )}

      {/* Chart Header & Controls */}
      <div className="chart-controls-bar">
        <div className="chart-title-group">
          <Calendar size={16} />
          <span>Historical Price Observations Timeline</span>
        </div>
        <div className="chart-filter-btns">
          <button
            type="button"
            className={`filter-btn ${timeFilter === '30D' ? 'active' : ''}`}
            onClick={() => setTimeFilter('30D')}
          >
            30 Days
          </button>
          <button
            type="button"
            className={`filter-btn ${timeFilter === '60D' ? 'active' : ''}`}
            onClick={() => setTimeFilter('60D')}
          >
            60 Days
          </button>
          <button
            type="button"
            className={`filter-btn ${timeFilter === 'ALL' ? 'active' : ''}`}
            onClick={() => setTimeFilter('ALL')}
          >
            All Time
          </button>
        </div>
      </div>

      {/* Interactive SVG Chart */}
      {chartData && (
        <div className="svg-chart-wrapper">
          <svg
            viewBox={`0 0 ${chartData.width} ${chartData.height}`}
            className="price-svg-chart"
            preserveAspectRatio="none"
          >
            <defs>
              <linearGradient id="priceGradient" x1="0" y1="0" x2="0" y2="1">
                <stop offset="0%" stopColor="#2563eb" stopOpacity="0.25" />
                <stop offset="100%" stopColor="#2563eb" stopOpacity="0.0" />
              </linearGradient>
            </defs>

            {/* Average Reference Line */}
            {chartData.avgY !== null && (
              <g className="avg-ref-line">
                <line
                  x1={40}
                  y1={chartData.avgY}
                  x2={chartData.width - 20}
                  y2={chartData.avgY}
                  stroke="#94a3b8"
                  strokeDasharray="4 4"
                  strokeWidth="1.5"
                />
                <text
                  x={chartData.width - 25}
                  y={chartData.avgY - 6}
                  textAnchor="end"
                  fontSize="10"
                  fill="#64748b"
                >
                  Avg: {formatPrice(summary?.averageObserved)}
                </text>
              </g>
            )}

            {/* Area Fill */}
            <polygon points={chartData.areaStr} fill="url(#priceGradient)" />

            {/* Price Line */}
            <polyline
              points={chartData.polylineStr}
              fill="none"
              stroke="#2563eb"
              strokeWidth="3"
              strokeLinecap="round"
              strokeLinejoin="round"
            />

            {/* Price Data Points */}
            {chartData.points.map((pt, i) => (
              <g
                key={i}
                className="chart-data-point"
                onMouseEnter={() => setActivePoint(pt.point)}
                onMouseLeave={() => setActivePoint(null)}
              >
                <circle
                  cx={pt.x}
                  cy={pt.y}
                  r="5"
                  className={`dot-circle ${activePoint === pt.point ? 'active-dot' : ''}`}
                />
                <circle
                  cx={pt.x}
                  cy={pt.y}
                  r="14"
                  fill="transparent"
                  style={{ cursor: 'pointer' }}
                />
              </g>
            ))}
          </svg>

          {/* Interactive Tooltip on Hover */}
          {activePoint && (
            <div className="chart-hover-tooltip">
              <div className="tooltip-price">{formatPrice(activePoint.price)}</div>
              <div className="tooltip-date">{activePoint.formattedDate}</div>
              <div className="tooltip-source">
                {activePoint.sourceName} &bull; {activePoint.sellerName}
              </div>
            </div>
          )}

          {/* X-Axis Dates */}
          <div className="chart-x-axis">
            <span>{filteredTimeline[0].formattedDate}</span>
            {filteredTimeline.length > 2 && (
              <span>{filteredTimeline[Math.floor(filteredTimeline.length / 2)].formattedDate}</span>
            )}
            <span>{filteredTimeline[filteredTimeline.length - 1].formattedDate}</span>
          </div>
        </div>
      )}
    </div>
  );
};
