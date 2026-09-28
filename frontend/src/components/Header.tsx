import React, { useState } from 'react';
import { Menu, X, Boxes } from 'lucide-react';
import Navigation from './Navigation';

interface HeaderProps {
  openAlertCount?: number;
}

export const Header: React.FC<HeaderProps> = ({ openAlertCount = 0 }) => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  return (
    <header className="sticky top-0 z-40 bg-white/95 backdrop-blur-md border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo / App Name */}
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-[#0066CC] to-[#1E40AF] flex items-center justify-center text-white shadow-md shadow-blue-500/20">
              <Boxes className="w-6 h-6" />
            </div>
            <div>
              <span className="text-xl font-black tracking-tight text-gray-900 flex items-center gap-1">
                Retail<span className="text-[#0066CC]">X</span>
              </span>
              <span className="text-[10px] uppercase font-bold tracking-widest text-gray-400 block -mt-1">
                Inventory & Reorder Intelligence
              </span>
            </div>
          </div>

          {/* Desktop Navigation */}
          <div className="hidden md:flex items-center">
            <Navigation openAlertCount={openAlertCount} />
          </div>

          {/* Right Status Indicator */}
          <div className="hidden md:flex items-center gap-3">
            <div className="flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-50 border border-emerald-200 text-xs font-medium text-emerald-700">
              <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
              <span>System Live</span>
            </div>
          </div>

          {/* Mobile Menu Button */}
          <div className="flex md:hidden">
            <button
              type="button"
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="p-2 rounded-lg text-gray-600 hover:text-gray-900 hover:bg-gray-100 focus:outline-none"
              aria-label="Toggle navigation menu"
            >
              {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile Menu Dropdown */}
      {mobileMenuOpen && (
        <div className="md:hidden border-t border-gray-200 bg-white px-4 pt-2 pb-4 shadow-lg">
          <Navigation
            mobile
            openAlertCount={openAlertCount}
            onItemClick={() => setMobileMenuOpen(false)}
          />
        </div>
      )}
    </header>
  );
};
export default Header;
