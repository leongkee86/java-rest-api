package com.demo.rest_api.controller;

import com.demo.rest_api.enums.SortDirection;
import com.demo.rest_api.utils.Constants;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( "/api/user" + Constants.API_PATH_SUFFIX_FOR_FORM_URLENCODED )
@Tag( name = Constants.USER_API_FORM)
@Validated
public class UserApiFormController extends UserApiBaseController
{
    @GetMapping(
        value = "/profile",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @GetOwnProfileOperation
    public ResponseEntity<?> getProfile()
    {
        return super.processGettingProfile();
    }

    @GetMapping(
        value = "/profile/{username}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @GetUserProfileOperation
    public ResponseEntity<?> getProfileByUsername( @PathVariable( "username" ) String username )
    {
        return super.processGettingProfile( username );
    }

    @PutMapping(
        value = "/changeDisplayName",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
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

    @PutMapping(
        value = "/changePassword",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
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

    @DeleteMapping(
        value = "/deleteAccount",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @DeleteAccountOperation
    public ResponseEntity<?> deleteAccount()
    {
        return super.processDeletingAccount();
    }

    @GetMapping(
        value = "/filterAndSort",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @FilterAndSortOperation
    public ResponseEntity<?> filterAndSort(
        @RequestParam( required = false ) Integer minimumScore,
        @RequestParam( required = false ) Integer maximumScore,
        @RequestParam( required = false ) String usernameKeyword,
        @Parameter( required = true ) @RequestParam( defaultValue = "Ascending" ) SortDirection sortDirection,
        @RequestParam( required = false ) Integer page,
        @RequestParam( required = false ) Integer limit )
    {
        return super.processFilteringAndSorting( minimumScore, maximumScore, usernameKeyword, sortDirection, page, limit );
    }
}
