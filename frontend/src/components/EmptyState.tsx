import React from 'react';
import { PackageOpen } from 'lucide-react';

interface EmptyStateProps {
  message?: string;
  subtext?: string;
  icon?: React.ReactNode;
  actionButton?: {
    label: string;
    onClick: () => void;
  };
}

export const EmptyState: React.FC<EmptyStateProps> = ({
  message = 'No data available',
  subtext,
  icon,
  actionButton,
}) => {
  return (
    <div className="flex flex-col items-center justify-center p-8 text-center bg-gray-50 rounded-lg border border-dashed border-gray-300">
      <div className="text-gray-400 mb-3">
        {icon || <PackageOpen className="w-12 h-12 stroke-[1.5]" />}
      </div>
      <h4 className="text-base font-semibold text-gray-700">{message}</h4>
      {subtext && <p className="text-sm text-gray-500 mt-1 max-w-sm">{subtext}</p>}
      {actionButton && (
        <button
          type="button"
          onClick={actionButton.onClick}
          className="mt-4 px-4 py-2 bg-[#0066CC] hover:bg-[#1E40AF] text-white text-sm font-medium rounded-md shadow-sm transition-colors"
        >
          {actionButton.label}
        </button>
      )}
    </div>
  );
};
export default EmptyState;
