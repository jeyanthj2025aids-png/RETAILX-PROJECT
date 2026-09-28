// Product
export interface Product {
  id: number;
  name: string;
  sku: string;
  reorderThreshold: number;
  reorderQuantity: number;
  createdAt: string;
  updatedAt: string;
  currentStock?: number;
  status?: 'IN_STOCK' | 'LOW_STOCK' | 'OUT_OF_STOCK';
  lowStock?: boolean;
  hasOpenAlert?: boolean;
}

// Stock Movement
export interface StockMovement {
  id: number;
  productId: number;
  productName?: string;
  productSku?: string;
  movementType: 'PURCHASE' | 'SALE' | 'RETURN' | 'DAMAGE';
  quantity: number;
  movementDate: string;
  createdAt: string;
  currentStockAfter?: number;
}

// Reorder Alert
export interface ReorderAlert {
  id: number;
  productId: number;
  productName?: string;
  productSku?: string;
  currentStockAtAlert: number;
  reorderThresholdAtAlert: number;
  reorderQuantity: number;
  currentStockNow?: number;
  status: 'OPEN' | 'FULFILLED';
  createdAt: string;
  fulfilledAt: string | null;
}

// Dashboard Summary
export interface DashboardSummary {
  totalProducts: number;
  totalStockUnits: number;
  openReorderAlerts: number;
  productsLowStock: number;
  recentMovements: StockMovement[];
  openAlerts: ReorderAlert[];
  topFastMovingProducts: FastMovingProduct[];
}

// Fast Moving Product
export interface FastMovingProduct {
  rank: number;
  productId: number;
  name: string;
  sku: string;
  unitsSold: number;
}

// API Response for Stock Movement
export interface StockMovementResponse {
  movement: StockMovement;
  currentStock: number;
  reorderAlertCreated: boolean;
  alertId: number | null;
}

// Stock with status
export interface Stock {
  productId: number;
  currentStock: number;
  reorderThreshold: number;
  reorderQuantity: number;
  status: 'IN_STOCK' | 'LOW_STOCK' | 'OUT_OF_STOCK';
}

// Error Response
export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}

// Product Form Input
export interface ProductFormData {
  name: string;
  sku: string;
  reorderThreshold: number | string;
  reorderQuantity: number | string;
}

// Stock Movement Form Input
export interface StockMovementFormData {
  productId: number | string;
  movementType: 'PURCHASE' | 'SALE' | 'RETURN' | 'DAMAGE' | '';
  quantity: number | string;
  movementDate: string;
}
