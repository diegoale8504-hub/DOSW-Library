// src/main/java/edu/eci/dosw/tdd/config/OpenApiConfig.java

package edu.eci.dosw.tdd.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DOSW Library API")
                        .version("1.0.0")
                        .description("API de gestión de biblioteca — DOSW ECI")
                );
    }
}