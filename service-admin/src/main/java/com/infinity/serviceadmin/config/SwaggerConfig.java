package com.infinity.serviceadmin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;

public class SwaggerConfig {
    @Bean
    public OpenAPI usersMicroserviceOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("SERVICE ADMIN")
                        .description(
                                "Service charger de gérer les administrateur de DEME"
                        )
                        .version("1.0"));
    }
}
