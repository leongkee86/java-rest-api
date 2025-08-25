package com.demo.rest_api.controller;

import com.demo.rest_api.dto.RegisterRequest;
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
    public ResponseEntity<?> register( @RequestBody RegisterRequest request )
    {
        return super.processRegistration( request.getUsername(), request.getPassword(), request.getDisplayName() );
    }

    @PostMapping( value = "/login", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE } )
    @LoginOperation
    public ResponseEntity<?> login( @RequestBody RegisterRequest request )
    {
        return super.processLogin( request.getUsername(), request.getPassword() );
    }

    @PostMapping( "/logout" )
    @LogoutOperation
    protected ResponseEntity<?> logout( HttpServletRequest request )
    {
        return super.processLogout( request );
    }
}
