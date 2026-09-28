import React from 'react';
import LoadingSpinner from './LoadingSpinner';
import EmptyState from './EmptyState';

export interface Column<T> {
  header: string;
  accessor?: keyof T | string;
  render?: (item: T, index: number) => React.ReactNode;
  className?: string;
  headerClassName?: string;
}

interface TableProps<T> {
  columns: Column<T>[];
  data: T[];
  keyExtractor: (item: T, index: number) => string | number;
  loading?: boolean;
  emptyMessage?: string;
  emptySubtext?: string;
  currentPage?: number;
  pageSize?: number;
  hasMore?: boolean;
  onPageChange?: (page: number) => void;
  className?: string;
}

export function Table<T>({
  columns,
  data,
  keyExtractor,
  loading = false,
  emptyMessage = 'No records found',
  emptySubtext,
  currentPage,
  pageSize = 20,
  hasMore = false,
  onPageChange,
  className = '',
}: TableProps<T>) {
  if (loading) {
    return (
      <div className="py-16 bg-white rounded-xl border border-gray-200 flex items-center justify-center">
        <LoadingSpinner text="Loading data..." />
      </div>
    );
  }

  if (!data || data.length === 0) {
    return <EmptyState message={emptyMessage} subtext={emptySubtext} />;
  }

  return (
    <div className={`bg-white rounded-xl border border-gray-200 overflow-hidden shadow-xs ${className}`}>
      <div className="overflow-x-auto">
        <table className="w-full text-left border-collapse text-sm">
          <thead>
            <tr className="bg-gray-50/80 border-b border-gray-200 text-xs font-semibold text-gray-500 uppercase tracking-wider">
              {columns.map((col, idx) => (
                <th
                  key={idx}
                  className={`px-5 py-3.5 ${col.headerClassName || ''}`}
                  scope="col"
                >
                  {col.header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {data.map((item, rowIdx) => (
              <tr
                key={keyExtractor(item, rowIdx)}
                className="hover:bg-blue-50/30 transition-colors duration-150"
              >
                {columns.map((col, colIdx) => (
                  <td
                    key={colIdx}
                    className={`px-5 py-3.5 text-gray-700 whitespace-nowrap ${col.className || ''}`}
                  >
                    {col.render
                      ? col.render(item, rowIdx)
                      : (col.accessor ? String(item[col.accessor as keyof T] ?? '—') : '—')}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {onPageChange && currentPage !== undefined && (
        <div className="flex items-center justify-between px-5 py-3.5 bg-gray-50 border-t border-gray-200 text-xs text-gray-600">
          <div>
            Page <span className="font-semibold">{currentPage + 1}</span>
          </div>
          <div className="flex items-center gap-2">
            <button
              type="button"
              disabled={currentPage <= 0}
              onClick={() => onPageChange(currentPage - 1)}
              className="px-3 py-1.5 rounded-md bg-white border border-gray-300 font-medium text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed shadow-2xs transition-colors"
            >
              Previous
            </button>
            <button
              type="button"
              disabled={!hasMore && data.length < pageSize}
              onClick={() => onPageChange(currentPage + 1)}
              className="px-3 py-1.5 rounded-md bg-white border border-gray-300 font-medium text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed shadow-2xs transition-colors"
            >
              Next
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
export default Table;
