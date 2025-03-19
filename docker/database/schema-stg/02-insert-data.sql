-- Inserir registros na tabela orders
INSERT INTO orders (external_id, status, total_value) VALUES
    ('ORD-001', 'PROCESSED', 150.50),
    ('ORD-002', 'PROCESSED', 320.00),
    ('ORD-003', 'PENDING', 95.75),
    ('ORD-004', 'CANCELED', 0.00),
    ('ORD-005', 'PROCESSED', 450.00),
    ('ORD-006', 'PENDING', 180.30),
    ('ORD-007', 'PROCESSED', 710.00),
    ('ORD-008', 'PENDING', 50.90),
    ('ORD-009', 'CANCELED', 0.00),
    ('ORD-010', 'PROCESSED', 620.50),
    ('ORD-011', 'PROCESSED', 980.00),
    ('ORD-012', 'PENDING', 75.00),
    ('ORD-013', 'CANCELED', 0.00),
    ('ORD-014', 'PROCESSED', 210.25),
    ('ORD-015', 'PROCESSED', 530.75),
    ('ORD-016', 'PENDING', 160.00),
    ('ORD-017', 'CANCELED', 0.00),
    ('ORD-018', 'PROCESSED', 340.90),
    ('ORD-019', 'PROCESSED', 275.50),
    ('ORD-020', 'PENDING', 120.40);

-- Inserir registros na tabela products
INSERT INTO products (order_id, name, price, quantity) VALUES
    -- Produtos dos pedidos existentes
    (1, 'Smartphone', 75.25, 2),
    (1, 'Capa Protetora', 10.00, 3),
    (2, 'Notebook', 320.00, 1),
    (3, 'Fone Bluetooth', 47.50, 2),
    (3, 'Cabo USB-C', 12.75, 2),
    (5, 'Monitor 27"', 225.00, 2),
    (6, 'Teclado Mecânico', 90.15, 2),
    (7, 'Smart TV 50"', 710.00, 1),
    (8, 'Mouse Sem Fio', 25.45, 2),
    (10, 'Cadeira Gamer', 620.50, 1),

    -- Produtos dos novos pedidos
    (11, 'iPhone 15 Pro', 980.00, 1),
    (12, 'Carregador Turbo', 25.00, 3),
    (14, 'Tablet Samsung', 210.25, 1),
    (15, 'Placa de Vídeo RTX 4060', 530.75, 1),
    (16, 'Headset Gamer', 80.00, 2),
    (18, 'Impressora Multifuncional', 340.90, 1),
    (19, 'HD Externo 1TB', 137.75, 2),
    (19, 'Pendrive 64GB', 20.00, 2),
    (20, 'Webcam Full HD', 60.20, 2),
    (20, 'Hub USB 3.0', 30.10, 2);
