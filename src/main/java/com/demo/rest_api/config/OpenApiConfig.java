package com.demo.rest_api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Server APIs (Java + MongoDB)",
        version = "0.0.2",
        description = """
            Hi. I am Lim Leong Kee.
            
            I have developed the entire set of server-side APIs using **Java**, with **MongoDB** as the underlying database. These APIs are fully documented and available for exploration and interaction via this page.
            
            You can register for an account, then log in to:
            
              - Play games:
                
                1. Guess Number
                
                2. Arrange Numbers
                
                3. Rock Paper Scissors (practice and challenge modes)
                
              - View your game profile or another user's game profile
                
              - Check the leaderboard
                
            > All API endpoints are interactive and can be tested directly from this page!
                
            I also customized this Swagger UI to include the following features:
            
            1. **Automatic JWT Handling:** Captures and injects the JWT token after a successful login to enable seamless access to protected endpoints.
            
            2. **Logout Behavior:** Clears the authorization token on logout to disable access to protected endpoints until you log in again.
            
            3. **Custom Operation Sorting:** Implements a custom sorting logic for API operations based on a predefined order, making the API navigation more intuitive.
        
            I would sincerely appreciate any feedback, suggestions, or improvements you might have. Thank you for taking the time to review my work.
            """,
        contact = @Contact(
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
