package edu.pg.qa.bookstore;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bookstoreOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Book Store API")
                        .description("API do ćwiczeń z testowania REST w Javie (studia podyplomowe PG)")
                        .version("v1"));
    }
}