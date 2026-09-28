import React from 'react';
import { Check, ShieldCheck } from 'lucide-react';
import type { ReorderAlert } from '../types';
import Table, { type Column } from './Table';
import Badge from './Badge';
import { formatDateTime, formatNumber } from '../utils/formatters';

interface AlertTableProps {
  alerts: ReorderAlert[];
  loading?: boolean;
  onFulfillClick: (alert: ReorderAlert) => void;
  currentPage?: number;
  onPageChange?: (page: number) => void;
  hasMore?: boolean;
}

export const AlertTable: React.FC<AlertTableProps> = ({
  alerts,
  loading = false,
  onFulfillClick,
  currentPage = 0,
  onPageChange,
  hasMore = false,
}) => {
  const columns: Column<ReorderAlert>[] = [
    {
      header: 'Product',
      render: alert => (
        <div>
          <div className="font-semibold text-gray-900">{alert.productName || `Product #${alert.productId}`}</div>
          <div className="text-xs text-gray-500 font-mono tracking-wider">{alert.productSku || '—'}</div>
        </div>
      ),
    },
    {
      header: 'Stock at Alert',
      render: alert => (
        <span className="font-bold text-amber-600 bg-amber-50 px-2 py-0.5 rounded border border-amber-200">
          {formatNumber(alert.currentStockAtAlert)} units
        </span>
      ),
    },
    {
      header: 'Threshold',
      render: alert => (
        <span className="text-gray-700 font-medium">{formatNumber(alert.reorderThresholdAtAlert)} units</span>
      ),
    },
    {
      header: 'Reorder Qty',
      render: alert => (
        <span className="font-semibold text-blue-700 bg-blue-50 px-2 py-0.5 rounded border border-blue-200">
          +{formatNumber(alert.reorderQuantity)} units
        </span>
      ),
    },
    {
      header: 'Status',
      render: alert =>
        alert.status === 'OPEN' ? (
          <Badge variant="orange" dot>
            OPEN
          </Badge>
        ) : (
          <Badge variant="green" dot icon={<ShieldCheck className="w-3.5 h-3.5 text-emerald-600" />}>
            FULFILLED
          </Badge>
        ),
    },
    {
      header: 'Created Date',
      render: alert => <span className="text-gray-600 text-xs">{formatDateTime(alert.createdAt)}</span>,
    },
    {
      header: 'Fulfilled Date',
      render: alert => (
        <span className="text-gray-600 text-xs">
          {alert.status === 'FULFILLED' ? formatDateTime(alert.fulfilledAt) : '—'}
        </span>
      ),
    },
    {
      header: 'Action',
      render: alert =>
        alert.status === 'OPEN' ? (
          <button
            type="button"
            onClick={() => onFulfillClick(alert)}
            className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-[#0066CC] hover:bg-[#1E40AF] text-white text-xs font-semibold rounded-md shadow-2xs transition-colors"
          >
            <Check className="w-3.5 h-3.5" />
            <span>Fulfill</span>
          </button>
        ) : (
          <span className="text-gray-400 font-mono text-xs">—</span>
        ),
    },
  ];

  return (
    <Table
      columns={columns}
      data={alerts}
      keyExtractor={item => item.id}
      loading={loading}
      emptyMessage="No alerts found"
      emptySubtext="All low stock items are either resolved or no reorder triggers have occurred yet."
      currentPage={currentPage}
      pageSize={20}
      hasMore={hasMore}
      onPageChange={onPageChange}
    />
  );
};
export default AlertTable;
