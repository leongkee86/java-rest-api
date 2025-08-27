/*
 * ****************************************************************************
 * File: AuthApiFormController.java
 * Author: Lim Leong Kee
 * Email: leongkee86@gmail.com
 * Last Modified Date: 27 August 2025
 *
 * Description:
 * This class provides REST API endpoints that use query and form parameters in
 * API requests for authentication-related API operations. It extends the
 * AuthApiBaseController class to reuse core logic for API operations. It
 * integrates with Swagger UI using annotations to automatically generate API
 * documentation.
 * ****************************************************************************
 */

package com.demo.rest_api.controller;

import com.demo.rest_api.utils.Constants;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/auth" + Constants.API_PATH_SUFFIX_FOR_FORM_URLENCODED )
@Tag( name = Constants.AUTH_API_FORM)
@Validated
public class AuthApiFormController extends AuthApiBaseController
{
    @PostMapping(
        value = "/register",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
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

    @PostMapping(
        value = "/login",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @LoginOperation
    public ResponseEntity<?> login(
        @RequestParam String username,
        @RequestParam String password
    )
    {
        return super.processLogin( username, password );
    }

    @PostMapping(
        value = "/logout",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @LogoutOperation
    protected ResponseEntity<?> logout( HttpServletRequest request )
    {
        return super.processLogout( request );
    }
}
