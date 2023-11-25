package com.infinity.serviceauth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;

public class SwaggerConfig {
    @Bean
    public OpenAPI usersMicroserviceOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("SERVICE AUTH")
                        .description(
                                "Service charger de créer des comptes utilisateur (user simple, organisation, admin simple, admin root)"
                        )
                        .version("1.0"));
    }
}
