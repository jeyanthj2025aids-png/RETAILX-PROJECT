import api from './api';
import type { ReorderAlert } from '../types';

export const alertService = {
  getAll: async (status?: 'OPEN' | 'FULFILLED', page = 0, size = 20): Promise<ReorderAlert[]> => {
    const params: { status?: string; page: number; size: number } = { page, size };
    if (status) {
      params.status = status;
    }
    const response = await api.get<ReorderAlert[]>('/reorder-alerts', { params });
    return response.data;
  },

  getOpen: async (page = 0, size = 20): Promise<ReorderAlert[]> => {
    const response = await api.get<ReorderAlert[]>('/reorder-alerts/open', {
      params: { page, size },
    });
    return response.data;
  },

  getById: async (id: number): Promise<ReorderAlert> => {
    const response = await api.get<ReorderAlert>(`/reorder-alerts/${id}`);
    return response.data;
  },

  fulfill: async (id: number): Promise<ReorderAlert> => {
    const response = await api.put<ReorderAlert>(`/reorder-alerts/${id}/fulfill`, {});
    return response.data;
  },
};
