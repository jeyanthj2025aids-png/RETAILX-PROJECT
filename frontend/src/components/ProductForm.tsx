import React, { useState, useEffect } from 'react';
import { X, PackagePlus, Edit } from 'lucide-react';
import type { Product, ProductFormData } from '../types';
import LoadingSpinner from './LoadingSpinner';

interface ProductFormProps {
  isOpen: boolean;
  productToEdit?: Product | null;
  loading?: boolean;
  serverError?: string | null;
  onClose: () => void;
  onSubmit: (formData: ProductFormData) => Promise<void>;
}

export const ProductForm: React.FC<ProductFormProps> = ({
  isOpen,
  productToEdit,
  loading = false,
  serverError,
  onClose,
  onSubmit,
}) => {
  const [name, setName] = useState('');
  const [sku, setSku] = useState('');
  const [reorderThreshold, setReorderThreshold] = useState<number | string>('');
  const [reorderQuantity, setReorderQuantity] = useState<number | string>('');
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (productToEdit) {
      setName(productToEdit.name);
      setSku(productToEdit.sku);
      setReorderThreshold(productToEdit.reorderThreshold);
      setReorderQuantity(productToEdit.reorderQuantity);
    } else {
      setName('');
      setSku('');
      setReorderThreshold('');
      setReorderQuantity('');
    }
    setErrors({});
  }, [productToEdit, isOpen]);

  if (!isOpen) return null;

  const validate = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!name.trim()) {
      newErrors.name = 'Product name is required';
    }

    if (!sku.trim()) {
      newErrors.sku = 'SKU is required';
    }

    if (reorderThreshold === '' || Number(reorderThreshold) < 0 || isNaN(Number(reorderThreshold))) {
      newErrors.reorderThreshold = 'Reorder threshold must be 0 or greater';
    }

    if (reorderQuantity === '' || Number(reorderQuantity) <= 0 || isNaN(Number(reorderQuantity))) {
      newErrors.reorderQuantity = 'Reorder quantity must be greater than 0';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    await onSubmit({
      name: name.trim(),
      sku: sku.trim().toUpperCase(),
      reorderThreshold: Number(reorderThreshold),
      reorderQuantity: Number(reorderQuantity),
    });
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-xs p-4 animate-fade-in">
      <div className="bg-white rounded-xl shadow-xl border border-gray-200 w-full max-w-lg overflow-hidden transform transition-all">
        {/* Modal Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100 bg-gray-50/50">
          <div className="flex items-center gap-2.5">
            <div className="p-2 rounded-lg bg-blue-50 text-[#0066CC]">
              {productToEdit ? <Edit className="w-5 h-5" /> : <PackagePlus className="w-5 h-5" />}
            </div>
            <div>
              <h3 className="text-lg font-bold text-gray-900">
                {productToEdit ? 'Edit Product' : 'Add New Product'}
              </h3>
              <p className="text-xs text-gray-500">
                {productToEdit ? 'Update product parameters & reorder triggers' : 'Add inventory item to catalog'}
              </p>
            </div>
          </div>
          <button
            type="button"
            onClick={onClose}
            disabled={loading}
            className="text-gray-400 hover:text-gray-600 p-1.5 rounded-lg hover:bg-gray-100 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Modal Body / Form */}
        <form onSubmit={handleSubmit} className="px-6 py-5 space-y-4">
          {serverError && (
            <div className="p-3.5 rounded-lg bg-red-50 border border-red-200 text-red-700 text-sm flex items-start gap-2">
              <span className="font-bold shrink-0">❌</span>
              <span>{serverError}</span>
            </div>
          )}

          {/* Product Name */}
          <div>
            <label className="block text-xs font-bold text-gray-700 uppercase tracking-wider mb-1.5">
              Product Name <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              value={name}
              onChange={e => {
                setName(e.target.value);
                if (errors.name) setErrors(prev => ({ ...prev, name: '' }));
              }}
              placeholder="Enter product name (e.g., Organic Whole Milk)"
              disabled={loading}
              className={`w-full px-3.5 py-2.5 rounded-lg border text-sm text-gray-900 bg-white placeholder-gray-400 focus:outline-none focus:ring-2 transition-all ${
                errors.name
                  ? 'border-red-300 focus:ring-red-200 focus:border-red-500'
                  : 'border-gray-300 focus:ring-blue-100 focus:border-[#0066CC]'
              }`}
            />
            {errors.name && <p className="text-xs text-red-600 mt-1 font-medium">{errors.name}</p>}
          </div>

          {/* SKU */}
          <div>
            <label className="block text-xs font-bold text-gray-700 uppercase tracking-wider mb-1.5">
              SKU (Stock Keeping Unit) <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              value={sku}
              onChange={e => {
                setSku(e.target.value);
                if (errors.sku) setErrors(prev => ({ ...prev, sku: '' }));
              }}
              placeholder="e.g., COKE500, MILK-01"
              disabled={loading}
              className={`w-full px-3.5 py-2.5 rounded-lg border text-sm text-gray-900 bg-white placeholder-gray-400 focus:outline-none focus:ring-2 uppercase tracking-wide transition-all ${
                errors.sku
                  ? 'border-red-300 focus:ring-red-200 focus:border-red-500'
                  : 'border-gray-300 focus:ring-blue-100 focus:border-[#0066CC]'
              }`}
            />
            {errors.sku && <p className="text-xs text-red-600 mt-1 font-medium">{errors.sku}</p>}
          </div>

          {/* Grid: Reorder Threshold & Reorder Quantity */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-bold text-gray-700 uppercase tracking-wider mb-1.5">
                Reorder Threshold <span className="text-red-500">*</span>
              </label>
              <input
                type="number"
                min="0"
                value={reorderThreshold}
                onChange={e => {
                  setReorderThreshold(e.target.value);
                  if (errors.reorderThreshold) setErrors(prev => ({ ...prev, reorderThreshold: '' }));
                }}
                placeholder="20"
                disabled={loading}
                className={`w-full px-3.5 py-2.5 rounded-lg border text-sm text-gray-900 bg-white placeholder-gray-400 focus:outline-none focus:ring-2 transition-all ${
                  errors.reorderThreshold
                    ? 'border-red-300 focus:ring-red-200 focus:border-red-500'
                    : 'border-gray-300 focus:ring-blue-100 focus:border-[#0066CC]'
                }`}
              />
              {errors.reorderThreshold && (
                <p className="text-xs text-red-600 mt-1 font-medium">{errors.reorderThreshold}</p>
              )}
            </div>

            <div>
              <label className="block text-xs font-bold text-gray-700 uppercase tracking-wider mb-1.5">
                Reorder Quantity <span className="text-red-500">*</span>
              </label>
              <input
                type="number"
                min="1"
                value={reorderQuantity}
                onChange={e => {
                  setReorderQuantity(e.target.value);
                  if (errors.reorderQuantity) setErrors(prev => ({ ...prev, reorderQuantity: '' }));
                }}
                placeholder="100"
                disabled={loading}
                className={`w-full px-3.5 py-2.5 rounded-lg border text-sm text-gray-900 bg-white placeholder-gray-400 focus:outline-none focus:ring-2 transition-all ${
                  errors.reorderQuantity
                    ? 'border-red-300 focus:ring-red-200 focus:border-red-500'
                    : 'border-gray-300 focus:ring-blue-100 focus:border-[#0066CC]'
                }`}
              />
              {errors.reorderQuantity && (
                <p className="text-xs text-red-600 mt-1 font-medium">{errors.reorderQuantity}</p>
              )}
            </div>
          </div>

          {/* Modal Footer */}
          <div className="flex items-center justify-end gap-3 pt-4 border-t border-gray-100">
            <button
              type="button"
              onClick={onClose}
              disabled={loading}
              className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 focus:outline-none transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="px-5 py-2 text-sm font-medium text-white bg-[#0066CC] hover:bg-[#1E40AF] disabled:opacity-50 disabled:cursor-not-allowed rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-300 transition-colors flex items-center gap-2"
            >
              {loading && <LoadingSpinner size="sm" />}
              <span>Save</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
export default ProductForm;
