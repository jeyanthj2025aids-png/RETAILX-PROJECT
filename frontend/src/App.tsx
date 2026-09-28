import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from './layouts/MainLayout';
import Dashboard from './pages/Dashboard';
import Products from './pages/Products';
import StockMovements from './pages/StockMovements';
import ReorderAlerts from './pages/ReorderAlerts';
import FastMovingReport from './pages/FastMovingReport';

export const App: React.FC = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<MainLayout />}>
          <Route index element={<Dashboard />} />
          <Route path="products" element={<Products />} />
          <Route path="stock-movements" element={<StockMovements />} />
          <Route path="movements" element={<Navigate to="/stock-movements" replace />} />
          <Route path="reorder-alerts" element={<ReorderAlerts />} />
          <Route path="alerts" element={<Navigate to="/reorder-alerts" replace />} />
          <Route path="reports" element={<FastMovingReport />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
};

export default App;
