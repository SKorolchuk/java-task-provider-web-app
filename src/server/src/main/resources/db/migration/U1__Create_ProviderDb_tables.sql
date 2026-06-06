-- 1. Принудительно указываем СУБД, в какой базе работать
USE dev_database;

-- 2. Отключаем проверки
SET FOREIGN_KEY_CHECKS = 0;

-- 3. Удаляем таблицы
DROP TABLE IF EXISTS flyway_schema_history; -- Стираем также историю Flyway
DROP TABLE IF EXISTS traffic_stats;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS tariff_promotions;
DROP TABLE IF EXISTS promotions;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS tariffs;

-- 4. Возвращаем проверки
SET FOREIGN_KEY_CHECKS = 1;
