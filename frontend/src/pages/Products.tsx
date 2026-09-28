import React, { useState, useEffect, useMemo } from 'react';
import { useOutletContext } from 'react-router-dom';
import { Plus, Eye, Edit2, Trash2, Package, X, Search, Sparkles } from 'lucide-react';
import { productService } from '../services/productService';
import type { Product, ProductFormData } from '../types';
import Table, { type Column } from '../components/Table';
import Badge from '../components/Badge';
import ProductForm from '../components/ProductForm';
import ConfirmDialog from '../components/ConfirmDialog';
import { formatDateTime, formatNumber } from '../utils/formatters';
import { searchProducts } from '../utils/searchUtils';

interface OutletContextType {
  addToast: (type: 'success' | 'error' | 'warning' | 'info', message: string) => void;
  refreshAlertCount: () => void;
}

export const Products: React.FC = () => {
  const { addToast, refreshAlertCount } = useOutletContext<OutletContextType>();

  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [searchQuery, setSearchQuery] = useState('');

  // Modals state
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [productToEdit, setProductToEdit] = useState<Product | null>(null);
  const [productToDelete, setProductToDelete] = useState<Product | null>(null);
  const [viewProduct, setViewProduct] = useState<Product | null>(null);
  const [formSubmitting, setFormSubmitting] = useState(false);
  const [serverFormError, setServerFormError] = useState<string | null>(null);
  const [deleting, setDeleting] = useState(false);

  const fetchProducts = async () => {
    try {
      setLoading(true);
      // Fetch all products so database-only search and ranking work seamlessly on client
      const data = await productService.getAll(0, 500);
      setProducts(data);
    } catch {
      addToast('error', 'Failed to load products list from catalog.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProducts();
  }, []);

  // Compute search & similar product recommendations from real database products
  const { displayedProducts, isSimilarSuggestion, totalCount } = useMemo(() => {
    const result = searchProducts(products, searchQuery);
    return {
      displayedProducts: result.directMatches,
      isSimilarSuggestion: result.isSimilarSuggestion,
      totalCount: result.directMatches.length,
    };
  }, [products, searchQuery]);

  // Reset page to 0 when search query changes
  useEffect(() => {
    setCurrentPage(0);
  }, [searchQuery]);

  const handleOpenAddModal = () => {
    setProductToEdit(null);
    setServerFormError(null);
    setIsFormOpen(true);
  };

  const handleOpenEditModal = (product: Product) => {
    setProductToEdit(product);
    setServerFormError(null);
    setIsFormOpen(true);
  };

  const handleFormSubmit = async (formData: ProductFormData) => {
    setFormSubmitting(true);
    setServerFormError(null);
    try {
      if (productToEdit) {
        await productService.update(productToEdit.id, formData);
        addToast('success', `Product "${formData.name}" updated successfully.`);
      } else {
        await productService.create(formData);
        addToast('success', `Product "${formData.name}" created successfully.`);
      }
      setIsFormOpen(false);
      await fetchProducts();
      refreshAlertCount();
    } catch (err: unknown) {
      if (typeof err === 'object' && err !== null && 'response' in err) {
        const axErr = err as { response?: { data?: { message?: string } } };
        setServerFormError(axErr.response?.data?.message || 'Error saving product');
      } else if (err instanceof Error) {
        setServerFormError(err.message);
      } else {
        setServerFormError('Failed to save product');
      }
    } finally {
      setFormSubmitting(false);
    }
  };

  const handleDeleteConfirm = async () => {
    if (!productToDelete) return;
    try {
      setDeleting(true);
      await productService.delete(productToDelete.id);
      addToast('success', `Product "${productToDelete.name}" deleted successfully.`);
      setProductToDelete(null);
      await fetchProducts();
      refreshAlertCount();
    } catch {
      addToast('error', 'Failed to delete product.');
    } finally {
      setDeleting(false);
    }
  };

  const getStatusBadge = (product: Product) => {
    const stock = product.currentStock ?? 0;
    const threshold = product.reorderThreshold ?? 0;

    if (stock === 0) {
      return <Badge variant="red" dot>OUT_OF_STOCK</Badge>;
    }
    if (stock <= threshold) {
      return <Badge variant="orange" dot>LOW_STOCK</Badge>;
    }
    return <Badge variant="green" dot>IN_STOCK</Badge>;
  };

  const columns: Column<Product>[] = [
    {
      header: 'Product',
      render: p => (
        <div>
          <div className="font-semibold text-gray-900">{p.name}</div>
          <div className="text-xs text-gray-400">ID #{p.id}</div>
        </div>
      ),
    },
    {
      header: 'SKU',
      render: p => (
        <span className="font-mono text-xs px-2 py-0.5 rounded bg-gray-100 text-gray-800 font-medium">
          {p.sku}
        </span>
      ),
    },
    {
      header: 'Current Stock',
      render: p => {
        const stock = p.currentStock ?? 0;
        const threshold = p.reorderThreshold ?? 0;
        return (
          <span className={`font-bold ${stock <= threshold ? 'text-amber-600' : 'text-gray-900'}`}>
            {formatNumber(stock)} units
          </span>
        );
      },
    },
    {
      header: 'Threshold',
      render: p => <span className="text-gray-600 font-medium">{formatNumber(p.reorderThreshold)} units</span>,
    },
    {
      header: 'Reorder Qty',
      render: p => (
        <span className="font-semibold text-blue-700 bg-blue-50 px-2 py-0.5 rounded border border-blue-200">
          +{formatNumber(p.reorderQuantity)} units
        </span>
      ),
    },
    {
      header: 'Added On',
      render: p => (
        <span className="text-xs text-gray-500 font-medium whitespace-nowrap">
          {p.createdAt ? formatDateTime(p.createdAt) : 'Not available'}
        </span>
      ),
    },
    {
      header: 'Status',
      render: p => getStatusBadge(p),
    },
    {
      header: 'Actions',
      render: p => (
        <div className="flex items-center gap-1.5">
          <button
            type="button"
            onClick={() => setViewProduct(p)}
            className="p-1.5 text-gray-600 hover:text-blue-600 hover:bg-blue-50 rounded transition-colors"
            title="View Details"
          >
            <Eye className="w-4 h-4" />
          </button>
          <button
            type="button"
            onClick={() => handleOpenEditModal(p)}
            className="p-1.5 text-gray-600 hover:text-blue-600 hover:bg-blue-50 rounded transition-colors"
            title="Edit Product"
          >
            <Edit2 className="w-4 h-4" />
          </button>
          <button
            type="button"
            onClick={() => setProductToDelete(p)}
            className="p-1.5 text-gray-600 hover:text-red-600 hover:bg-red-50 rounded transition-colors"
            title="Delete Product"
          >
            <Trash2 className="w-4 h-4" />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-2 border-b border-gray-200">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-gray-900 tracking-tight">Products</h1>
          <p className="text-sm text-gray-500 mt-1">
            Manage product catalog, reorder threshold boundaries, and live stock positions
          </p>
        </div>

        <button
          type="button"
          onClick={handleOpenAddModal}
          className="px-4 py-2.5 bg-[#0066CC] hover:bg-[#1E40AF] text-white text-sm font-semibold rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-300 transition-colors flex items-center gap-2 self-start sm:self-auto"
        >
          <Plus className="w-4 h-4" />
          <span>Add Product</span>
        </button>
      </div>

      {/* Search Input Bar */}
      <div className="bg-white p-4 rounded-xl shadow-xs border border-gray-200 space-y-3">
        <div className="relative flex items-center">
          <Search className="w-4 h-4 text-gray-400 absolute left-3 pointer-events-none" />
          <input
            type="text"
            value={searchQuery}
            onChange={e => setSearchQuery(e.target.value)}
            placeholder="Search products by name or SKU (e.g. Coca-Cola, COKE500, Parle-G, Maggi)..."
            className="w-full pl-9 pr-10 py-2.5 bg-gray-50 border border-gray-300 rounded-lg text-sm text-gray-900 placeholder-gray-400 focus:bg-white focus:outline-none focus:ring-2 focus:ring-[#0066CC] focus:border-[#0066CC] transition-all"
          />
          {searchQuery && (
            <button
              type="button"
              onClick={() => setSearchQuery('')}
              className="absolute right-3 text-gray-400 hover:text-gray-600 p-1 rounded-md transition-colors"
              title="Clear search"
            >
              <X className="w-4 h-4" />
            </button>
          )}
        </div>

        {/* Similar Suggestions Banner (Search Engine-like recommendations) */}
        {isSimilarSuggestion && searchQuery.trim() && (
          <div className="flex items-center gap-2 px-3 py-2 bg-amber-50 border border-amber-200 rounded-lg text-xs text-amber-900 animate-fade-in">
            <Sparkles className="w-4 h-4 text-amber-600 shrink-0" />
            <span>
              No exact match found for <strong>"{searchQuery}"</strong>. Showing <strong>{totalCount}</strong> similar products found in database:
            </span>
          </div>
        )}

        {/* Active Search Results Indicator */}
        {searchQuery.trim() && !isSimilarSuggestion && (
          <div className="flex items-center justify-between text-xs text-gray-500 px-1">
            <span>
              Found <strong>{totalCount}</strong> matching product{totalCount === 1 ? '' : 's'} for <strong>"{searchQuery}"</strong>
            </span>
            <button
              type="button"
              onClick={() => setSearchQuery('')}
              className="text-[#0066CC] hover:underline font-medium"
            >
              Clear filter
            </button>
          </div>
        )}
      </div>

      {/* Table */}
      <Table
        columns={columns}
        data={displayedProducts}
        keyExtractor={item => item.id}
        loading={loading}
        emptyMessage="No products found."
        emptySubtext={
          searchQuery.trim()
            ? `No matching or similar products found in database for "${searchQuery}". Try searching by product name or SKU.`
            : 'Add your first product to catalog to start recording inventory stock movements.'
        }
        currentPage={currentPage}
        pageSize={20}
        onPageChange={setCurrentPage}
      />

      {/* Add / Edit Product Modal */}
      <ProductForm
        isOpen={isFormOpen}
        productToEdit={productToEdit}
        loading={formSubmitting}
        serverError={serverFormError}
        onClose={() => setIsFormOpen(false)}
        onSubmit={handleFormSubmit}
      />

      {/* Delete Confirmation Dialog */}
      <ConfirmDialog
        isOpen={Boolean(productToDelete)}
        title="Delete Product"
        message={`Are you sure you want to delete product "${productToDelete?.name}" (${productToDelete?.sku})? This action cannot be undone.`}
        confirmLabel="Delete"
        cancelLabel="Cancel"
        variant="danger"
        loading={deleting}
        onConfirm={handleDeleteConfirm}
        onCancel={() => setProductToDelete(null)}
      />

      {/* View Product Details Modal */}
      {viewProduct && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-xs p-4 animate-fade-in">
          <div className="bg-white rounded-xl shadow-xl border border-gray-200 w-full max-w-md overflow-hidden transform transition-all">
            <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100 bg-gray-50">
              <div className="flex items-center gap-2">
                <Package className="w-5 h-5 text-[#0066CC]" />
                <h3 className="text-lg font-bold text-gray-900">Product Details</h3>
              </div>
              <button
                type="button"
                onClick={() => setViewProduct(null)}
                className="text-gray-400 hover:text-gray-600 p-1 rounded-md"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="px-6 py-5 space-y-3.5 text-sm">
              <div className="flex justify-between py-1.5 border-b border-gray-100">
                <span className="text-gray-500 font-medium">Product Name:</span>
                <span className="font-bold text-gray-900">{viewProduct.name}</span>
              </div>
              <div className="flex justify-between py-1.5 border-b border-gray-100">
                <span className="text-gray-500 font-medium">SKU:</span>
                <span className="font-mono font-bold text-gray-800 bg-gray-100 px-2 py-0.5 rounded">{viewProduct.sku}</span>
              </div>
              <div className="flex justify-between py-1.5 border-b border-gray-100">
                <span className="text-gray-500 font-medium">Current Stock:</span>
                <span className="font-bold text-gray-900">{viewProduct.currentStock ?? 0} units</span>
              </div>
              <div className="flex justify-between py-1.5 border-b border-gray-100">
                <span className="text-gray-500 font-medium">Reorder Threshold:</span>
                <span className="font-medium text-gray-800">{viewProduct.reorderThreshold} units</span>
              </div>
              <div className="flex justify-between py-1.5 border-b border-gray-100">
                <span className="text-gray-500 font-medium">Reorder Batch Quantity:</span>
                <span className="font-semibold text-blue-700">+{viewProduct.reorderQuantity} units</span>
              </div>
              <div className="flex justify-between py-1.5 border-b border-gray-100">
                <span className="text-gray-500 font-medium">Inventory Health Status:</span>
                <div>{getStatusBadge(viewProduct)}</div>
              </div>
              <div className="flex justify-between py-1.5 border-b border-gray-100">
                <span className="text-gray-500 font-medium">Active Alert Present:</span>
                <span className="font-semibold">{viewProduct.hasOpenAlert ? 'Yes (OPEN)' : 'No'}</span>
              </div>
              <div className="flex justify-between py-1.5">
                <span className="text-gray-500 font-medium">Added On:</span>
                <span className="text-gray-600 text-xs font-semibold">
                  {viewProduct.createdAt ? formatDateTime(viewProduct.createdAt) : 'Not available'}
                </span>
              </div>
            </div>

            <div className="px-6 py-4 bg-gray-50 border-t border-gray-100 flex justify-end">
              <button
                type="button"
                onClick={() => setViewProduct(null)}
                className="px-4 py-2 bg-white border border-gray-300 text-gray-700 text-sm font-medium rounded-lg hover:bg-gray-100 transition-colors"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
export default Products;
