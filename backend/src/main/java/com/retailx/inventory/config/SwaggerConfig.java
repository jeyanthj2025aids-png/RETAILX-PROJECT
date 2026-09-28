package com.retailx.inventory.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Retail Store Inventory Reorder Alert System API")
                        .version("1.0.0")
                        .description("REST API specifications for dynamic stock calculation, single-OPEN alert prevention, sales reports, and inventory management.")
                        .contact(new Contact()
                                .name("Retail Store Systems Engineering")
                                .email("inventory@retailx.internal"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")));
    }
}
