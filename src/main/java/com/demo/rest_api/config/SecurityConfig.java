package com.demo.rest_api.config;

import com.demo.rest_api.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig
{
    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain( HttpSecurity http ) throws Exception
    {
        http.csrf( AbstractHttpConfigurer::disable )
            .authorizeHttpRequests( auth -> auth
                    .requestMatchers(
                        "/api/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                    )
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
    public BCryptPasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
}
