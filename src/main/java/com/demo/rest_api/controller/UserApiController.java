package com.demo.rest_api.controller;

import com.demo.rest_api.dto.ChangeDisplayNameRequest;
import com.demo.rest_api.dto.ChangePasswordRequest;
import com.demo.rest_api.enums.SortDirection;
import com.demo.rest_api.utils.Constants;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
        return super.processGettingProfile( username );
    }

    @PutMapping( "/changeDisplayName" )
    @ChangeDisplayNameOperation
    public ResponseEntity<?> changeDisplayName( @RequestBody ChangeDisplayNameRequest request )
    {
        return super.processChangingDisplayName( request.getDisplayName() );
    }

    @PutMapping( "/changePassword" )
    @ChangePasswordOperation
    public ResponseEntity<?> changePassword( @RequestBody ChangePasswordRequest request )
    {
        return super.processChangingPassword( request.getPassword() );
    }

    @DeleteMapping( "/deleteAccount" )
    @DeleteAccountOperation
    public ResponseEntity<?> deleteAccount()
    {
        return super.processDeletingAccount();
    }

    @GetMapping( "/filterAndSort" )
    @FilterAndSortOperation
    public ResponseEntity<?> filterAndSort(
            @RequestParam( required = false ) Integer minimumScore,
            @RequestParam( required = false ) Integer maximumScore,
            @RequestParam( required = false ) String usernameKeyword,
            @Parameter( required = true ) @RequestParam( defaultValue = "Ascending" ) SortDirection sortDirection,
            @RequestParam( required = false ) Integer limit )
    {
        return super.processFilteringAndSorting( minimumScore, maximumScore, usernameKeyword, sortDirection, limit );
    }
}
