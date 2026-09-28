export const APP_NAME = 'RetailX Inventory';

export const MOVEMENT_TYPE_COLORS = {
  PURCHASE: {
    bg: 'bg-blue-50',
    text: 'text-blue-700',
    border: 'border-blue-200',
    icon: '↑',
    badge: 'bg-blue-100 text-blue-800',
  },
  SALE: {
    bg: 'bg-red-50',
    text: 'text-red-700',
    border: 'border-red-200',
    icon: '↓',
    badge: 'bg-red-100 text-red-800',
  },
  RETURN: {
    bg: 'bg-green-50',
    text: 'text-green-700',
    border: 'border-green-200',
    icon: '↺',
    badge: 'bg-green-100 text-green-800',
  },
  DAMAGE: {
    bg: 'bg-orange-50',
    text: 'text-orange-700',
    border: 'border-orange-200',
    icon: '⚠',
    badge: 'bg-orange-100 text-orange-800',
  },
};

export const STOCK_STATUS_CONFIG = {
  IN_STOCK: {
    label: 'IN_STOCK',
    badge: 'bg-green-100 text-green-800 border-green-200',
    dot: 'bg-green-500',
  },
  LOW_STOCK: {
    label: 'LOW_STOCK',
    badge: 'bg-orange-100 text-orange-800 border-orange-200',
    dot: 'bg-orange-500',
  },
  OUT_OF_STOCK: {
    label: 'OUT_OF_STOCK',
    badge: 'bg-red-100 text-red-800 border-red-200',
    dot: 'bg-red-500',
  },
};

export const ALERT_STATUS_CONFIG = {
  OPEN: {
    label: 'OPEN',
    badge: 'bg-orange-100 text-orange-800 border-orange-200',
  },
  FULFILLED: {
    label: 'FULFILLED',
    badge: 'bg-green-100 text-green-800 border-green-200',
  },
};
