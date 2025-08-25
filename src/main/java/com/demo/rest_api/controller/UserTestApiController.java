package com.demo.rest_api.controller;

import com.demo.rest_api.utils.Constants;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( "/api/test/user" )
@Tag( name = Constants.USER_API_TEST )
@Validated
public class UserTestApiController extends UserApiBaseController
{
    @GetMapping( "/profile" )
    @GetProfileOperation
    public ResponseEntity<?> getProfile( @RequestParam( required = false ) String username )
    {
        return super.processGettingProfile( username );
    }

    @PutMapping( "/changeDisplayName" )
    @ChangeDisplayNameOperation
    public ResponseEntity<?> changeDisplayName(
        @RequestParam @NotBlank @Size(
            min = Constants.DISPLAY_NAME_LENGTH,
            message = "Display name must be at least " + Constants.DISPLAY_NAME_LENGTH + " characters long."
        )
        String displayName
    )
    {
        return super.processChangingDisplayName( displayName );
    }

    @PutMapping( "/changePassword" )
    @ChangePasswordOperation
    public ResponseEntity<?> changePassword(
        @RequestParam @NotBlank @Size(
            min = Constants.PASSWORD_LENGTH,
            message = "Password must be at least " + Constants.PASSWORD_LENGTH + " characters long."
        )
        String password
    )
    {
        return super.processChangingPassword( password );
    }

    @DeleteMapping( "/deleteAccount" )
    @DeleteAccountOperation
    public ResponseEntity<?> deleteAccount()
    {
        return super.processDeletingAccount();
    }
}
