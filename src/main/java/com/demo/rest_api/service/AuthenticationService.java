package com.demo.rest_api.service;

import com.demo.rest_api.dto.ServerApiResponse;
import com.demo.rest_api.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService
{
    @Autowired
    private UserService userService;

    public ResponseEntity<?> getAuthenticatedUserOrError()
    {
        // At this point, the JwtFilter class should have already set the authenticated user
        // in the SecurityContext via the doFilterInternal() method.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the authentication object is null or an anonymous user (no actual authentication).
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
        {
            return ServerApiResponse.generateResponseEntity(
                    HttpStatus.UNAUTHORIZED,
                    "You are not logged in, or your session token is invalid or has expired. Please use the 'api/auth/login' endpoint to log in again."
            );
        }

        String username = authentication.getName();  // From JWT token subject.
        Optional<User> userOpt = userService.findByUsername( username );

        if (userOpt.isEmpty())
        {
            return ServerApiResponse.generateResponseEntity(
                    HttpStatus.NOT_FOUND,
                    "The user associated with the current session token was not found. Please use the 'api/auth/login' endpoint to log in again."
            );
        }

        return ResponseEntity.ok( userOpt.get() );
    }
}
