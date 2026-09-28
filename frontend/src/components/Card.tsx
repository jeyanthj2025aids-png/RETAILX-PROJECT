import React from 'react';

interface CardProps {
  title: string;
  value: string | number;
  subtext?: string;
  icon: React.ReactNode;
  iconBgColor?: string;
  className?: string;
}

export const Card: React.FC<CardProps> = ({
  title,
  value,
  subtext,
  icon,
  iconBgColor = 'bg-blue-50 text-[#0066CC]',
  className = '',
}) => {
  return (
    <div
      className={`bg-white rounded-xl border border-gray-200 p-5 shadow-xs hover:shadow-md transition-shadow duration-200 ${className}`}
    >
      <div className="flex items-center justify-between">
        <span className="text-xs font-semibold text-gray-500 uppercase tracking-wider">
          {title}
        </span>
        <div className={`p-2.5 rounded-lg ${iconBgColor}`}>{icon}</div>
      </div>
      <div className="mt-2">
        <div className="text-2xl font-bold text-gray-900 tracking-tight">{value}</div>
        {subtext && <p className="text-xs text-gray-500 mt-1 font-medium">{subtext}</p>}
      </div>
    </div>
  );
};
export default Card;
