import React from 'react';

interface BadgeProps {
  variant?: 'green' | 'orange' | 'red' | 'blue' | 'gray';
  children: React.ReactNode;
  icon?: React.ReactNode;
  dot?: boolean;
  className?: string;
}

export const Badge: React.FC<BadgeProps> = ({
  variant = 'gray',
  children,
  icon,
  dot,
  className = '',
}) => {
  const variantStyles = {
    green: 'bg-emerald-50 text-emerald-700 border-emerald-200',
    orange: 'bg-amber-50 text-amber-700 border-amber-200',
    red: 'bg-rose-50 text-rose-700 border-rose-200',
    blue: 'bg-blue-50 text-blue-700 border-blue-200',
    gray: 'bg-gray-100 text-gray-700 border-gray-200',
  };

  const dotColors = {
    green: 'bg-emerald-500',
    orange: 'bg-amber-500',
    red: 'bg-rose-500',
    blue: 'bg-blue-500',
    gray: 'bg-gray-500',
  };

  return (
    <span
      className={`inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-semibold border ${variantStyles[variant]} ${className}`}
    >
      {dot && <span className={`w-1.5 h-1.5 rounded-full ${dotColors[variant]}`} />}
      {icon && <span className="shrink-0">{icon}</span>}
      {children}
    </span>
  );
};
export default Badge;
