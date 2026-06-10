package com.study_project.provider_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация спецификации OpenAPI (Swagger) для автоматической генерации документации.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Интернет-Провайдера (Provider API)")
                        .version("1.2")
                        .description("Интерактивная спецификация REST API для управления клиентами, биллингом, интернет-тарифами и акциями.")
                        .contact(new Contact()
                                .name("John D.")
                                .email("student@localhost")));
    }
}
