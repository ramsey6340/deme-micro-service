package com.infinity.servicecause.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;

public class SwaggerConfig {
    @Bean
    public OpenAPI usersMicroserviceOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("SERVICE CAUSE")
                        .description(
                                "Service charger de gérer les causes humanitaire"
                        )
                        .version("1.0"));
    }
}
