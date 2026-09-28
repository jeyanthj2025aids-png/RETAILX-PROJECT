import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Boxes, Lock, Mail, Eye, EyeOff, ArrowRight, ShieldCheck, UserCheck } from 'lucide-react';

export const Login: React.FC = () => {
  const navigate = useNavigate();

  const [email, setEmail] = useState('manager@retailx.com');
  const [password, setPassword] = useState('retailx2026');
  const [showPassword, setShowPassword] = useState(false);
  const [rememberMe, setRememberMe] = useState(true);
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const handleLogin = (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);

    if (!email.trim() || !password.trim()) {
      setErrorMessage('Please enter both email and password.');
      return;
    }

    setLoading(true);

    // Simulate authenticating against store credentials
    setTimeout(() => {
      setLoading(false);
      const user = {
        name: email.startsWith('admin') ? 'Admin User' : 'Store Manager',
        email: email.trim(),
        role: email.startsWith('admin') ? 'Administrator' : 'Store Manager',
        loggedInAt: new Date().toISOString(),
      };

      localStorage.setItem('retailx_user', JSON.stringify(user));
      navigate('/');
    }, 600);
  };

  const handleQuickFill = (demoEmail: string, demoPass: string) => {
    setEmail(demoEmail);
    setPassword(demoPass);
    setErrorMessage(null);
  };

  return (
    <div className="min-h-[80vh] flex flex-col justify-center items-center py-8 px-4 sm:px-6 lg:px-8 animate-fade-in">
      {/* Branding Card */}
      <div className="w-full max-w-md space-y-6">
        <div className="text-center space-y-2">
          <div className="inline-flex w-12 h-12 rounded-2xl bg-gradient-to-tr from-[#0066CC] to-[#1E40AF] items-center justify-center text-white shadow-lg shadow-blue-500/20 mb-1">
            <Boxes className="w-7 h-7" />
          </div>
          <h2 className="text-2xl sm:text-3xl font-extrabold text-gray-900 tracking-tight">
            Sign in to Retail<span className="text-[#0066CC]">X</span>
          </h2>
          <p className="text-xs sm:text-sm text-gray-500">
            Inventory Ledger & Automated Reorder Alert Intelligence
          </p>
        </div>

        {/* Login Form Container */}
        <div className="bg-white py-8 px-6 sm:px-8 rounded-2xl shadow-sm border border-gray-200 space-y-6">
          {errorMessage && (
            <div className="p-3.5 bg-rose-50 border border-rose-200 rounded-xl text-xs text-rose-700 font-medium animate-fade-in">
              {errorMessage}
            </div>
          )}

          <form onSubmit={handleLogin} className="space-y-4">
            {/* Email Field */}
            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-gray-700 mb-1.5">
                Work Email Address
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-gray-400">
                  <Mail className="w-4 h-4" />
                </div>
                <input
                  type="email"
                  required
                  value={email}
                  onChange={e => setEmail(e.target.value)}
                  placeholder="name@retailstore.com"
                  className="w-full pl-10 pr-4 py-2.5 bg-gray-50 border border-gray-300 rounded-lg text-sm text-gray-900 focus:bg-white focus:outline-none focus:ring-2 focus:ring-[#0066CC] focus:border-[#0066CC] transition-all"
                />
              </div>
            </div>

            {/* Password Field */}
            <div>
              <div className="flex items-center justify-between mb-1.5">
                <label className="block text-xs font-bold uppercase tracking-wider text-gray-700">
                  Password
                </label>
                <span className="text-xs text-[#0066CC] hover:underline cursor-pointer font-medium">
                  Forgot?
                </span>
              </div>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-gray-400">
                  <Lock className="w-4 h-4" />
                </div>
                <input
                  type={showPassword ? 'text' : 'password'}
                  required
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  placeholder="••••••••"
                  className="w-full pl-10 pr-10 py-2.5 bg-gray-50 border border-gray-300 rounded-lg text-sm text-gray-900 focus:bg-white focus:outline-none focus:ring-2 focus:ring-[#0066CC] focus:border-[#0066CC] transition-all"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute inset-y-0 right-0 pr-3.5 flex items-center text-gray-400 hover:text-gray-600 focus:outline-none"
                  tabIndex={-1}
                >
                  {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button>
              </div>
            </div>

            {/* Remember Me */}
            <div className="flex items-center justify-between pt-1">
              <label className="flex items-center gap-2 cursor-pointer text-xs text-gray-600 font-medium select-none">
                <input
                  type="checkbox"
                  checked={rememberMe}
                  onChange={e => setRememberMe(e.target.checked)}
                  className="w-4 h-4 text-[#0066CC] border-gray-300 rounded focus:ring-blue-400"
                />
                <span>Remember this workstation</span>
              </label>

              <span className="text-[11px] text-emerald-600 flex items-center gap-1 font-semibold">
                <ShieldCheck className="w-3.5 h-3.5" />
                <span>SSL Encrypted</span>
              </span>
            </div>

            {/* Submit Button */}
            <button
              type="submit"
              disabled={loading}
              className="w-full py-2.5 px-4 bg-[#0066CC] hover:bg-[#1E40AF] disabled:bg-blue-400 text-white text-sm font-semibold rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-300 transition-colors flex items-center justify-center gap-2 mt-2"
            >
              {loading ? (
                <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
              ) : (
                <>
                  <span>Sign In to Dashboard</span>
                  <ArrowRight className="w-4 h-4" />
                </>
              )}
            </button>
          </form>

          {/* Quick Demo Accounts Selection */}
          <div className="pt-4 border-t border-gray-100 space-y-2.5">
            <div className="text-[11px] font-bold uppercase tracking-wider text-gray-400 flex items-center gap-1">
              <UserCheck className="w-3.5 h-3.5" />
              <span>Quick Demo Roles:</span>
            </div>
            <div className="grid grid-cols-2 gap-2">
              <button
                type="button"
                onClick={() => handleQuickFill('manager@retailx.com', 'retailx2026')}
                className="px-3 py-2 bg-gray-50 hover:bg-blue-50 border border-gray-200 hover:border-blue-200 rounded-lg text-left transition-colors"
              >
                <div className="text-xs font-bold text-gray-800">Store Manager</div>
                <div className="text-[10px] text-gray-500 font-mono">manager@retailx.com</div>
              </button>

              <button
                type="button"
                onClick={() => handleQuickFill('admin@retailx.com', 'admin2026')}
                className="px-3 py-2 bg-gray-50 hover:bg-blue-50 border border-gray-200 hover:border-blue-200 rounded-lg text-left transition-colors"
              >
                <div className="text-xs font-bold text-gray-800">Admin Account</div>
                <div className="text-[10px] text-gray-500 font-mono">admin@retailx.com</div>
              </button>
            </div>
          </div>
        </div>

        {/* Back to Dashboard link */}
        <div className="text-center text-xs text-gray-500">
          <Link to="/" className="text-[#0066CC] hover:underline font-semibold">
            ← Return to Dashboard as Guest
          </Link>
        </div>
      </div>
    </div>
  );
};
export default Login;
