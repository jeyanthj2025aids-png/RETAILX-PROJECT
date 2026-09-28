import React, { useState } from 'react';
import { useOutletContext } from 'react-router-dom';
import { BarChart3, Calendar, Award, AlertCircle } from 'lucide-react';
import { reportService } from '../services/reportService';
import type { FastMovingProduct } from '../types';
import Table, { type Column } from '../components/Table';
import LoadingSpinner from '../components/LoadingSpinner';
import { formatNumber } from '../utils/formatters';

interface OutletContextType {
  addToast: (type: 'success' | 'error' | 'warning' | 'info', message: string) => void;
}

export const FastMovingReport: React.FC = () => {
  const { addToast } = useOutletContext<OutletContextType>();

  const todayStr = new Date().toISOString().split('T')[0];
  const defaultStartStr = new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];

  const [startDate, setStartDate] = useState(defaultStartStr);
  const [endDate, setEndDate] = useState(todayStr);
  const [reportData, setReportData] = useState<FastMovingProduct[] | null>(null);
  const [loading, setLoading] = useState(false);
  const [dateError, setDateError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(0);

  const handleGenerateReport = async (e?: React.FormEvent) => {
    if (e) e.preventDefault();

    setDateError(null);

    if (!startDate || !endDate) {
      setDateError('Both start date and end date are required');
      return;
    }

    if (new Date(startDate) > new Date(endDate)) {
      setDateError('❌ Start date must be before end date');
      return;
    }

    try {
      setLoading(true);
      const data = await reportService.getFastMovingProducts(startDate, endDate);
      setReportData(data);
      setCurrentPage(0);
      addToast('success', `Fast-moving sales report generated (${data.length} products found).`);
    } catch (err: unknown) {
      if (typeof err === 'object' && err !== null && 'response' in err) {
        const axErr = err as { response?: { data?: { message?: string } } };
        setDateError(axErr.response?.data?.message || 'Failed to generate report');
      } else {
        setDateError('Failed to generate report for selected date range');
      }
    } finally {
      setLoading(false);
    }
  };

  const columns: Column<FastMovingProduct>[] = [
    {
      header: 'Rank',
      render: p => {
        const rankColors: Record<number, string> = {
          1: 'bg-amber-100 text-amber-800 border-amber-300 font-black',
          2: 'bg-slate-200 text-slate-800 border-slate-300 font-bold',
          3: 'bg-amber-700/15 text-amber-900 border-amber-700/30 font-bold',
        };
        const rankStyle = rankColors[p.rank] || 'bg-gray-100 text-gray-700 border-gray-200';

        return (
          <div className="flex items-center gap-2">
            <span className={`w-7 h-7 rounded-full flex items-center justify-center text-xs border ${rankStyle}`}>
              {p.rank === 1 ? <Award className="w-3.5 h-3.5 text-amber-600" /> : p.rank}
            </span>
          </div>
        );
      },
    },
    {
      header: 'Product',
      render: p => (
        <div>
          <span className="font-semibold text-gray-900">{p.name}</span>
          <div className="text-xs text-gray-400">ID #{p.productId}</div>
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
      header: 'Units Sold (SALE Volume)',
      render: p => (
        <span className="font-bold text-gray-900 text-base">
          {formatNumber(p.unitsSold)} <span className="text-xs font-normal text-gray-500">units</span>
        </span>
      ),
    },
  ];

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Header */}
      <div className="pb-2 border-b border-gray-200">
        <h1 className="text-2xl sm:text-3xl font-extrabold text-gray-900 tracking-tight">
          Fast-Moving Products Report
        </h1>
        <p className="text-sm text-gray-500 mt-1">
          Rank catalog products strictly by outbound SALE movement volume within customized date ranges
        </p>
      </div>

      {/* Date Range Selector Form (Top) */}
      <div className="bg-white rounded-xl border border-gray-200 p-6 shadow-xs">
        <form onSubmit={handleGenerateReport} className="space-y-4">
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 items-end">
            <div>
              <label className="block text-xs font-bold text-gray-700 uppercase tracking-wider mb-1.5 flex items-center gap-1.5">
                <Calendar className="w-3.5 h-3.5 text-gray-500" />
                <span>Start Date <span className="text-red-500">*</span></span>
              </label>
              <input
                type="date"
                value={startDate}
                onChange={e => setStartDate(e.target.value)}
                className="w-full px-3.5 py-2.5 rounded-lg border border-gray-300 text-sm text-gray-900 bg-white focus:outline-none focus:ring-2 focus:ring-blue-100 focus:border-[#0066CC] transition-all"
              />
            </div>

            <div>
              <label className="block text-xs font-bold text-gray-700 uppercase tracking-wider mb-1.5 flex items-center gap-1.5">
                <Calendar className="w-3.5 h-3.5 text-gray-500" />
                <span>End Date <span className="text-red-500">*</span></span>
              </label>
              <input
                type="date"
                value={endDate}
                onChange={e => setEndDate(e.target.value)}
                className="w-full px-3.5 py-2.5 rounded-lg border border-gray-300 text-sm text-gray-900 bg-white focus:outline-none focus:ring-2 focus:ring-blue-100 focus:border-[#0066CC] transition-all"
              />
            </div>

            <div>
              <button
                type="submit"
                disabled={loading}
                className="w-full px-6 py-2.5 bg-[#0066CC] hover:bg-[#1E40AF] disabled:opacity-50 disabled:cursor-not-allowed text-white text-sm font-semibold rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-300 transition-colors flex items-center justify-center gap-2"
              >
                {loading && <LoadingSpinner size="sm" />}
                <span>Generate Report</span>
              </button>
            </div>
          </div>

          {dateError && (
            <div className="p-3.5 rounded-lg bg-red-50 border border-red-200 text-red-800 text-sm flex items-center gap-2">
              <AlertCircle className="w-4 h-4 text-red-600 shrink-0" />
              <span>{dateError}</span>
            </div>
          )}
        </form>
      </div>

      {/* Report Results */}
      <div className="space-y-4">
        <div className="flex items-center gap-2">
          <BarChart3 className="w-5 h-5 text-gray-700" />
          <h2 className="text-lg font-bold text-gray-900">Ranked Sales Velocity</h2>
        </div>

        {reportData ? (
          <Table
            columns={columns}
            data={reportData}
            keyExtractor={item => item.productId}
            loading={loading}
            emptyMessage="No sales data available for selected date range"
            emptySubtext="Try selecting a wider date range or logging new SALE movements."
            currentPage={currentPage}
            pageSize={20}
            onPageChange={setCurrentPage}
          />
        ) : (
          <div className="py-12 bg-white rounded-xl border border-dashed border-gray-300 text-center text-gray-500 text-sm">
            <BarChart3 className="w-10 h-10 text-gray-400 mx-auto mb-2" />
            <p className="font-semibold text-gray-700">Select Date Range and click "Generate Report"</p>
            <p className="text-xs text-gray-500 mt-0.5">
              Calculates total units sold (SALE only) per product sorted in descending order.
            </p>
          </div>
        )}
      </div>
    </div>
  );
};
export default FastMovingReport;
