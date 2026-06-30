package br.com.postechfiap.toloni.restauranthub.infrastructure.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RestaurantHub API")
                        .description("API for restaurant and menu management")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Tiago Toloni")
                                .email("tiago.toloni@gmail.com")));
    }
}
