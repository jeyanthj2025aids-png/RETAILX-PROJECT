import React from 'react';
import { NavLink } from 'react-router-dom';
import { LayoutDashboard, Package, ArrowLeftRight, BellRing, BarChart3 } from 'lucide-react';

interface NavigationProps {
  mobile?: boolean;
  onItemClick?: () => void;
  openAlertCount?: number;
}

export const Navigation: React.FC<NavigationProps> = ({
  mobile = false,
  onItemClick,
  openAlertCount = 0,
}) => {
  const navItems = [
    { name: 'Dashboard', path: '/', icon: <LayoutDashboard className="w-4 h-4" /> },
    { name: 'Products', path: '/products', icon: <Package className="w-4 h-4" /> },
    { name: 'Stock Movements', path: '/stock-movements', icon: <ArrowLeftRight className="w-4 h-4" /> },
    {
      name: 'Reorder Alerts',
      path: '/reorder-alerts',
      icon: <BellRing className="w-4 h-4" />,
      badge: openAlertCount > 0 ? openAlertCount : undefined,
    },
    { name: 'Reports', path: '/reports', icon: <BarChart3 className="w-4 h-4" /> },
  ];

  if (mobile) {
    return (
      <div className="flex flex-col space-y-1 p-2">
        {navItems.map(item => (
          <NavLink
            key={item.path}
            to={item.path}
            onClick={onItemClick}
            className={({ isActive }) =>
              `flex items-center justify-between px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                isActive
                  ? 'bg-blue-50 text-[#0066CC] font-semibold'
                  : 'text-gray-700 hover:bg-gray-100 hover:text-gray-900'
              }`
            }
          >
            <div className="flex items-center gap-3">
              {item.icon}
              <span>{item.name}</span>
            </div>
            {item.badge !== undefined && (
              <span className="px-2 py-0.5 text-xs font-bold rounded-full bg-amber-500 text-white">
                {item.badge}
              </span>
            )}
          </NavLink>
        ))}
      </div>
    );
  }

  return (
    <nav className="flex items-center space-x-1 lg:space-x-2">
      {navItems.map(item => (
        <NavLink
          key={item.path}
          to={item.path}
          className={({ isActive }) =>
            `flex items-center gap-2 px-3.5 py-2 rounded-lg text-sm font-medium transition-all duration-150 ${
              isActive
                ? 'bg-blue-50/80 text-[#0066CC] font-semibold border border-blue-100/60 shadow-2xs'
                : 'text-gray-600 hover:text-gray-900 hover:bg-gray-50'
            }`
          }
        >
          {item.icon}
          <span>{item.name}</span>
          {item.badge !== undefined && (
            <span className="ml-1 px-1.5 py-0.5 text-[11px] font-bold rounded-full bg-amber-500 text-white animate-pulse">
              {item.badge}
            </span>
          )}
        </NavLink>
      ))}
    </nav>
  );
};
export default Navigation;
