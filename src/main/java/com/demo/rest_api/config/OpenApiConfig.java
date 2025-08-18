package com.demo.rest_api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@OpenAPIDefinition(
    info = @Info(
                title = "Server APIs (Java + MongoDB)",
                version = "0.0.1",
                description = "Hi. I am Lim Leong Kee.<br>I have developed the entire set of server-side APIs using Java, with MongoDB as the underlying database. These APIs are fully documented and available for exploration and interaction via this page.<br>I would sincerely appreciate any feedback, suggestions or improvements you might have. Thank you for taking the time to review my work.",
                contact = @io.swagger.v3.oas.annotations.info.Contact(
                    name = "Lim Leong Kee",
                    email = "leongkee86@gmail.com"
                )
    )
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class OpenApiConfig
{
    @Bean
    public OpenAPI customOpenAPI()
    {
        return new OpenAPI()
                .tags( List.of(
                    new Tag().name( "Auth" ),
                    new Tag().name( "Game" )
                ) );
    }
}
