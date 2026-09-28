-- Sample Products
INSERT INTO products (name, sku, reorder_threshold, reorder_quantity) VALUES
('Coca-Cola 500ml', 'COKE500', 20, 100),
('Parle-G Biscuits', 'PARLEG100', 15, 80),
('Lux Soap', 'LUX125', 10, 50),
('Head & Shoulders Shampoo', 'HS200', 8, 40),
('Aashirvaad Rice 5kg', 'AARRICE5', 12, 60);

-- Sample Stock Movements
INSERT INTO stock_movements (product_id, movement_type, quantity, movement_date) VALUES
(1, 'PURCHASE', 150, '2026-09-01'),
(1, 'SALE', 20, '2026-09-02'),
(1, 'SALE', 15, '2026-09-03'),
(1, 'DAMAGE', 3, '2026-09-04'),
(2, 'PURCHASE', 100, '2026-09-01'),
(2, 'SALE', 30, '2026-09-05'),
(2, 'SALE', 25, '2026-09-06'),
(2, 'SALE', 30, '2026-09-07'),
(3, 'PURCHASE', 80, '2026-09-01'),
(3, 'SALE', 20, '2026-09-08'),
(4, 'PURCHASE', 50, '2026-09-01'),
(4, 'SALE', 18, '2026-09-09'),
(4, 'SALE', 15, '2026-09-10'),
(5, 'PURCHASE', 120, '2026-09-01'),
(5, 'SALE', 50, '2026-09-11'),
(5, 'SALE', 40, '2026-09-12'),
(5, 'SALE', 25, '2026-09-13');

-- Reorder Alerts triggered by sample movements
-- Product 2 (Parle-G): 100 - 30 - 25 - 30 = 15 (equals threshold 15)
INSERT INTO reorder_alerts (product_id, current_stock_at_alert, reorder_threshold_at_alert, reorder_quantity, status, created_at) VALUES
(2, 15, 15, 80, 'OPEN', CURRENT_TIMESTAMP);

-- Product 5 (Aashirvaad Rice): 120 - 50 - 40 - 25 = 5 (below threshold 12)
INSERT INTO reorder_alerts (product_id, current_stock_at_alert, reorder_threshold_at_alert, reorder_quantity, status, created_at) VALUES
(5, 5, 12, 60, 'OPEN', CURRENT_TIMESTAMP);
