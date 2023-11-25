package com.infinity.serviceactivity.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;

public class SwaggerConfig {
    @Bean
    public OpenAPI usersMicroserviceOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("SERVICE USER")
                        .description(
                                "Service charger de gérer tous les demandes de l'utilisateur (user simple)"
                        )
                        .version("1.0"));
    }
}
