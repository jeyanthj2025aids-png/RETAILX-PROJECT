import React, { useState, useEffect, useMemo } from 'react';
import { useOutletContext } from 'react-router-dom';
import { ArrowUpDown, History } from 'lucide-react';
import { movementService } from '../services/movementService';
import { productService } from '../services/productService';
import type { Product, StockMovement, StockMovementFormData, StockMovementResponse } from '../types';
import StockMovementForm from '../components/StockMovementForm';
import Table, { type Column } from '../components/Table';
import { formatDate, formatDateTime, formatNumber } from '../utils/formatters';
import { MOVEMENT_TYPE_COLORS } from '../utils/constants';

interface OutletContextType {
  addToast: (type: 'success' | 'error' | 'warning' | 'info', message: string) => void;
  refreshAlertCount: () => void;
}

type FilterType = 'ALL' | 'PURCHASE' | 'SALE' | 'RETURN' | 'DAMAGE';

export const StockMovements: React.FC = () => {
  const { addToast, refreshAlertCount } = useOutletContext<OutletContextType>();

  const [products, setProducts] = useState<Product[]>([]);
  const [movements, setMovements] = useState<StockMovement[]>([]);
  const [loadingProducts, setLoadingProducts] = useState(true);
  const [loadingMovements, setLoadingMovements] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [sortAsc, setSortAsc] = useState(false);
  const [selectedType, setSelectedType] = useState<FilterType>('ALL');

  const loadData = async () => {
    try {
      setLoadingProducts(true);
      const prods = await productService.getAll(0, 500);
      setProducts(prods);
    } catch {
      addToast('error', 'Failed to load products for stock movement selection.');
    } finally {
      setLoadingProducts(false);
    }
  };

  const loadMovements = async () => {
    try {
      setLoadingMovements(true);
      // Fetch all movements so client-side filtering across all 4 movement types is instantaneous
      const data = await movementService.getAll(0, 1000);
      setMovements(data);
    } catch {
      addToast('error', 'Failed to load stock movements audit history.');
    } finally {
      setLoadingMovements(false);
    }
  };

  useEffect(() => {
    loadData();
    loadMovements();
  }, []);

  const handleSubmitMovement = async (data: StockMovementFormData): Promise<StockMovementResponse> => {
    const res = await movementService.record(data);
    addToast('success', `${data.movementType} movement recorded successfully!`);
    return res;
  };

  const handleRecordSuccess = async () => {
    // Refresh product stock list, movement table and alerts badge
    await loadData();
    await loadMovements();
    setCurrentPage(0);
    refreshAlertCount();
  };

  // Filter & sort movements
  const filteredAndSortedMovements = useMemo(() => {
    let result = movements;
    if (selectedType !== 'ALL') {
      result = result.filter(m => m.movementType === selectedType);
    }
    return [...result].sort((a, b) => {
      const dateA = new Date(a.movementDate).getTime();
      const dateB = new Date(b.movementDate).getTime();
      return sortAsc ? dateA - dateB : dateB - dateA;
    });
  }, [movements, selectedType, sortAsc]);

  // Counts for each movement type
  const counts = useMemo(() => {
    return {
      ALL: movements.length,
      PURCHASE: movements.filter(m => m.movementType === 'PURCHASE').length,
      SALE: movements.filter(m => m.movementType === 'SALE').length,
      RETURN: movements.filter(m => m.movementType === 'RETURN').length,
      DAMAGE: movements.filter(m => m.movementType === 'DAMAGE').length,
    };
  }, [movements]);

  const columns: Column<StockMovement>[] = [
    {
      header: 'Product',
      render: m => (
        <div>
          <div className="font-semibold text-gray-900">{m.productName || `Product #${m.productId}`}</div>
          <div className="text-xs text-gray-500 font-mono">{m.productSku || '—'}</div>
        </div>
      ),
    },
    {
      header: 'Type',
      render: m => {
        const style = MOVEMENT_TYPE_COLORS[m.movementType] || MOVEMENT_TYPE_COLORS.PURCHASE;
        return (
          <span className={`inline-flex items-center gap-1 px-2.5 py-1 rounded-md text-xs font-bold ${style.badge}`}>
            <span className="font-bold">{style.icon}</span>
            <span>{m.movementType}</span>
          </span>
        );
      },
    },
    {
      header: 'Quantity',
      render: m => (
        <span className="font-bold text-gray-900">{formatNumber(m.quantity)} units</span>
      ),
    },
    {
      header: 'Movement Date',
      render: m => <span className="text-gray-700 text-xs font-medium">{formatDate(m.movementDate)}</span>,
    },
    {
      header: 'Recorded Timestamp',
      render: m => <span className="text-gray-500 text-xs">{formatDateTime(m.createdAt)}</span>,
    },
  ];

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Header */}
      <div className="pb-2 border-b border-gray-200">
        <h1 className="text-2xl sm:text-3xl font-extrabold text-gray-900 tracking-tight">
          Stock Movements
        </h1>
        <p className="text-sm text-gray-500 mt-1">
          Record stock transactions (Inflow/Outflow) and view complete immutable ledger history across PURCHASE, SALE, RETURN, and DAMAGE
        </p>
      </div>

      {/* Form Section (Top) */}
      <StockMovementForm
        products={products}
        loadingProducts={loadingProducts}
        onSubmitMovement={handleSubmitMovement}
        onRecordSuccess={handleRecordSuccess}
      />

      {/* Movement History Table (Bottom) */}
      <div className="space-y-4">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-3">
          <div className="flex items-center gap-2">
            <History className="w-5 h-5 text-gray-700" />
            <h2 className="text-lg font-bold text-gray-900">Movement History</h2>
          </div>

          <div className="flex flex-wrap items-center gap-2">
            {/* Movement Type Filter Tabs */}
            <div className="inline-flex bg-gray-100 p-1 rounded-lg text-xs font-semibold">
              <button
                type="button"
                onClick={() => { setSelectedType('ALL'); setCurrentPage(0); }}
                className={`px-3 py-1.5 rounded-md transition-all ${
                  selectedType === 'ALL' ? 'bg-white text-gray-900 shadow-xs' : 'text-gray-600 hover:text-gray-900'
                }`}
              >
                All ({counts.ALL})
              </button>
              <button
                type="button"
                onClick={() => { setSelectedType('PURCHASE'); setCurrentPage(0); }}
                className={`px-3 py-1.5 rounded-md transition-all ${
                  selectedType === 'PURCHASE' ? 'bg-emerald-600 text-white shadow-xs' : 'text-emerald-700 hover:bg-emerald-50'
                }`}
              >
                + PURCHASE ({counts.PURCHASE})
              </button>
              <button
                type="button"
                onClick={() => { setSelectedType('SALE'); setCurrentPage(0); }}
                className={`px-3 py-1.5 rounded-md transition-all ${
                  selectedType === 'SALE' ? 'bg-amber-600 text-white shadow-xs' : 'text-amber-700 hover:bg-amber-50'
                }`}
              >
                - SALE ({counts.SALE})
              </button>
              <button
                type="button"
                onClick={() => { setSelectedType('RETURN'); setCurrentPage(0); }}
                className={`px-3 py-1.5 rounded-md transition-all ${
                  selectedType === 'RETURN' ? 'bg-blue-600 text-white shadow-xs' : 'text-blue-700 hover:bg-blue-50'
                }`}
              >
                + RETURN ({counts.RETURN})
              </button>
              <button
                type="button"
                onClick={() => { setSelectedType('DAMAGE'); setCurrentPage(0); }}
                className={`px-3 py-1.5 rounded-md transition-all ${
                  selectedType === 'DAMAGE' ? 'bg-rose-600 text-white shadow-xs' : 'text-rose-700 hover:bg-rose-50'
                }`}
              >
                - DAMAGE ({counts.DAMAGE})
              </button>
            </div>

            <button
              type="button"
              onClick={() => setSortAsc(!sortAsc)}
              className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-white border border-gray-300 hover:bg-gray-50 text-gray-700 text-xs font-medium rounded-lg shadow-2xs transition-colors"
            >
              <ArrowUpDown className="w-3.5 h-3.5" />
              <span>Sort: {sortAsc ? 'Oldest First' : 'Newest First'}</span>
            </button>
          </div>
        </div>

        <Table
          columns={columns}
          data={filteredAndSortedMovements}
          keyExtractor={item => item.id}
          loading={loadingMovements}
          emptyMessage={`No ${selectedType !== 'ALL' ? selectedType : ''} movements recorded`}
          emptySubtext="Log transactions using the form above to update inventory ledger."
          currentPage={currentPage}
          pageSize={20}
          onPageChange={setCurrentPage}
        />
      </div>
    </div>
  );
};
export default StockMovements;
