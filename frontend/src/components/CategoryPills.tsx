import React from 'react';

interface CategoryPillsProps {
  categories: string[];
  selectedCategory: string;
  onSelect: (category: string) => void;
}

export const CategoryPills: React.FC<CategoryPillsProps> = ({
  categories,
  selectedCategory,
  onSelect,
}) => {
  return (
    <div className="category-pills">
      <button
        type="button"
        className={`pill ${selectedCategory === 'ALL' ? 'active' : ''}`}
        onClick={() => onSelect('ALL')}
      >
        All Categories
      </button>
      {categories.map((cat) => (
        <button
          key={cat}
          type="button"
          className={`pill ${selectedCategory === cat ? 'active' : ''}`}
          onClick={() => onSelect(cat)}
        >
          {cat}
        </button>
      ))}
    </div>
  );
};
