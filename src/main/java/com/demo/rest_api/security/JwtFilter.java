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
    private JwtUtil jwtUtil;

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
                String username = jwtUtil.validateToken( token );

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null)
                {
                    UserDetails userDetails = userService.loadUserByUsername( username );

                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );

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
