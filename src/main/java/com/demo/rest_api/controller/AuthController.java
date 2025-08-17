package com.demo.rest_api.controller;

import com.demo.rest_api.dto.LoginRequest;
import com.demo.rest_api.dto.RegisterRequest;
import com.demo.rest_api.dto.UserResponse;
import com.demo.rest_api.model.User;
import com.demo.rest_api.repository.UserRepository;
import com.demo.rest_api.dto.ApiResponse;
import com.demo.rest_api.security.JwtUtil;
import com.demo.rest_api.service.UserService;
import com.demo.rest_api.utils.Constants;
import com.demo.rest_api.utils.StringHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@Tag( name = "Auth" )
@RequestMapping( "/api/auth" )
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
        summary = "Registers a new user account.",
        description = """
            Registers a new user account.
            
            ### Request body must include:
            - `username` (unique)
            - `password`
            
            > Once registered successfully, you can log in using the `/api/auth/login` endpoint to receive a JWT token.
            """
    )
    public ResponseEntity<?> register( @RequestBody RegisterRequest request )
    {
        if (StringHelper.isNullOrEmpty( request.getUsername() ))
        {
            return ResponseEntity
                    .status( HttpStatus.BAD_REQUEST )
                    .body(
                        new ApiResponse<>(
                            HttpStatus.BAD_REQUEST.value(),
                            "username is required",
                            null,
                            null
                        )
                    );
        }

        if (StringHelper.isNullOrEmpty( request.getPassword() ))
        {
            return ResponseEntity
                    .status( HttpStatus.BAD_REQUEST )
                    .body(
                        new ApiResponse<>(
                            HttpStatus.BAD_REQUEST.value(),
                            "password is required",
                            null,
                            null
                        )
                    );
        }

        if (userRepository.findByUsername( request.getUsername() ).isPresent())
        {
            return ResponseEntity
                    .status( HttpStatus.CONFLICT )
                    .body(
                        new ApiResponse<>(
                                HttpStatus.CONFLICT.value(),
                                "username already registered",
                                null,
                                null
                        )
                    );
        }

        User user = new User( request.getUsername(), request.getPassword() );
        userService.save( user );

        return ResponseEntity
                .status( HttpStatus.CREATED )
                .body(
                    new ApiResponse<>(
                        HttpStatus.CREATED.value(),
                        Constants.DEFAULT_SUCCESS_MESSAGE,
                        null,
                        null
                    )
                );
    }

    @PostMapping( "/login" )
    @Operation(
        summary = "Log in to an existing user account.",
        description = """
            Authenticates a user with username and password.
            
            On success, returns a JWT access token in the `token` field.
            
            ### How to use the token in Swagger UI:
            1. Copy the `token` from the response.
            2. Click the **Authorize** button (top right in Swagger UI).
            3. Paste the token into the `value` input field.
            4. Click **Authorize** — now your requests will include the token.
            """
    )
    public ResponseEntity<?> login( @RequestBody LoginRequest request )
    {
        Optional<User> userOpt = userService.findByUsername( request.getUsername() );

        if (userOpt.isEmpty())
        {
            return ResponseEntity
                    .status( HttpStatus.NOT_FOUND )
                    .body(
                        new ApiResponse<>(
                            HttpStatus.NOT_FOUND.value(),
                            "username not found",
                            null,
                            null
                        )
                    );
        }

        User user = userOpt.get();

        if (!userService.validatePassword( request.getPassword(), user.getPassword() ))
        {
            return ResponseEntity
                    .status( HttpStatus.UNAUTHORIZED )
                    .body(
                        new ApiResponse<>(
                            HttpStatus.UNAUTHORIZED.value(),
                            "invalid credentials",
                            null,
                            null
                        )
                    );
        }

        String token = jwtUtil.generateToken( user.getUsername() );

        Map<String, Object> data = new LinkedHashMap<>();
        data.put( "token", token );
        data.put( "user", new UserResponse( user.getUsername(), user.getScore() ) );

        return ResponseEntity
                .status( HttpStatus.OK )
                .body(
                        new ApiResponse<>(
                            HttpStatus.OK.value(),
                            Constants.DEFAULT_SUCCESS_MESSAGE,
                            data,
                            null
                        )
                );
    }
}
