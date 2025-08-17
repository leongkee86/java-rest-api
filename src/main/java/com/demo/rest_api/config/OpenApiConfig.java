package com.demo.rest_api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
                title = "Server APIs (Java + MongoDB)",
                version = "0.0.1",
                description = "Hi. I am Lim Leong Kee. I have developed these server APIs using Java, with MongoDB as the database. Feel free to explore and interact with them via this Swagger UI. I would greatly appreciate any feedback or suggestions you may have. Thank you.",
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
}
