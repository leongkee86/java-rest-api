/*
 * ****************************************************************************
 * File: JwtUtil.java
 * Author: Lim Leong Kee
 * Email: leongkee86@gmail.com
 * Last Modified Date: 24 August 2025
 * 
 * Description:
 * This class provides functionality for working with JSON Web Tokens (JWTs).
 * It includes methods for generating and validating JWTs.
 * ****************************************************************************
 */

package com.demo.rest_api.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtAuthenticator
{
    @Autowired
    private SecretKey jwtSecretKey;

    // Token valid for 1 day.
    private static final long EXPIRATION_TIME_MS = 24 * 60 * 60 * 1000;

    // Generates a JWT token for the given subject.
    public String generateToken( String subject )
    {
        return Jwts.builder()
                .setSubject( subject )
                .setIssuedAt( new Date() )
                .setExpiration( new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS ) )
                .signWith( jwtSecretKey, SignatureAlgorithm.HS256 )
                .compact();
    }

    // Validates a JWT token and returns the subject if valid.
    // Returns null if the token is invalid or expired.
    public String validateToken( String token )
    {
        try
        {
            return Jwts.parserBuilder()
                    .setSigningKey(jwtSecretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        }
        catch ( JwtException e )
        {
            // Invalid token (expired, malformed, tampered, etc.)
            return null;
        }
    }
}
