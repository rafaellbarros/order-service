-- Inserir registros na tabela orders
INSERT INTO orders (external_id, status, total_value) VALUES
                                                          ('ORD-001', 'PROCESSED', 150.50),
                                                          ('ORD-002', 'PROCESSED', 200.00),
                                                          ('ORD-003', 'PENDING', 300.75),
                                                          ('ORD-004', 'CANCELED', 0.00),
                                                          ('ORD-005', 'PROCESSED', 450.00),
                                                          ('ORD-006', 'PENDING', 120.30),
                                                          ('ORD-007', 'PROCESSED', 700.00),
                                                          ('ORD-008', 'PENDING', 80.90),
                                                          ('ORD-009', 'CANCELED', 0.00),
                                                          ('ORD-010', 'PROCESSED', 500.50);

-- Inserir registros na tabela products
INSERT INTO products (order_id, name, price, quantity) VALUES
                                                           (1, 'Product A', 50.25, 2),
                                                           (1, 'Product B', 25.00, 2),
                                                           (2, 'Product C', 100.00, 2),
                                                           (3, 'Product D', 150.25, 1),
                                                           (3, 'Product E', 75.50, 2),
                                                           (5, 'Product F', 225.00, 2),
                                                           (6, 'Product G', 60.15, 2),
                                                           (7, 'Product H', 350.00, 2),
                                                           (8, 'Product I', 40.45, 2),
                                                           (10, 'Product J', 250.25, 2);
