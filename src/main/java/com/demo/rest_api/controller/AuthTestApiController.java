package com.demo.rest_api.controller;

import com.demo.rest_api.utils.Constants;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/test/auth" )
@Tag( name = Constants.AUTH_API_TEST )
@Validated
public class AuthTestApiController extends AuthApiBaseController
{
    @PostMapping( value = "/register" )
    @RegisterOperation
    public ResponseEntity<?> register(
        @RequestParam @NotBlank @Size(
            min = Constants.USERNAME_LENGTH,
            message = "Username must be at least " + Constants.USERNAME_LENGTH + " characters long."
        )
        String username,
        @RequestParam @NotBlank @Size(
            min = Constants.PASSWORD_LENGTH,
            message = "Password must be at least " + Constants.PASSWORD_LENGTH + " characters long."
        )
        String password,
        @RequestParam(
            required = false
        )
        String displayName
    )
    {
        return super.processRegistration( username, password, displayName );
    }

    @PostMapping( value = "/login" )
    @LoginOperation
    public ResponseEntity<?> login(
        @RequestParam String username,
        @RequestParam String password
    )
    {
        return super.processLogin( username, password );
    }

    @PostMapping( "/logout" )
    @LogoutOperation
    protected ResponseEntity<?> logout( HttpServletRequest request )
    {
        return super.processLogout( request );
    }
}
