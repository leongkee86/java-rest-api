package com.demo.rest_api.controller;

import com.demo.rest_api.dto.RegisterLoginRequest;
import com.demo.rest_api.utils.Constants;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( "/api/auth" )
@Tag( name = Constants.AUTH_API )
@Validated
public class AuthApiController extends AuthApiBaseController
{
    @PostMapping( value = "/register", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE } )
    @RegisterOperation
    public ResponseEntity<?> register( @RequestBody RegisterLoginRequest request )
    {
        return super.register( request.getUsername(), request.getPassword() );
    }

    @PostMapping( value = "/login", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE } )
    @LoginOperation
    public ResponseEntity<?> login( @RequestBody RegisterLoginRequest request )
    {
        return super.login( request.getUsername(), request.getPassword() );
    }

    @PostMapping( "/logout" )
    @LogoutOperation
    protected ResponseEntity<?> logout( HttpServletRequest request )
    {
        return super.logout( request );
    }
}
