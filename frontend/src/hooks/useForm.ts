import { useState, type ChangeEvent } from 'react';

export function useForm<T extends Record<string, unknown>>(initialValues: T) {
  const [values, setValues] = useState<T>(initialValues);
  const [errors, setErrors] = useState<Partial<Record<keyof T, string>>>({});

  const handleChange = (
    e: ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>
  ) => {
    const { name, value, type } = e.target;
    const finalValue = type === 'number' && value !== '' ? Number(value) : value;

    setValues(prev => ({
      ...prev,
      [name]: finalValue,
    }));

    // Clear error on change
    if (errors[name as keyof T]) {
      setErrors(prev => ({
        ...prev,
        [name]: undefined,
      }));
    }
  };

  const reset = (newValues?: T) => {
    setValues(newValues || initialValues);
    setErrors({});
  };

  const setFieldError = (field: keyof T, message: string) => {
    setErrors(prev => ({
      ...prev,
      [field]: message,
    }));
  };

  return {
    values,
    setValues,
    errors,
    setErrors,
    handleChange,
    reset,
    setFieldError,
  };
}
