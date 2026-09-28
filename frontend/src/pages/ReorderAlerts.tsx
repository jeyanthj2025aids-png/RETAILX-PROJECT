import React, { useState, useEffect } from 'react';
import { useOutletContext } from 'react-router-dom';
import { ShieldCheck, Filter } from 'lucide-react';
import { alertService } from '../services/alertService';
import type { ReorderAlert } from '../types';
import AlertTable from '../components/AlertTable';
import ConfirmDialog from '../components/ConfirmDialog';

interface OutletContextType {
  addToast: (type: 'success' | 'error' | 'warning' | 'info', message: string) => void;
  refreshAlertCount: () => void;
}

export const ReorderAlerts: React.FC = () => {
  const { addToast, refreshAlertCount } = useOutletContext<OutletContextType>();

  const [alerts, setAlerts] = useState<ReorderAlert[]>([]);
  const [loading, setLoading] = useState(true);
  const [filterStatus, setFilterStatus] = useState<'ALL' | 'OPEN' | 'FULFILLED'>('ALL');
  const [currentPage, setCurrentPage] = useState(0);

  // Fulfill interaction modal
  const [alertToFulfill, setAlertToFulfill] = useState<ReorderAlert | null>(null);
  const [fulfilling, setFulfilling] = useState(false);

  const fetchAlerts = async (statusFilter = filterStatus, page = currentPage) => {
    try {
      setLoading(true);
      const statusParam = statusFilter === 'ALL' ? undefined : statusFilter;
      const data = await alertService.getAll(statusParam, page, 20);
      setAlerts(data);
    } catch {
      addToast('error', 'Failed to retrieve reorder alerts.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAlerts(filterStatus, currentPage);
  }, [filterStatus, currentPage]);

  const handleFilterChange = (newStatus: 'ALL' | 'OPEN' | 'FULFILLED') => {
    setFilterStatus(newStatus);
    setCurrentPage(0);
  };

  const handleFulfillConfirm = async () => {
    if (!alertToFulfill) return;
    try {
      setFulfilling(true);
      await alertService.fulfill(alertToFulfill.id);
      addToast('success', 'Reorder alert fulfilled successfully');
      setAlertToFulfill(null);
      refreshAlertCount();
      await fetchAlerts(filterStatus, currentPage);
    } catch {
      addToast('error', 'Failed to fulfill reorder alert');
    } finally {
      setFulfilling(false);
    }
  };

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-2 border-b border-gray-200">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-gray-900 tracking-tight">
            Reorder Alerts
          </h1>
          <p className="text-sm text-gray-500 mt-1">
            Automated threshold violation alerts with duplicate alert prevention
          </p>
        </div>

        {/* Filter Bar */}
        <div className="flex items-center gap-2.5 bg-white px-3.5 py-2 rounded-lg border border-gray-300 shadow-2xs">
          <Filter className="w-4 h-4 text-gray-500" />
          <span className="text-xs font-semibold text-gray-600">Show:</span>
          <select
            value={filterStatus}
            onChange={e => handleFilterChange(e.target.value as 'ALL' | 'OPEN' | 'FULFILLED')}
            className="text-xs font-semibold text-gray-800 bg-transparent focus:outline-none cursor-pointer"
          >
            <option value="ALL">All Alerts</option>
            <option value="OPEN">Open Alerts Only</option>
            <option value="FULFILLED">Fulfilled Alerts Only</option>
          </select>
        </div>
      </div>

      {/* Reorder Alerts Rule Banner */}
      <div className="p-4 rounded-xl bg-blue-50/80 border border-blue-200/80 flex items-start gap-3 text-xs text-blue-900 leading-relaxed">
        <ShieldCheck className="w-5 h-5 text-[#0066CC] shrink-0 mt-0.5" />
        <div>
          <span className="font-bold text-blue-950">Duplicate Alert Suppression Rule:</span> An OPEN alert is generated only once when stock reaches or drops below threshold. Further stock decrements while an OPEN alert is active will NOT generate duplicate alerts. Once fulfilled, future low-stock transitions will cleanly trigger new alerts.
        </div>
      </div>

      {/* Alerts Table */}
      <AlertTable
        alerts={alerts}
        loading={loading}
        onFulfillClick={alert => setAlertToFulfill(alert)}
        currentPage={currentPage}
        onPageChange={setCurrentPage}
      />

      {/* Confirmation Dialog for Fulfill Alert */}
      <ConfirmDialog
        isOpen={Boolean(alertToFulfill)}
        title="Confirm Alert Fulfillment"
        message={`Mark alert for ${alertToFulfill?.productName || 'selected item'} as fulfilled? This records timestamp and clears the active OPEN state.`}
        confirmLabel="Confirm"
        cancelLabel="Cancel"
        variant="primary"
        loading={fulfilling}
        onConfirm={handleFulfillConfirm}
        onCancel={() => setAlertToFulfill(null)}
      />
    </div>
  );
};
export default ReorderAlerts;
