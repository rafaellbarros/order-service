package br.com.rafaellbarros.order.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI swaggerAPI() {

        final var openAPI = new OpenAPI();

        openAPI.info(new Info().title("Order Service API")
                .description("API for Order Service")
                .version("1.0"));


        return openAPI;
    }
}
