import api from './api';
import type { FastMovingProduct } from '../types';

export const reportService = {
  getFastMovingProducts: async (startDate: string, endDate: string): Promise<FastMovingProduct[]> => {
    const response = await api.get<FastMovingProduct[]>('/reports/fast-moving-products', {
      params: { startDate, endDate },
    });
    return response.data;
  },
};
