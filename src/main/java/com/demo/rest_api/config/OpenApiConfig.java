package com.demo.rest_api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Server APIs (Java + MongoDB)",
        version = "0.0.2",
        description = """
            ## Hi. I am Lim Leong Kee, the sole designer and developer of this project.
            
            - 🖥️ **Backend Tech Stack:** Java | Spring Boot | MongoDB | Docker
            
            - 🌐 **Frontend Tech Stack:** Customized Swagger UI | HTML | CSS | JavaScript
            
            I have developed the entire set of server-side APIs using **Java**, with **MongoDB** as the underlying database. These APIs are fully documented and available for exploration and interaction directly on this page.
            
            Once you have registered and logged in to your account, you will be able to:
            
              - 🎮 Play games: 1. Guess Number | 2. Arrange Numbers | 3. Rock Paper Scissors (Practice and Challenge modes)
              
              - 🎁 Claim your bonus points once they are available
              
              - 👤 View your own or any other user's game profiles
              
              - 🏆 Check the leaderboard
                
            > All API endpoints are interactive, always ready to use (no "Try it out" button needed), and can be tried out directly on this page!
            
            Additionally, I customized this Swagger UI by integrating my own **HTML**, **CSS**, and **JavaScript** files to provide the following features:
            
            1. 🔐 Automatically captures the JWT token upon successful login, stores it in local storage for seamless login on return visits, injects it into all requests targeting protected API endpoints, and updates the Swagger UI authorization dialog to reflect the current authentication state.
                
            2. 🚪 Extended Swagger UI's logout function to mirror the behavior of the logout API endpoint — clearing the stored JWT token, resetting the user's authentication state, and clearing the Swagger UI authorization dialog for consistent logout behavior.
                
            3. 👤 Displays login status — including username and score if logged in, or a guest message — fixed at the top-right corner of the page, always visible when scrolling.
                
            4. ✨ Automatically enables "Try it out" mode for all API operations to be always ready to use, and hides all “Try it out” buttons since they are no longer necessary, resulting in a cleaner interface.
                
            5. 🧩 Implements custom sorting logic for API operations to improve navigation and make the interface more user-friendly.
            
            I sincerely appreciate any feedback, suggestions, or improvements you may have. Thank you for taking the time to review my work.
            
            **My Cocos Creator Projects:** [Space Shooter Game](https://leongkee-space-shooter.onrender.com) | [Thai Hi-Lo Game](https://thaihilo-web.onrender.com) ( Login Details — Username: demo | Password: demo )
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
                ) )
                .servers( List.of(
                    new Server().url( "http://localhost:8080" ).description( "" )
//                    new Server().url( "https://leongkee-java-rest-api.onrender.com" ).description( "" )
                ) );
    }
}
