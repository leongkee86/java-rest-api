package com.demo.rest_api.controller;

import com.demo.rest_api.dto.UserResponse;
import com.demo.rest_api.model.User;
import com.demo.rest_api.repository.UserRepository;
import com.demo.rest_api.dto.ServerApiResponse;
import com.demo.rest_api.security.JwtUtil;
import com.demo.rest_api.service.UserService;
import com.demo.rest_api.utils.StringHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping( "/api/auth" )
@Tag( name = "Auth" )
@Validated
public class AuthController
{
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping( "/register" )
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
    public ResponseEntity<?> register(
        @RequestParam String username,
        @RequestParam String password
    )
    {
        if (StringHelper.isNullOrEmpty( username ))
        {
            return ResponseEntity
                    .status( HttpStatus.BAD_REQUEST )
                    .body(
                        new ServerApiResponse<>(
                            HttpStatus.BAD_REQUEST.value(),
                            "Please enter a username.",
                            null,
                            null
                        )
                    );
        }

        if (StringHelper.isNullOrEmpty( password ))
        {
            return ResponseEntity
                    .status( HttpStatus.BAD_REQUEST )
                    .body(
                        new ServerApiResponse<>(
                            HttpStatus.BAD_REQUEST.value(),
                            "Please enter a password.",
                            null,
                            null
                        )
                    );
        }

        if (userService.findByUsername( username ).isPresent())
        {
            return ResponseEntity
                    .status( HttpStatus.CONFLICT )
                    .body(
                        new ServerApiResponse<>(
                            HttpStatus.CONFLICT.value(),
                            "The username '" + username + "' is already taken. Please choose a different username.",
                            null,
                            null
                        )
                    );
        }

        User user = new User( username, password );
        userService.save( user );

        return ResponseEntity
                .status( HttpStatus.CREATED )
                .body(
                    new ServerApiResponse<>(
                        HttpStatus.CREATED.value(),
                        "Registration successful. Please use the 'api/auth/login' endpoint to log in to your account.",
                        null,
                        null
                    )
                );
    }

    @PostMapping( "/login" )
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
    public ResponseEntity<?> login(
        @RequestParam String username,
        @RequestParam String password
    )
    {
        Optional<User> userOpt = userService.findByUsername( username );

        if (userOpt.isEmpty())
        {
            return ResponseEntity
                    .status( HttpStatus.NOT_FOUND )
                    .body(
                        new ServerApiResponse<>(
                            HttpStatus.NOT_FOUND.value(),
                            "Username not found. Please check and try again.",
                            null,
                            null
                        )
                    );
        }

        User user = userOpt.get();

        if (!userService.validatePassword( password, user.getPassword() ))
        {
            return ResponseEntity
                    .status( HttpStatus.UNAUTHORIZED )
                    .body(
                        new ServerApiResponse<>(
                            HttpStatus.UNAUTHORIZED.value(),
                            "Invalid credentials. Please check and try again.",
                            null,
                            null
                        )
                    );
        }

        String token = jwtUtil.generateToken( user.getUsername() );

        Map<String, Object> data = new LinkedHashMap<>();
        data.put( "token", token );
        data.put( "user", new UserResponse( user ) );

        return ResponseEntity
                .status( HttpStatus.OK )
                .body(
                    new ServerApiResponse<>(
                        HttpStatus.OK.value(),
                        "Login successful! You have been authorized and can access the protected API endpoints now.",
                        data,
                        null
                    )
                );
    }

    @PostMapping( "/logout" )
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
    public ResponseEntity<?> logout( HttpServletRequest request )
    {
        String authHeader = request.getHeader( "Authorization" );

        if (authHeader == null)
        {
            return ResponseEntity
                    .status( HttpStatus.UNAUTHORIZED )
                    .body(
                        new ServerApiResponse<>(
                            HttpStatus.UNAUTHORIZED.value(),
                            "You are not logged in.",
                            null,
                            null
                        )
                    );
        }

        return ResponseEntity
                .status( HttpStatus.OK )
                .body(
                    new ServerApiResponse<>(
                        HttpStatus.OK.value(),
                        "You have been successfully logged out. You are no longer authorized to access protected API endpoints. Please log in again to continue.",
                        null,
                        null
                    )
                );
    }
}
