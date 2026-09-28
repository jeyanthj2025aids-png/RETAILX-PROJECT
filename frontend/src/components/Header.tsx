import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Menu, X, Boxes, User, LogOut, LogIn } from 'lucide-react';
import Navigation from './Navigation';

interface HeaderProps {
  openAlertCount?: number;
}

interface StoredUser {
  name: string;
  email: string;
  role: string;
}

export const Header: React.FC<HeaderProps> = ({ openAlertCount = 0 }) => {
  const navigate = useNavigate();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [currentUser, setCurrentUser] = useState<StoredUser | null>(null);

  useEffect(() => {
    const checkUser = () => {
      const stored = localStorage.getItem('retailx_user');
      if (stored) {
        try {
          setCurrentUser(JSON.parse(stored));
        } catch {
          setCurrentUser(null);
        }
      } else {
        setCurrentUser(null);
      }
    };

    checkUser();
    window.addEventListener('storage', checkUser);
    return () => window.removeEventListener('storage', checkUser);
  }, []);

  const handleSignOut = () => {
    localStorage.removeItem('retailx_user');
    setCurrentUser(null);
    navigate('/login');
  };

  return (
    <header className="sticky top-0 z-40 bg-white/95 backdrop-blur-md border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo / App Name */}
          <Link to="/" className="flex items-center gap-3 group">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-[#0066CC] to-[#1E40AF] flex items-center justify-center text-white shadow-md shadow-blue-500/20 group-hover:scale-105 transition-transform">
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
          </Link>

          {/* Desktop Navigation */}
          <div className="hidden md:flex items-center">
            <Navigation openAlertCount={openAlertCount} />
          </div>

          {/* Right Status Indicator & Auth */}
          <div className="hidden md:flex items-center gap-3">
            <div className="flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-50 border border-emerald-200 text-xs font-medium text-emerald-700">
              <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
              <span>System Live</span>
            </div>

            {currentUser ? (
              <div className="flex items-center gap-2 pl-2 border-l border-gray-200">
                <div className="flex items-center gap-1.5 px-2.5 py-1 bg-gray-100 rounded-lg text-xs font-semibold text-gray-800">
                  <User className="w-3.5 h-3.5 text-[#0066CC]" />
                  <span>{currentUser.name}</span>
                </div>
                <button
                  type="button"
                  onClick={handleSignOut}
                  className="p-1.5 text-gray-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors"
                  title="Sign Out"
                >
                  <LogOut className="w-4 h-4" />
                </button>
              </div>
            ) : (
              <Link
                to="/login"
                className="flex items-center gap-1 px-3 py-1.5 bg-gray-50 hover:bg-blue-50 text-gray-700 hover:text-[#0066CC] border border-gray-200 hover:border-blue-200 rounded-lg text-xs font-semibold transition-colors"
              >
                <LogIn className="w-3.5 h-3.5" />
                <span>Sign In</span>
              </Link>
            )}
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
        <div className="md:hidden border-t border-gray-200 bg-white px-4 pt-2 pb-4 shadow-lg space-y-3">
          <Navigation
            mobile
            openAlertCount={openAlertCount}
            onItemClick={() => setMobileMenuOpen(false)}
          />

          <div className="pt-2 border-t border-gray-100 flex items-center justify-between">
            {currentUser ? (
              <div className="flex items-center justify-between w-full">
                <div className="flex items-center gap-2 text-xs font-semibold text-gray-800">
                  <User className="w-4 h-4 text-[#0066CC]" />
                  <span>{currentUser.name} ({currentUser.role})</span>
                </div>
                <button
                  type="button"
                  onClick={handleSignOut}
                  className="text-xs font-semibold text-rose-600 hover:underline flex items-center gap-1"
                >
                  <LogOut className="w-3.5 h-3.5" />
                  <span>Sign Out</span>
                </button>
              </div>
            ) : (
              <Link
                to="/login"
                onClick={() => setMobileMenuOpen(false)}
                className="w-full py-2 bg-[#0066CC] text-white text-xs font-semibold rounded-lg text-center flex items-center justify-center gap-1.5"
              >
                <LogIn className="w-4 h-4" />
                <span>Sign In to Workstation</span>
              </Link>
            )}
          </div>
        </div>
      )}
    </header>
  );
};
export default Header;
