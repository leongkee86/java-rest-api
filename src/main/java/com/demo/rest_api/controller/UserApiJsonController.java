/*
 * ****************************************************************************
 * File: UserApiJsonController.java
 * Author: Lim Leong Kee
 * Email: leongkee86@gmail.com
 * Last Modified Date: 27 August 2025
 * 
 * Description:
 * This class provides REST API endpoints that use query and JSON parameters in
 * API requests to manage user account settings. It extends the
 * UserApiBaseController class to reuse core logic for API operations. It
 * integrates with Swagger UI using annotations to automatically generate API
 * documentation.
 * ****************************************************************************
 */

package com.demo.rest_api.controller;

import com.demo.rest_api.dto.ChangeDisplayNameRequest;
import com.demo.rest_api.dto.ChangePasswordRequest;
import com.demo.rest_api.enums.SortDirection;
import com.demo.rest_api.utils.Constants;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( "/api/user" )
@Tag( name = Constants.USER_API_JSON)
@Validated
public class UserApiJsonController extends UserApiBaseController
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
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ChangeDisplayNameOperation
    public ResponseEntity<?> changeDisplayName( @RequestBody ChangeDisplayNameRequest request )
    {
        return super.processChangingDisplayName( request.getDisplayName() );
    }

    @PutMapping(
        value = "/changePassword",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ChangePasswordOperation
    public ResponseEntity<?> changePassword( @RequestBody ChangePasswordRequest request )
    {
        return super.processChangingPassword( request.getPassword() );
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
