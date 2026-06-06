-- 1. Таблица тарифов
CREATE TABLE tariffs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    speed_mbps INT NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Таблица пользователей (Клиенты и Админы)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL, -- 'CLIENT', 'ADMIN'
    balance DECIMAL(10, 2) DEFAULT 0.00,
    is_blocked BOOLEAN DEFAULT FALSE,
    tariff_id BIGINT,
    FOREIGN KEY (tariff_id) REFERENCES tariffs(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Таблица акций и скидок
CREATE TABLE promotions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    discount_percentage INT DEFAULT 0,
    end_date DATE NOT NULL,
    description TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. СВЯЗУЮЩАЯ ТАБЛИЦА: Какие акции применены к каким тарифам (Многие-ко-многим)
-- Это позволит админу «объявлять акции и скидки» на конкретные тарифные планы
CREATE TABLE tariff_promotions (
    tariff_id BIGINT NOT NULL,
    promotion_id BIGINT NOT NULL,
    PRIMARY KEY (tariff_id, promotion_id),
    FOREIGN KEY (tariff_id) REFERENCES tariffs(id) ON DELETE CASCADE,
    FOREIGN KEY (promotion_id) REFERENCES promotions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Таблица истории платежей (Пополнение счета)
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. ТАБЛИЦА СТАТИСТИКИ ТРАФИКА: Учет трафика клиента по месяцам
-- Закрывает требование «Клиент может просмотреть состояние своего трафика»
CREATE TABLE traffic_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    billing_period DATE NOT NULL, -- Например, '2026-05-01' (первый день месяца учета)
    traffic_consumed_gb DECIMAL(10, 2) DEFAULT 0.00,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_period (user_id, billing_period) -- Защита от дублей за один месяц
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
