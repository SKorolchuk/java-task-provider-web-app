package com.study_project.provider_api.config;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
public class DatabaseConfig {

    private final DataSource dataSource;

    public DatabaseConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Создает потокобезопасный именованный шаблон JDBC,
     * защищающий приложение от атак типа SQL-Injection.
     */
    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * Принудительный ручной запуск миграций Flyway для чистого JDBC окружения.
     * Вызывается автоматически при старте контекста Spring Boot.
     */
    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();
    }
}
