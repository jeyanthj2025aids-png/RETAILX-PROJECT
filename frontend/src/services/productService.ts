import api from './api';
import type { Product, ProductFormData, Stock } from '../types';

export const productService = {
  getAll: async (page = 0, size = 20): Promise<Product[]> => {
    const response = await api.get<Product[]>('/products', {
      params: { page, size },
    });
    return response.data;
  },

  getById: async (id: number): Promise<Product> => {
    const response = await api.get<Product>(`/products/${id}`);
    return response.data;
  },

  create: async (data: ProductFormData): Promise<Product> => {
    const payload = {
      name: data.name.trim(),
      sku: data.sku.trim().toUpperCase(),
      reorderThreshold: Number(data.reorderThreshold),
      reorderQuantity: Number(data.reorderQuantity),
    };
    const response = await api.post<Product>('/products', payload);
    return response.data;
  },

  update: async (id: number, data: ProductFormData): Promise<Product> => {
    const payload = {
      name: data.name.trim(),
      sku: data.sku.trim().toUpperCase(),
      reorderThreshold: Number(data.reorderThreshold),
      reorderQuantity: Number(data.reorderQuantity),
    };
    const response = await api.put<Product>(`/products/${id}`, payload);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/products/${id}`);
  },

  getStock: async (id: number): Promise<Stock> => {
    const response = await api.get<Stock>(`/products/${id}/stock`);
    return response.data;
  },
};
