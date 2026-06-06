-- =============================================================================
-- 1. ЗАПОЛНЕНИЕ ТАРИФНЫХ ПЛАНОВ
-- =============================================================================
INSERT INTO tariffs (name, price, speed_mbps, description, is_active) VALUES 
('Социальный Стартер', 12.00, 15, 'Для пенсионеров и базовых нужд. Стабильный доступ сервисам.', TRUE),
('Базовый Дом', 18.00, 60, 'Оптимальный выбор для повседневного серфинга, работы и соцсетей.', TRUE),
('Стандарт Плюс', 24.00, 100, 'Комфортная скорость для одновременного просмотра фильмов в FullHD.', TRUE),
('Семейный Киноман 4K', 32.00, 250, 'Высокая скорость для большой семьи: стриминг в 4K и онлайн-игры.', TRUE),
('Геймерский Ultra Pro', 40.00, 600, 'Максимальный приоритет трафика, минимальный пинг и ночной безлимит.', TRUE),
('Гигабит Бизнес', 60.00, 1000, 'Ультимативная скорость для самых требовательных пользователей и серверов.', TRUE),
('Архивный Эконом 2024', 10.00, 10, 'Старый тарифный план, закрыт для новых подключений с января.', FALSE);

-- =============================================================================
-- 2. ЗАПОЛНЕНИЕ АДМИНИСТРАТОРОВ
-- =============================================================================
INSERT INTO users (username, password, email, role, balance, is_blocked, tariff_id) VALUES 
('admin_chief', '$2a$10$fD3rPb14BY5hazz6ob1U0u.OuC0aD905v.9xLM6wSbHWQbdEg1JNq', 'chief@provider.com', 'ADMIN', 0.00, FALSE, NULL),
('admin_support', '$2a$10$fD3rPb14BY5hazz6ob1U0u.OuC0aD905v.9xLM6wSbHWQbdEg1JNq', 'support@provider.com', 'ADMIN', 50.00, FALSE, NULL);

-- =============================================================================
-- 3. ЗАПОЛНЕНИЕ КЛИЕНТОВ (25 пользователей для тестирования пагинации)
-- =============================================================================
INSERT INTO users (username, password, email, role, balance, is_blocked, tariff_id) VALUES 
('ivanov_ivan', '$2a$10$fD3rPb14BY5hazz6ob1U0u.OuC0aD905v.9xLM6wSbHWQbdEg1JNq', 'ivanov@mail.ru', 'CLIENT', 15.50, FALSE, (SELECT id FROM tariffs WHERE name = 'Базовый Дом')),
('petrov_petr', '$2a$10$fD3rPb14BY5hazz6ob1U0u.OuC0aD905v.9xLM6wSbHWQbdEg1JNq', 'petrov@gmail.com', 'CLIENT', -12.40, TRUE, (SELECT id FROM tariffs WHERE name = 'Геймерский Ultra Pro')),
('sidorov_alex', '$2a$10$fD3rPb14BY5hazz6ob1U0u.OuC0aD905v.9xLM6wSbHWQbdEg1JNq', 'sidorov@yandex.ru', 'CLIENT', 45.00, FALSE, (SELECT id FROM tariffs WHERE name = 'Стандарт Плюс')),
('smirnov_dmitry', '$2a$10$fD3rPb14BY5hazz6ob1U0u.OuC0aD905v.9xLM6wSbHWQbdEg1JNq', 'smirnov@mail.ru', 'CLIENT', 2.00, FALSE, (SELECT id FROM tariffs WHERE name = 'Социальный Стартер')),
('kuznetsov_serg', '$2a$10$fD3rPb14BY5hazz6ob1U0u.OuC0aD905v.9xLM6wSbHWQbdEg1JNq', 'kuznetsov@gmail.com', 'CLIENT', 140.00, FALSE, (SELECT id FROM tariffs WHERE name = 'Гигабит Бизнес')),
('popov_andrey', '$2a$10$fD3rPb14BY5hazz6ob1U0u.OuC0aD905v.9xLM6wSbHWQbdEg1JNq', 'popov@yandex.ru', 'CLIENT', -35.00, TRUE, (SELECT id FROM tariffs WHERE name = 'Семейный Киноман 4K')),
('vasiliev_vlad', '$2a$10$fD3rPb14BY5hazz6ob1U0u.OuC0aD905v.9xLM6wSbHWQbdEg1JNq', 'vasiliev@mail.ru', 'CLIENT', 8.40, FALSE, (SELECT id FROM tariffs WHERE name = 'Базовый Дом')),
('sokolov_elena', '$2a$10$fD3rPb14BY5hazz6ob1U0u.OuC0aD905v.9xLM6wSbHWQbdEg1JNq', 'sokolova@gmail.com', 'CLIENT', 32.00, FALSE, (SELECT id FROM tariffs WHERE name = 'Стандарт Плюс')),
('mihailov_mih', '$2a$10$fD3rPb14BY5hazz6ob1U0u.OuC0aD905v.9xLM6wSbHWQbdEg1JNq', 'mihailov@yandex.ru', 'CLIENT', 1.10, FALSE, (SELECT id FROM tariffs WHERE name = 'Геймерский Ultra Pro')),
('novikov_artem', '$2a$10$fD3rPb14BY5hazz6ob1U0u.OuC0aD905v.9xLM6wSbHWQbdEg1JNq', 'novikov@mail.ru', 'CLIENT', -1.50, FALSE, (SELECT id FROM tariffs WHERE name = 'Социальный Стартер')),
('fedorov_denis', '$2a$10$fD3rPb14BY5hazz6ob1U0u.OuC0aD905v.9xLM6wSbHWQbdEg1JNq', 'fedorov@gmail.com', 'CLIENT', 75.00, FALSE, (SELECT id FROM tariffs WHERE name = 'Семейный Киноман 4K')),
('morozov_igor', '$2a$10$fD3rPb14BY5hazz6ob1U0u.OuC0aD905v.9xLM6wSbHWQbdEg1JNq', 'morozov@yandex.ru', 'CLIENT', 14.00, FALSE, (SELECT id FROM tariffs WHERE name = 'Базовый Дом')),
('volkov_oleg', '$2a$10$fD3rPb14BY5hazz6ob1U0u.OuC0aD905v.9xLM6wSbHWQbdEg1JNq', 'volkov@mail.ru', 'CLIENT', -60.00, TRUE, (SELECT id FROM tariffs WHERE name = 'Гигабит Бизнес')),
('lebedev_pavel', '$2a$10$fD3rPb14BY5hazz6ob1U0u.OuC0aD905v.9xLM6wSbHWQbdEg1JNq', 'lebedev@gmail.com', 'CLIENT', 22.80, FALSE, (SELECT id FROM tariffs WHERE name = 'Стандарт Плюс')),
('semenov_anton', '$2a$10$fD3rPb14BY5hazz6ob1U0u.OuC0aD905v.9xLM6wSbHWQbdEg1JNq', 'semenov@yandex.ru', 'CLIENT', 4.50, FALSE, (SELECT id FROM tariffs WHERE name = 'Базовый Дом')),
('egorov_maxim', '$2a$10$fD3rPb14BY5hazz6ob1U0u.OuC0aD905v.9xLM6wSbHWQbdEg1JNq', 'egorov@mail.ru', 'CLIENT', 0.50, FALSE, (SELECT id FROM tariffs WHERE name = 'Социальный Стартер')),
('kozlov_roman', '$2a$10$fD3rPb14BY5hazz6ob1U0u.OuC0aD905v.9xLM6wSbHWQbdEg1JNq', 'kozlov@gmail.com', 'CLIENT', -2.10, TRUE, (SELECT id FROM tariffs WHERE name = 'Геймерский Ultra Pro')),
('stepanov_ilya', '$2a$10$fD3rPb14BY5hazz6ob1U0u.OuC0aD905v.9xLM6wSbHWQbdEg1JNq', 'stepanov@yandex.ru', 'CLIENT', 90.00, FALSE, (SELECT id FROM tariffs WHERE name = 'Семейный Киноман 4K')),
('nikolaev_nik', '$2a$10$fD3rPb14BY5hazz6ob1U0u.OuC0aD905v.9xLM6wSbHWQbdEg1JNq', 'nikolaev@mail.ru', 'CLIENT', 18.00, FALSE, (SELECT id FROM tariffs WHERE name = 'Стандарт Плюс')),
('orlov_kirill', '$2a$10$fD3rPb14BY5hazz6ob1U0u.OuC0aD905v.9xLM6wSbHWQbdEg1JNq', 'orlov@gmail.com', 'CLIENT', 110.00, FALSE, (SELECT id FROM tariffs WHERE name = 'Гигабит Бизнес')),
('andreeva_anna', '$2a$10$fD3rPb14BY5hazz6ob1U0u.OuC0aD905v.9xLM6wSbHWQbdEg1JNq', 'andreeva@mail.ru', 'CLIENT', 25.00, FALSE, (SELECT id FROM tariffs WHERE name = 'Базовый Дом')),
('pavlova_olga', '$2a$10$fD3rPb14BY5hazz6ob1U0u.OuC0aD905v.9xLM6wSbHWQbdEg1JNq', 'pavlova@gmail.com', 'CLIENT', 0.00, FALSE, (SELECT id FROM tariffs WHERE name = 'Стандарт Плюс')),
('romanov_roma', '$2a$10$fD3rPb14BY5hazz6ob1U0u.OuC0aD905v.9xLM6wSbHWQbdEg1JNq', 'romanov@yandex.ru', 'CLIENT', -8.90, FALSE, (SELECT id FROM tariffs WHERE name = 'Семейный Киноман 4K')),
('morozova_taty', '$2a$10$fD3rPb14BY5hazz6ob1U0u.OuC0aD905v.9xLM6wSbHWQbdEg1JNq', 'morozovat@mail.ru', 'CLIENT', 55.00, FALSE, (SELECT id FROM tariffs WHERE name = 'Геймерский Ultra Pro')),
('soloviev_max', '$2a$10$fD3rPb14BY5hazz6ob1U0u.OuC0aD905v.9xLM6wSbHWQbdEg1JNq', 'soloviev@gmail.com', 'CLIENT', 13.20, FALSE, (SELECT id FROM tariffs WHERE name = 'Архивный Эконом 2024'));

-- =============================================================================
-- 4. ЗАПОЛНЕНИЕ АКЦИЙ И СКИДОК
-- =============================================================================
INSERT INTO promotions (title, discount_percentage, end_date, description) VALUES 
('Летний Ускоритель', 20, '2026-08-31', 'Подключите тариф Гигабит Бизнес до конца сезона и получите скидку 20% на абонентскую плату!'),
('Домашний Комфорт', 15, '2026-07-15', 'Скидка 15% на тариф Стандарт Плюс для всех новых абонентов компании.'),
('Геймерский Старт', 25, '2026-09-30', 'Специальные условия для киберспортсменов: 25% скидки на тариф Ultra Pro.');

-- Привязка акций к тарифам (Связующая таблица) [1]
INSERT INTO tariff_promotions (tariff_id, promotion_id) VALUES
((SELECT id FROM tariffs WHERE name = 'Гигабит Бизнес'), (SELECT id FROM promotions WHERE title = 'Летний Ускоритель')),
((SELECT id FROM tariffs WHERE name = 'Стандарт Плюс'), (SELECT id FROM promotions WHERE title = 'Домашний Комфорт')),
((SELECT id FROM tariffs WHERE name = 'Геймерский Ultra Pro'), (SELECT id FROM promotions WHERE title = 'Геймерский Старт'));

-- =============================================================================
-- 5. ИСТОРИЯ ПЛАТЕЖЕЙ (Симуляция пополнений за Май 2026)
-- =============================================================================
INSERT INTO payments (user_id, amount, payment_date) VALUES 
((SELECT id FROM users WHERE username = 'ivanov_ivan'), 20.00, '2026-05-02 10:15:00'),
((SELECT id FROM users WHERE username = 'ivanov_ivan'), 15.00, '2026-05-28 17:40:00'),
((SELECT id FROM users WHERE username = 'sidorov_alex'), 50.00, '2026-05-01 09:00:00'),
((SELECT id FROM users WHERE username = 'kuznetsov_serg'), 100.00, '2026-05-03 14:20:00'),
((SELECT id FROM users WHERE username = 'kuznetsov_serg'), 100.00, '2026-05-25 11:10:00'),
((SELECT id FROM users WHERE username = 'sokolov_elena'), 40.00, '2026-05-05 19:33:00'),
((SELECT id FROM users WHERE username = 'fedorov_denis'), 80.00, '2026-05-10 12:00:00'),
((SELECT id FROM users WHERE username = 'morozov_igor'), 25.00, '2026-05-01 08:45:00'),
((SELECT id FROM users WHERE username = 'lebedev_pavel'), 30.00, '2026-05-12 21:05:00'),
((SELECT id FROM users WHERE username = 'stepanov_ilya'), 120.00, '2026-05-02 16:14:00'),
((SELECT id FROM users WHERE username = 'orlov_kirill'), 150.00, '2026-05-01 00:05:00'),
((SELECT id FROM users WHERE username = 'andreeva_anna'), 30.00, '2026-05-18 13:22:00'),
((SELECT id FROM users WHERE username = 'morozova_taty'), 60.00, '2026-05-04 15:50:00');

-- =============================================================================
-- 6. ИСТОРИЯ ПОТРЕБЛЕНИЯ ТРАФИКА (Апрель и Май 2026) [1]
-- =============================================================================
INSERT INTO traffic_stats (user_id, billing_period, traffic_consumed_gb) VALUES 
-- Клиент ivanov_ivan (Базовый Дом)
((SELECT id FROM users WHERE username = 'ivanov_ivan'), '2026-04-01', 98.40),
((SELECT id FROM users WHERE username = 'ivanov_ivan'), '2026-05-01', 115.20),
-- Клиент petrov_petr (Геймерский Ultra)
((SELECT id FROM users WHERE username = 'petrov_petr'), '2026-04-01', 412.00),
((SELECT id FROM users WHERE username = 'petrov_petr'), '2026-05-01', 524.80),
-- Клиент sidorov_alex (Стандарт Плюс)
((SELECT id FROM users WHERE username = 'sidorov_alex'), '2026-04-01', 145.10),
((SELECT id FROM users WHERE username = 'sidorov_alex'), '2026-05-01', 160.30),
-- Клиент kuznetsov_serg (Гигабит Бизнес)
((SELECT id FROM users WHERE username = 'kuznetsov_serg'), '2026-04-01', 890.00),
((SELECT id FROM users WHERE username = 'kuznetsov_serg'), '2026-05-01', 1045.50),
-- Клиент popov_andrey (Семейный Киноман)
((SELECT id FROM users WHERE username = 'popov_andrey'), '2026-04-01', 280.45),
((SELECT id FROM users WHERE username = 'popov_andrey'), '2026-05-01', 310.15),
-- Наполнение для остальных клиентов за Май, чтобы сформировать отчеты
((SELECT id FROM users WHERE username = 'smirnov_dmitry'), '2026-05-01', 22.40),
((SELECT id FROM users WHERE username = 'vasiliev_vlad'), '2026-05-01', 85.30),
((SELECT id FROM users WHERE username = 'sokolov_elena'), '2026-05-01', 195.00),
((SELECT id FROM users WHERE username = 'mihailov_mih'), '2026-05-01', 489.12),
((SELECT id FROM users WHERE username = 'novikov_artem'), '2026-05-01', 41.50),
((SELECT id FROM users WHERE username = 'fedorov_denis'), '2026-05-01', 230.00),
((SELECT id FROM users WHERE username = 'morozov_igor'), '2026-05-01', 102.40),
((SELECT id FROM users WHERE username = 'volkov_oleg'), '2026-05-01', 750.20),
((SELECT id FROM users WHERE username = 'lebedev_pavel'), '2026-05-01', 134.60),
((SELECT id FROM users WHERE username = 'semenov_anton'), '2026-05-01', 94.10),
((SELECT id FROM users WHERE username = 'egorov_maxim'), '2026-05-01', 15.80),
((SELECT id FROM users WHERE username = 'kozlov_roman'), '2026-05-01', 390.00),
((SELECT id FROM users WHERE username = 'stepanov_ilya'), '2026-05-01', 299.40),
((SELECT id FROM users WHERE username = 'nikolaev_nik'), '2026-05-01', 142.10),
((SELECT id FROM users WHERE username = 'orlov_kirill'), '2026-05-01', 920.50),
((SELECT id FROM users WHERE username = 'andreeva_anna'), '2026-05-01', 70.30),
((SELECT id FROM users WHERE username = 'pavlova_olga'), '2026-05-01', 110.00),
((SELECT id FROM users WHERE username = 'romanov_roma'), '2026-05-01', 185.40),
((SELECT id FROM users WHERE username = 'morozova_taty'), '2026-05-01', 511.00),
((SELECT id FROM users WHERE username = 'soloviev_max'), '2026-05-01', 45.20);
