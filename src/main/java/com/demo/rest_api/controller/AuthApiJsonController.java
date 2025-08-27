/*
 * ****************************************************************************
 * File: AuthApiJsonController.java
 * Author: Lim Leong Kee
 * Email: leongkee86@gmail.com
 * Last Modified Date: 27 August 2025
 * 
 * Description:
 * This class provides REST API endpoints that use query and JSON parameters in
 * API requests for authentication-related API operations. It extends the
 * AuthApiBaseController class to reuse core logic for API operations. It
 * integrates with Swagger UI using annotations to automatically generate API
 * documentation.
 * ****************************************************************************
 */

package com.demo.rest_api.controller;

import com.demo.rest_api.dto.LoginRequest;
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
@Tag( name = Constants.AUTH_API_JSON)
@Validated
public class AuthApiJsonController extends AuthApiBaseController
{
    @PostMapping(
        value = "/register",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @RegisterOperation
    public ResponseEntity<?> register( @RequestBody RegisterRequest request )
    {
        return super.processRegistration( request.getUsername(), request.getPassword(), request.getDisplayName() );
    }

    @PostMapping(
        value = "/login",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @LoginOperation
    public ResponseEntity<?> login( @RequestBody LoginRequest request )
    {
        return super.processLogin( request.getUsername(), request.getPassword() );
    }

    @PostMapping(
        value = "/logout",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @LogoutOperation
    protected ResponseEntity<?> logout( HttpServletRequest request )
    {
        return super.processLogout( request );
    }
}
