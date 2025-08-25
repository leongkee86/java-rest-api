package com.demo.rest_api.controller;

import com.demo.rest_api.utils.Constants;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/user" )
@Tag( name = Constants.USER_API )
@Validated
public class UserApiController extends UserApiBaseController
{
    @GetMapping( "/profile" )
    @UserApiBaseController.GetProfileOperation
    public ResponseEntity<?> getProfile( @RequestParam( required = false ) String username )
    {
        return super.getProfile( username );
    }
}
