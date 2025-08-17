package com.demo.rest_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.demo.rest_api.service.UserService;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter
{
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal( HttpServletRequest request, HttpServletResponse response, FilterChain filterChain )
            throws ServletException, IOException
    {
        String authHeader = request.getHeader( "Authorization" );

        if (authHeader != null && authHeader.startsWith( "Bearer " ))
        {
            String token = authHeader.substring( 7 );

            try
            {
                String username = jwtUtil.validateToken( token );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken( username, null, null );

                SecurityContextHolder.getContext().setAuthentication( authentication );

            }
            catch ( Exception e )
            {
                response.setStatus( HttpServletResponse.SC_UNAUTHORIZED );
                return;
            }
        }

        filterChain.doFilter( request, response );
    }
}
