package com.demo.rest_api.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig
{
    @Value( "${JWT_SECRET_KEY}" )
    private String jwtSecretKeyString;

    @Bean
    public SecretKey jwtSecretKey()
    {
        // Create SecretKey from jwt secret key string (must be at least 32 characters long for HS256).
        return Keys.hmacShaKeyFor( jwtSecretKeyString.getBytes( StandardCharsets.UTF_8 ) );
    }
}
