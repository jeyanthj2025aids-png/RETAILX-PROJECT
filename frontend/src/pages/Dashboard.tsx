import React, { useState, useEffect } from 'react';
import { useOutletContext, Link } from 'react-router-dom';
import {
  Package,
  Boxes,
  BellRing,
  AlertTriangle,
  ArrowRight,
  TrendingUp,
  Clock,
  ShieldCheck,
} from 'lucide-react';
import { dashboardService } from '../services/dashboardService';
import { alertService } from '../services/alertService';
import type { DashboardSummary, ReorderAlert } from '../types';
import Card from '../components/Card';
import Badge from '../components/Badge';
import LoadingSpinner from '../components/LoadingSpinner';
import ConfirmDialog from '../components/ConfirmDialog';
import { formatDate, formatDateTime, formatNumber } from '../utils/formatters';
import { MOVEMENT_TYPE_COLORS } from '../utils/constants';

interface OutletContextType {
  addToast: (type: 'success' | 'error' | 'warning' | 'info', message: string) => void;
  refreshAlertCount: () => void;
}

export const Dashboard: React.FC = () => {
  const { addToast, refreshAlertCount } = useOutletContext<OutletContextType>();

  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [loading, setLoading] = useState(true);
  const [alertToFulfill, setAlertToFulfill] = useState<ReorderAlert | null>(null);
  const [fulfilling, setFulfilling] = useState(false);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      const data = await dashboardService.getSummary();
      setSummary(data);
    } catch {
      addToast('error', 'Failed to fetch live dashboard summary metrics.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const handleConfirmFulfill = async () => {
    if (!alertToFulfill) return;
    try {
      setFulfilling(true);
      await alertService.fulfill(alertToFulfill.id);
      addToast('success', `Reorder alert for ${alertToFulfill.productName || 'product'} fulfilled successfully!`);
      setAlertToFulfill(null);
      refreshAlertCount();
      await fetchDashboardData();
    } catch {
      addToast('error', 'Failed to fulfill reorder alert. Please try again.');
    } finally {
      setFulfilling(false);
    }
  };

  const currentDateFormatted = new Date().toLocaleDateString('en-US', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });

  if (loading && !summary) {
    return (
      <div className="py-24 flex items-center justify-center">
        <LoadingSpinner size="lg" text="Loading live inventory metrics..." />
      </div>
    );
  }

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Top Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-2 border-b border-gray-200">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-gray-900 tracking-tight">Dashboard</h1>
          <p className="text-sm text-gray-500 mt-1 flex items-center gap-1.5">
            <Clock className="w-4 h-4 text-gray-400" />
            <span>{currentDateFormatted}</span>
            <span>•</span>
            <span className="text-blue-600 font-medium">Real-time DB Ledger</span>
          </p>
        </div>

        <div className="flex items-center gap-2.5">
          <Link
            to="/stock-movements"
            className="px-4 py-2 bg-[#0066CC] hover:bg-[#1E40AF] text-white text-sm font-semibold rounded-lg shadow-sm transition-colors"
          >
            Record Movement
          </Link>
          <Link
            to="/products"
            className="px-4 py-2 bg-white hover:bg-gray-50 text-gray-700 text-sm font-semibold rounded-lg border border-gray-300 shadow-2xs transition-colors"
          >
            Manage Catalog
          </Link>
        </div>
      </div>

      {/* Summary Cards (4 Cards in row) */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <Card
          title="Total Products"
          value={formatNumber(summary?.totalProducts ?? 0)}
          subtext="products in inventory"
          icon={<Package className="w-5 h-5 text-blue-600" />}
          iconBgColor="bg-blue-50"
        />
        <Card
          title="Total Stock Units"
          value={formatNumber(summary?.totalStockUnits ?? 0)}
          subtext="units across all products"
          icon={<Boxes className="w-5 h-5 text-indigo-600" />}
          iconBgColor="bg-indigo-50"
        />
        <Card
          title="Open Reorder Alerts"
          value={formatNumber(summary?.openReorderAlerts ?? 0)}
          subtext="alerts awaiting action"
          icon={<BellRing className="w-5 h-5 text-amber-600" />}
          iconBgColor="bg-amber-50"
        />
        <Card
          title="Products Below Threshold"
          value={formatNumber(summary?.productsLowStock ?? 0)}
          subtext="products low on stock"
          icon={<AlertTriangle className="w-5 h-5 text-rose-600" />}
          iconBgColor="bg-rose-50"
        />
      </div>

      {/* Grid: Open Reorder Alerts & Top Fast-Moving Products */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
        {/* Section 2: Open Reorder Alerts (Table) */}
        <div className="lg:col-span-7 bg-white rounded-xl border border-gray-200 p-6 shadow-xs">
          <div className="flex items-center justify-between pb-4 mb-4 border-b border-gray-100">
            <div className="flex items-center gap-2">
              <div className="p-1.5 rounded-lg bg-amber-50 text-amber-600">
                <BellRing className="w-4 h-4" />
              </div>
              <h2 className="text-base font-bold text-gray-900">Open Reorder Alerts</h2>
            </div>
            <Link
              to="/reorder-alerts"
              className="text-xs font-semibold text-[#0066CC] hover:text-[#1E40AF] flex items-center gap-1"
            >
              <span>View All</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </Link>
          </div>

          {summary?.openAlerts && summary.openAlerts.length > 0 ? (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm">
                <thead>
                  <tr className="bg-gray-50 text-xs font-semibold text-gray-500 uppercase tracking-wider border-b border-gray-200">
                    <th className="px-3.5 py-2.5">Product</th>
                    <th className="px-3 py-2.5">Stock</th>
                    <th className="px-3 py-2.5">Threshold</th>
                    <th className="px-3 py-2.5">Reorder Qty</th>
                    <th className="px-3 py-2.5">Status</th>
                    <th className="px-3 py-2.5 text-right">Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {summary.openAlerts.map(alert => (
                    <tr key={alert.id} className="hover:bg-amber-50/20">
                      <td className="px-3.5 py-3 font-semibold text-gray-900">
                        {alert.productName || `Product #${alert.productId}`}
                      </td>
                      <td className="px-3 py-3">
                        <span className="font-bold text-amber-600 bg-amber-50 px-1.5 py-0.5 rounded text-xs border border-amber-200">
                          {alert.currentStockAtAlert}
                        </span>
                      </td>
                      <td className="px-3 py-3 text-gray-600 text-xs">{alert.reorderThresholdAtAlert}</td>
                      <td className="px-3 py-3 text-blue-700 font-semibold text-xs">+{alert.reorderQuantity}</td>
                      <td className="px-3 py-3">
                        <Badge variant="orange" dot>
                          OPEN
                        </Badge>
                      </td>
                      <td className="px-3 py-3 text-right">
                        <button
                          type="button"
                          onClick={() => setAlertToFulfill(alert)}
                          className="px-2.5 py-1 bg-[#0066CC] hover:bg-[#1E40AF] text-white text-xs font-semibold rounded shadow-2xs transition-colors"
                        >
                          Fulfill
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <div className="py-8 text-center text-gray-500 bg-gray-50 rounded-lg border border-dashed border-gray-200 text-sm">
              <ShieldCheck className="w-8 h-8 text-emerald-500 mx-auto mb-2" />
              <p className="font-semibold text-gray-700">No open alerts</p>
              <p className="text-xs text-gray-500 mt-0.5">All product inventory is currently healthy.</p>
            </div>
          )}
        </div>

        {/* Section 3: Top Fast-Moving Products (Table) */}
        <div className="lg:col-span-5 bg-white rounded-xl border border-gray-200 p-6 shadow-xs">
          <div className="flex items-center justify-between pb-4 mb-4 border-b border-gray-100">
            <div className="flex items-center gap-2">
              <div className="p-1.5 rounded-lg bg-indigo-50 text-indigo-600">
                <TrendingUp className="w-4 h-4" />
              </div>
              <h2 className="text-base font-bold text-gray-900">Top Fast-Moving (30 Days)</h2>
            </div>
            <Link
              to="/reports"
              className="text-xs font-semibold text-[#0066CC] hover:text-[#1E40AF] flex items-center gap-1"
            >
              <span>Full Report</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </Link>
          </div>

          {summary?.topFastMovingProducts && summary.topFastMovingProducts.length > 0 ? (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm">
                <thead>
                  <tr className="bg-gray-50 text-xs font-semibold text-gray-500 uppercase tracking-wider border-b border-gray-200">
                    <th className="px-3 py-2.5">Rank</th>
                    <th className="px-3.5 py-2.5">Product</th>
                    <th className="px-3 py-2.5">SKU</th>
                    <th className="px-3 py-2.5 text-right">Units Sold</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {summary.topFastMovingProducts.slice(0, 5).map(prod => (
                    <tr key={prod.productId} className="hover:bg-indigo-50/20">
                      <td className="px-3 py-3 font-bold text-indigo-600">
                        <span className="w-5 h-5 rounded-full bg-indigo-100 flex items-center justify-center text-xs">
                          {prod.rank}
                        </span>
                      </td>
                      <td className="px-3.5 py-3 font-semibold text-gray-900">{prod.name}</td>
                      <td className="px-3 py-3 text-xs text-gray-500 font-mono">{prod.sku}</td>
                      <td className="px-3 py-3 text-right font-bold text-gray-900">
                        {formatNumber(prod.unitsSold)}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <div className="py-8 text-center text-gray-500 bg-gray-50 rounded-lg border border-dashed border-gray-200 text-sm">
              <p className="font-semibold text-gray-700">No sales data available</p>
              <p className="text-xs text-gray-500 mt-0.5">Record SALE movements to generate velocity metrics.</p>
            </div>
          )}
        </div>
      </div>

      {/* Section 1: Recent Stock Movements (Table) */}
      <div className="bg-white rounded-xl border border-gray-200 p-6 shadow-xs">
        <div className="flex items-center justify-between pb-4 mb-4 border-b border-gray-100">
          <div className="flex items-center gap-2">
            <div className="p-1.5 rounded-lg bg-blue-50 text-[#0066CC]">
              <Package className="w-4 h-4" />
            </div>
            <h2 className="text-base font-bold text-gray-900">Recent Stock Movements (Last 10)</h2>
          </div>
          <Link
            to="/stock-movements"
            className="text-xs font-semibold text-[#0066CC] hover:text-[#1E40AF] flex items-center gap-1"
          >
            <span>Movement History</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </Link>
        </div>

        {summary?.recentMovements && summary.recentMovements.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead>
                <tr className="bg-gray-50 text-xs font-semibold text-gray-500 uppercase tracking-wider border-b border-gray-200">
                  <th className="px-4 py-3">Product</th>
                  <th className="px-4 py-3">Type</th>
                  <th className="px-4 py-3">Quantity</th>
                  <th className="px-4 py-3">Movement Date</th>
                  <th className="px-4 py-3">Logged At</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {summary.recentMovements.map((movement, idx) => {
                  const style = MOVEMENT_TYPE_COLORS[movement.movementType] || MOVEMENT_TYPE_COLORS.PURCHASE;
                  return (
                    <tr key={movement.id || idx} className="hover:bg-blue-50/20">
                      <td className="px-4 py-3 font-semibold text-gray-900">
                        {movement.productName || `Product #${movement.productId}`}
                      </td>
                      <td className="px-4 py-3">
                        <span className={`inline-flex items-center gap-1 px-2 py-0.5 rounded text-xs font-bold ${style.badge}`}>
                          <span>{style.icon}</span>
                          <span>{movement.movementType}</span>
                        </span>
                      </td>
                      <td className="px-4 py-3 font-bold text-gray-800">{formatNumber(movement.quantity)} units</td>
                      <td className="px-4 py-3 text-xs text-gray-600">{formatDate(movement.movementDate)}</td>
                      <td className="px-4 py-3 text-xs text-gray-500">{formatDateTime(movement.createdAt)}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="py-8 text-center text-gray-500 bg-gray-50 rounded-lg border border-dashed border-gray-200 text-sm">
            <p className="font-semibold text-gray-700">No movements yet</p>
            <p className="text-xs text-gray-500 mt-0.5">Use "Record Movement" to log stock inflows and outflows.</p>
          </div>
        )}
      </div>

      {/* Confirmation Dialog for Fulfill Alert */}
      <ConfirmDialog
        isOpen={Boolean(alertToFulfill)}
        title="Confirm Alert Fulfillment"
        message={`Mark alert for ${alertToFulfill?.productName || 'selected product'} as fulfilled? This will record fulfillment timestamp and clear active alert status.`}
        confirmLabel="Confirm"
        cancelLabel="Cancel"
        variant="primary"
        loading={fulfilling}
        onConfirm={handleConfirmFulfill}
        onCancel={() => setAlertToFulfill(null)}
      />
    </div>
  );
};
export default Dashboard;
