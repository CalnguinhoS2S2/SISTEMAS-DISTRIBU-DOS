package com.sisdistro.tweetapp.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configura o documento OpenAPI/Swagger da aplicação.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI tweetAppOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API TweetApp")
                        .description("API para cadastro de usuários e publicação de tweets.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Equipe Sistemas Distribuídos")
                                .email("contato@example.com"))
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentação do trabalho NT2")
                        .url("https://spring.io/projects/spring-boot"));
    }
}
