package com.demo.rest_api.controller;

import com.demo.rest_api.dto.UserResponse;
import com.demo.rest_api.model.User;
import com.demo.rest_api.repository.UserRepository;
import com.demo.rest_api.dto.ServerApiResponse;
import com.demo.rest_api.security.JwtUtil;
import com.demo.rest_api.service.UserService;
import com.demo.rest_api.utils.Constants;
import com.demo.rest_api.utils.StringHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class AuthApiBaseController
{
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Target( ElementType.METHOD )
    @Retention( RetentionPolicy.RUNTIME )
    @Operation(
        operationId = "1_1",
        summary = "Registers for a new user account.",
        description = """
            Registers for a new user account.
            
            ### Request body must include:
            - `username` (unique)
            - `password`
            
            > Once registered successfully, you can log in using the `/api/auth/login` endpoint to receive a JWT token.
            """
    )
    @ApiResponses( value =
        {
            @ApiResponse( responseCode = "200", description = "OK", content = @Content( mediaType = "" ) )
        }
    )
    public @interface RegisterOperation {}

    protected ResponseEntity<?> register( String username, String password )
    {
        if (StringHelper.isNullOrEmpty( username ))
        {
            return ServerApiResponse.generateResponseEntity(
                    HttpStatus.BAD_REQUEST,
                    "Please enter a username."
                    );
        }

        if (StringHelper.isNullOrEmpty( password ))
        {
            return ServerApiResponse.generateResponseEntity(
                    HttpStatus.BAD_REQUEST,
                    "Please enter a password."
                    );
        }

        if (userService.findByUsername( username ).isPresent())
        {
            return ServerApiResponse.generateResponseEntity(
                    HttpStatus.CONFLICT,
                    "The username '" + username + "' is already taken. Please choose a different username."
                    );
        }

        User user = new User( username, password );
        userService.save( user );

        return ServerApiResponse.generateResponseEntity(
                HttpStatus.CREATED,
                "Registration successful. Please use the 'api/auth/login' endpoint to log in to your account."
                );
    }

    @Target( ElementType.METHOD )
    @Retention( RetentionPolicy.RUNTIME )
    @Operation(
        operationId = "1_2",
        summary = "Log in to an existing user account.",
        description = """
            Authenticates a user with username and password.
            
            Upon successful authentication, a JWT access token is returned in the `token` field.
            
            <s>**How to use the token in Swagger UI:**</s>
            
            <s>1. Copy the `token` from the response.</s>
            
            <s>2. Click the **Authorize** button (top right in Swagger UI).</s>
            
            <s>3. Paste the token into the `value` input field.</s>
            
            <s>4. Click **Authorize** — now your requests will include the token.</s>
            
            **Important:** I have implemented automation that automatically sets the JWT access token in Swagger UI’s Authorization header. As a result, users no longer need to manually click the 'Authorize' button and paste the token.
            """
    )
    @ApiResponses( value =
        {
            @ApiResponse( responseCode = "200", description = "Successful login", content = @Content( mediaType = "" ) )
        }
    )
    public @interface LoginOperation {}

    protected ResponseEntity<?> login( String username, String password )
    {
        Optional<User> userOpt = userService.findByUsername( username );

        if (userOpt.isEmpty())
        {
            return ServerApiResponse.generateResponseEntity(
                    HttpStatus.NOT_FOUND,
                    "Username not found. Please check and try again."
                    );
        }

        User user = userOpt.get();

        if (!userService.validatePassword( password, user.getPassword() ))
        {
            return ServerApiResponse.generateResponseEntity(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid credentials. Please check and try again."
                    );
        }

        // Generate a JWT token using the user's username as the subject.
        String token = jwtUtil.generateToken( user.getUsername() );

        Map<String, Object> data = new LinkedHashMap<>();
        data.put( "token", token );
        data.put( "user", new UserResponse( user ) );

        return ServerApiResponse.generateResponseEntity(
                HttpStatus.OK,
                "Login successful! You have been authorized and can access the protected API endpoints now.",
                data
                );
    }

    @Target( ElementType.METHOD )
    @Retention( RetentionPolicy.RUNTIME )
    @SecurityRequirement( name = "bearerAuth" )
    @Operation(
        operationId = "1_3",
        summary = "Log out of your current user account.",
        description = "Log out from the user account that you are currently logged in to."
    )
    @ApiResponses( value =
        {
            @ApiResponse( responseCode = "200", description = "Successful logout", content = @Content( mediaType = "" ) ),
            @ApiResponse( responseCode = "401", description = "Unauthorized — invalid or missing token", content = @Content( mediaType = "" ) ),
        }
    )
    public @interface LogoutOperation {}

    protected ResponseEntity<?> logout( HttpServletRequest request )
    {
        String authHeader = request.getHeader( Constants.AUTH_HEADER );

        if (authHeader == null)
        {
            return ServerApiResponse.generateResponseEntity(
                    HttpStatus.UNAUTHORIZED,
                    "You are not logged in."
                    );
        }

        return ServerApiResponse.generateResponseEntity(
                HttpStatus.OK,
                "You have been successfully logged out. You are no longer authorized to access protected API endpoints. Please log in again to continue."
                );
    }
}
