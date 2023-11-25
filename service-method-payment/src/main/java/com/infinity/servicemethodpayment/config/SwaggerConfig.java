package com.infinity.servicemethodpayment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;

public class SwaggerConfig {
    @Bean
    public OpenAPI usersMicroserviceOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("SERVICE METHOD PAYMENT")
                        .description(
                                "Service charger de gérer tous les payments de l'utilisateur"
                        )
                        .version("1.0"));
    }
}
