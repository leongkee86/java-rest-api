/*
 * ****************************************************************************
 * File: JwtFilter.java
 * Author: Lim Leong Kee
 * Email: leongkee86@gmail.com
 * Last Modified Date: 
 * 
 * Description:
 * This class intercepts incoming HTTP requests to perform JWT-based
 * authentication. It extracts the JWT token from the Authorization header,
 * validates it, and if valid, loads the corresponding user details. The
 * authenticated user is then stored in the Spring SecurityContext, enabling
 * access to secured endpoints. If validation fails, the response is returned
 * with a 401 Unauthorized status.
 * ****************************************************************************
 */

package com.demo.rest_api.security;

import com.demo.rest_api.utils.Constants;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.demo.rest_api.service.UserService;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter
{
    @Autowired
    private JwtAuthenticator jwtAuthenticator;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(
        @Nonnull HttpServletRequest request,
        @Nonnull HttpServletResponse response,
        @Nonnull FilterChain filterChain
    ) throws ServletException, IOException
    {
        String authHeader = request.getHeader( Constants.AUTH_HEADER );

        if (authHeader != null && authHeader.startsWith( Constants.TOKEN_PREFIX ))
        {
            String token = authHeader.substring( Constants.TOKEN_PREFIX.length() );

            try
            {
                // Validate the JWT token and return its subject (username).
                String username = jwtAuthenticator.validateToken( token );

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null)
                {
                    UserDetails userDetails = userService.loadUserByUsername( username );

                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );

                    // Set the authenticated user in the SecurityContext.
                    SecurityContextHolder.getContext().setAuthentication( authentication );
                }
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
