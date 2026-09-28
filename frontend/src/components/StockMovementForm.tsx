import React, { useState, useEffect } from 'react';
import { ArrowLeftRight, CheckCircle2, AlertTriangle, Info, XCircle } from 'lucide-react';
import type { Product, StockMovementFormData, StockMovementResponse } from '../types';
import LoadingSpinner from './LoadingSpinner';

interface StockMovementFormProps {
  products: Product[];
  loadingProducts?: boolean;
  onRecordSuccess: (response: StockMovementResponse) => void;
  onSubmitMovement: (data: StockMovementFormData) => Promise<StockMovementResponse>;
}

export const StockMovementForm: React.FC<StockMovementFormProps> = ({
  products,
  loadingProducts = false,
  onRecordSuccess,
  onSubmitMovement,
}) => {
  const today = new Date().toISOString().split('T')[0];

  const [productId, setProductId] = useState<number | string>('');
  const [movementType, setMovementType] = useState<'PURCHASE' | 'SALE' | 'RETURN' | 'DAMAGE' | ''>('');
  const [quantity, setQuantity] = useState<number | string>('');
  const [movementDate, setMovementDate] = useState<string>(today);

  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [apiError, setApiError] = useState<string | null>(null);
  const [lastResponse, setLastResponse] = useState<StockMovementResponse | null>(null);

  const selectedProduct = products.find(p => p.id === Number(productId));

  // Reset notifications on form change
  useEffect(() => {
    setApiError(null);
  }, [productId, movementType, quantity, movementDate]);

  const validate = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!productId) {
      newErrors.productId = 'Product is required';
    }

    if (!movementType) {
      newErrors.movementType = 'Movement type is required';
    }

    if (quantity === '' || Number(quantity) <= 0 || isNaN(Number(quantity))) {
      newErrors.quantity = 'Quantity must be greater than 0';
    }

    if (!movementDate) {
      newErrors.movementDate = 'Date is required';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    setLoading(true);
    setApiError(null);
    setLastResponse(null);

    try {
      const response = await onSubmitMovement({
        productId: Number(productId),
        movementType: movementType as 'PURCHASE' | 'SALE' | 'RETURN' | 'DAMAGE',
        quantity: Number(quantity),
        movementDate,
      });

      setLastResponse(response);
      onRecordSuccess(response);

      // Clear inputs
      setQuantity('');
    } catch (err: unknown) {
      // API error handled and displayed in red box
      if (typeof err === 'object' && err !== null && 'response' in err) {
        const axErr = err as { response?: { data?: { message?: string } } };
        setApiError(axErr.response?.data?.message || 'Failed to record stock movement');
      } else if (err instanceof Error) {
        setApiError(err.message);
      } else {
        setApiError('An unexpected error occurred while processing movement');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-white rounded-xl border border-gray-200 p-6 shadow-xs">
      <div className="flex items-center gap-2.5 pb-4 mb-5 border-b border-gray-100">
        <div className="p-2 rounded-lg bg-blue-50 text-[#0066CC]">
          <ArrowLeftRight className="w-5 h-5" />
        </div>
        <div>
          <h2 className="text-lg font-bold text-gray-900">Record Stock Movement</h2>
          <p className="text-xs text-gray-500">
            Log inventory transactions (PURCHASE, SALE, RETURN, DAMAGE) and update live balance
          </p>
        </div>
      </div>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          {/* Product Select */}
          <div>
            <label className="block text-xs font-bold text-gray-700 uppercase tracking-wider mb-1.5">
              Product <span className="text-red-500">*</span>
            </label>
            <select
              value={productId}
              onChange={e => {
                setProductId(e.target.value);
                if (errors.productId) setErrors(prev => ({ ...prev, productId: '' }));
              }}
              disabled={loading || loadingProducts}
              className={`w-full px-3.5 py-2.5 rounded-lg border text-sm text-gray-900 bg-white focus:outline-none focus:ring-2 transition-all ${
                errors.productId
                  ? 'border-red-300 focus:ring-red-200 focus:border-red-500'
                  : 'border-gray-300 focus:ring-blue-100 focus:border-[#0066CC]'
              }`}
            >
              <option value="">Select a product</option>
              {products.map(product => (
                <option key={product.id} value={product.id}>
                  {product.name} ({product.sku}) — Stock: {product.currentStock ?? '0'}
                </option>
              ))}
            </select>
            {errors.productId && (
              <p className="text-xs text-red-600 mt-1 font-medium">{errors.productId}</p>
            )}
          </div>

          {/* Movement Type Select */}
          <div>
            <label className="block text-xs font-bold text-gray-700 uppercase tracking-wider mb-1.5">
              Movement Type <span className="text-red-500">*</span>
            </label>
            <select
              value={movementType}
              onChange={e => {
                setMovementType(e.target.value as 'PURCHASE' | 'SALE' | 'RETURN' | 'DAMAGE' | '');
                if (errors.movementType) setErrors(prev => ({ ...prev, movementType: '' }));
              }}
              disabled={loading}
              className={`w-full px-3.5 py-2.5 rounded-lg border text-sm text-gray-900 bg-white focus:outline-none focus:ring-2 transition-all ${
                errors.movementType
                  ? 'border-red-300 focus:ring-red-200 focus:border-red-500'
                  : 'border-gray-300 focus:ring-blue-100 focus:border-[#0066CC]'
              }`}
            >
              <option value="">Select movement type</option>
              <option value="PURCHASE">PURCHASE (+ Stock)</option>
              <option value="SALE">SALE (- Stock)</option>
              <option value="RETURN">RETURN (+ Stock)</option>
              <option value="DAMAGE">DAMAGE (- Stock)</option>
            </select>
            {errors.movementType && (
              <p className="text-xs text-red-600 mt-1 font-medium">{errors.movementType}</p>
            )}
          </div>

          {/* Quantity */}
          <div>
            <label className="block text-xs font-bold text-gray-700 uppercase tracking-wider mb-1.5">
              Quantity <span className="text-red-500">*</span>
            </label>
            <input
              type="number"
              min="1"
              value={quantity}
              onChange={e => {
                setQuantity(e.target.value);
                if (errors.quantity) setErrors(prev => ({ ...prev, quantity: '' }));
              }}
              placeholder="Enter quantity (e.g., 25)"
              disabled={loading}
              className={`w-full px-3.5 py-2.5 rounded-lg border text-sm text-gray-900 bg-white placeholder-gray-400 focus:outline-none focus:ring-2 transition-all ${
                errors.quantity
                  ? 'border-red-300 focus:ring-red-200 focus:border-red-500'
                  : 'border-gray-300 focus:ring-blue-100 focus:border-[#0066CC]'
              }`}
            />
            {errors.quantity && (
              <p className="text-xs text-red-600 mt-1 font-medium">{errors.quantity}</p>
            )}
          </div>

          {/* Date Picker */}
          <div>
            <label className="block text-xs font-bold text-gray-700 uppercase tracking-wider mb-1.5">
              Date <span className="text-red-500">*</span>
            </label>
            <input
              type="date"
              value={movementDate}
              onChange={e => {
                setMovementDate(e.target.value);
                if (errors.movementDate) setErrors(prev => ({ ...prev, movementDate: '' }));
              }}
              disabled={loading}
              className={`w-full px-3.5 py-2.5 rounded-lg border text-sm text-gray-900 bg-white focus:outline-none focus:ring-2 transition-all ${
                errors.movementDate
                  ? 'border-red-300 focus:ring-red-200 focus:border-red-500'
                  : 'border-gray-300 focus:ring-blue-100 focus:border-[#0066CC]'
              }`}
            />
            {errors.movementDate && (
              <p className="text-xs text-red-600 mt-1 font-medium">{errors.movementDate}</p>
            )}
          </div>
        </div>

        {/* Action Button */}
        <div className="flex justify-end pt-2">
          <button
            type="submit"
            disabled={loading}
            className="w-full sm:w-auto px-6 py-2.5 bg-[#0066CC] hover:bg-[#1E40AF] disabled:opacity-50 disabled:cursor-not-allowed text-white text-sm font-semibold rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-300 transition-colors flex items-center justify-center gap-2"
          >
            {loading && <LoadingSpinner size="sm" />}
            <span>Record Movement</span>
          </button>
        </div>
      </form>

      {/* Result Status Message Boxes */}
      <div className="mt-5 space-y-3">
        {/* Error box */}
        {apiError && (
          <div className="p-4 rounded-xl bg-red-50 border border-red-200 text-red-800 text-sm flex items-start gap-3 animate-fade-in">
            <XCircle className="w-5 h-5 text-red-600 shrink-0 mt-0.5" />
            <div>
              <h5 className="font-bold">Transaction Rejected</h5>
              <p className="mt-0.5">{apiError}</p>
            </div>
          </div>
        )}

        {/* Success updated stock box */}
        {lastResponse && (
          <div className="space-y-2.5 animate-fade-in">
            <div className="p-4 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-900 text-sm flex items-start gap-3">
              <CheckCircle2 className="w-5 h-5 text-emerald-600 shrink-0 mt-0.5" />
              <div>
                <h5 className="font-bold">Stock Movement Logged Successfully</h5>
                <p className="mt-0.5 font-semibold text-emerald-800">
                  Current Stock: {lastResponse.currentStock} units
                </p>
              </div>
            </div>

            {/* Alert created orange box */}
            {lastResponse.reorderAlertCreated && (
              <div className="p-4 rounded-xl bg-amber-50 border border-amber-200 text-amber-900 text-sm flex items-start gap-3">
                <AlertTriangle className="w-5 h-5 text-amber-600 shrink-0 mt-0.5" />
                <div>
                  <h5 className="font-bold">Reorder Alert Created</h5>
                  <p className="mt-0.5">
                    ⚠ Reorder alert created for{' '}
                    <span className="font-semibold">{selectedProduct?.name || 'Product'}</span>.
                    Current stock: <span className="font-bold">{lastResponse.currentStock} units</span>.
                    Recommended order: <span className="font-bold">{selectedProduct?.reorderQuantity || 100} units</span>.
                  </p>
                </div>
              </div>
            )}

            {/* Alert exists already blue box */}
            {!lastResponse.reorderAlertCreated &&
              selectedProduct &&
              lastResponse.currentStock <= selectedProduct.reorderThreshold && (
                <div className="p-4 rounded-xl bg-blue-50 border border-blue-200 text-blue-900 text-sm flex items-start gap-3">
                  <Info className="w-5 h-5 text-blue-600 shrink-0 mt-0.5" />
                  <div>
                    <h5 className="font-bold">Existing Alert Active</h5>
                    <p className="mt-0.5">
                      ℹ Product stock is below threshold ({selectedProduct.reorderThreshold}), and already has an active OPEN reorder alert (duplicate alert prevented).
                    </p>
                  </div>
                </div>
              )}
          </div>
        )}
      </div>
    </div>
  );
};
export default StockMovementForm;
