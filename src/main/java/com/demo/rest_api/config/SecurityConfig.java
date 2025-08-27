/*
 * ****************************************************************************
 * File: SecurityConfig.java
 * Author: Lim Leong Kee
 * Email: leongkee86@gmail.com
 * Last Modified Date: 24 August 2025
 * 
 * Description:
 * This class configures application security with Spring Security by disabling
 * CSRF, enabling CORS with configurable allowed origins, and enforcing
 * stateless JWT-based authentication. Configurable public URLs are open to
 * everyone, while other requests require JWT authentication when login is
 * needed. The JwtFilter is added to validate tokens before requests are
 * processed.
 * ****************************************************************************
 */

package com.demo.rest_api.config;

import com.demo.rest_api.security.JwtFilter;
import com.demo.rest_api.utils.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig
{
    @Value( "${PUBLIC_URLS}" )
    private String publicUrls;

    @Value( "${ALLOWED_ORIGINS}" )
    private String allowedOrigins;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain( HttpSecurity http ) throws Exception
    {
        http.csrf( AbstractHttpConfigurer::disable )
            .cors( cors -> {} ) // Enable CORS with an empty customizer.
            .authorizeHttpRequests(
                auth -> auth.requestMatchers( StringHelper.splitStringToArray( publicUrls, "|" ) )
                .permitAll()
                .anyRequest()
                .authenticated()
            )
            .sessionManagement(
                session -> session.sessionCreationPolicy( SessionCreationPolicy.STATELESS )
            )
            .addFilterBefore( jwtFilter, UsernamePasswordAuthenticationFilter.class );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource()
    {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins( StringHelper.splitStringToList( allowedOrigins, "|" ) );
        config.setAllowedMethods( List.of( "GET", "POST", "PUT", "DELETE", "OPTIONS") );
        config.setAllowedHeaders( List.of( "*") );
        config.setAllowCredentials( true );

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration( "/**", config );

        return source;
    }
}
