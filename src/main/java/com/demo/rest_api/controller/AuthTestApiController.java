package com.demo.rest_api.controller;

import com.demo.rest_api.utils.Constants;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
            @RequestParam( required = true ) String username,
            @RequestParam( required = true ) String password
    )
    {
        return super.register( username, password );
    }

    @PostMapping( value = "/login" )
    @LoginOperation
    public ResponseEntity<?> login(
            @RequestParam String username,
            @RequestParam String password
    )
    {
        return super.login( username, password );
    }

    @PostMapping( "/logout" )
    @LogoutOperation
    protected ResponseEntity<?> logout( HttpServletRequest request )
    {
        return super.logout( request );
    }
}
