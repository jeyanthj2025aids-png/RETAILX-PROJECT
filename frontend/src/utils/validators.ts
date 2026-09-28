export const isValidSku = (sku: string): boolean => {
  return Boolean(sku && sku.trim().length >= 2);
};

export const isPositiveNumber = (val: number | string): boolean => {
  const n = Number(val);
  return !isNaN(n) && n > 0;
};

export const isNonNegativeNumber = (val: number | string): boolean => {
  const n = Number(val);
  return !isNaN(n) && n >= 0;
};

export const isValidDate = (dateStr: string): boolean => {
  if (!dateStr) return false;
  const d = new Date(dateStr);
  return !isNaN(d.getTime());
};
