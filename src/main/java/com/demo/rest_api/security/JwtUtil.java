package com.demo.rest_api.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil
{
    @Value( "${JWT_SECRET_KEY}" )
    private String secretKey;

    public String generateToken( String subject )
    {
        return Jwts.builder()
                .setSubject( subject )
                .setIssuedAt( new Date() )
                .setExpiration( new Date(System.currentTimeMillis() + 86400000 ) ) // 1 day
                .signWith( SignatureAlgorithm.HS256, secretKey )
                .compact();
    }

    public String validateToken( String token )
    {
        return Jwts.parser().setSigningKey( secretKey )
                .parseClaimsJws( token )
                .getBody().getSubject();
    }
}
