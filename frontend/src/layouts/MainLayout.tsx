import React, { useState, useEffect } from 'react';
import { Outlet } from 'react-router-dom';
import Header from '../components/Header';
import Toast, { type ToastMessage } from '../components/Toast';
import { alertService } from '../services/alertService';

export const MainLayout: React.FC = () => {
  const [openAlertCount, setOpenAlertCount] = useState<number>(0);
  const [toasts, setToasts] = useState<ToastMessage[]>([]);

  const fetchOpenAlertCount = async () => {
    try {
      const openAlerts = await alertService.getOpen(0, 100);
      setOpenAlertCount(openAlerts.length);
    } catch {
      // ignore in background polling
    }
  };

  useEffect(() => {
    fetchOpenAlertCount();
    const interval = setInterval(fetchOpenAlertCount, 15000);
    return () => clearInterval(interval);
  }, []);

  const addToast = (type: 'success' | 'error' | 'warning' | 'info', message: string) => {
    const id = Date.now().toString() + Math.random().toString(36).substring(2, 5);
    setToasts(prev => [...prev, { id, type, message }]);
  };

  const removeToast = (id: string) => {
    setToasts(prev => prev.filter(t => t.id !== id));
  };

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col selection:bg-blue-500 selection:text-white">
      {/* Toast Notification Container */}
      <Toast toasts={toasts} onDismiss={removeToast} />

      {/* Top Header Navigation */}
      <Header openAlertCount={openAlertCount} />

      {/* Main Content Viewport */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Outlet context={{ addToast, refreshAlertCount: fetchOpenAlertCount }} />
      </main>

      {/* Footer */}
      <footer className="bg-white border-t border-gray-200 py-5 text-center text-xs text-gray-500">
        <div className="max-w-7xl mx-auto px-4 flex flex-col sm:flex-row items-center justify-between gap-2">
          <div>
            <span className="font-semibold text-gray-700">RetailX Inventory System</span> — Real-time Ledger & Automated Reorder Engine
          </div>
          <div className="flex items-center gap-4 text-gray-400">
            <span>MySQL / H2 Dynamic Balance</span>
            <span>•</span>
            <span>REST API v1.0</span>
          </div>
        </div>
      </footer>
    </div>
  );
};
export default MainLayout;
