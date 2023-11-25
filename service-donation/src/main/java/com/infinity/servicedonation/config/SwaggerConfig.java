package com.infinity.servicedonation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;

public class SwaggerConfig {
    @Bean
    public OpenAPI usersMicroserviceOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("SERVICE TEMOIGNAGE")
                        .description(
                                "Service charger de gérer tous les temoignages des utilisateurs (user simple et  organisation)"
                        )
                        .version("1.0"));
    }
}
