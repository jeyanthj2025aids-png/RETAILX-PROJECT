import api from './api';
import type { StockMovement, StockMovementFormData, StockMovementResponse } from '../types';

export const movementService = {
  getAll: async (page = 0, size = 20): Promise<StockMovement[]> => {
    const response = await api.get<StockMovement[]>('/stock-movements', {
      params: { page, size },
    });
    return response.data;
  },

  getByProduct: async (productId: number, page = 0, size = 20): Promise<StockMovement[]> => {
    const response = await api.get<StockMovement[]>(`/stock-movements/product/${productId}`, {
      params: { page, size },
    });
    return response.data;
  },

  record: async (data: StockMovementFormData): Promise<StockMovementResponse> => {
    const payload = {
      productId: Number(data.productId),
      movementType: data.movementType,
      quantity: Number(data.quantity),
      movementDate: data.movementDate,
    };
    const response = await api.post<StockMovementResponse>('/stock-movements', payload);
    return response.data;
  },
};
