package com.demo.rest_api.controller;

import com.demo.rest_api.dto.LeaderboardUserResponse;
import com.demo.rest_api.dto.ServerApiResponse;
import com.demo.rest_api.model.User;
import com.demo.rest_api.service.AuthenticationService;
import com.demo.rest_api.service.LeaderboardService;
import com.demo.rest_api.service.UserService;
import com.demo.rest_api.utils.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;

public class UserApiBaseController
{
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private LeaderboardService leaderboardService;

    @Target( ElementType.METHOD )
    @Retention( RetentionPolicy.RUNTIME )
    @SecurityRequirement( name = "bearerAuth" )
    @Operation(
        operationId = "2_1",
        summary = "View a user's game profile",
        description = """
        View **your own game profile** (requires Bearer token) by omitting the `username` parameter.
        
        View **another user's game profile** by providing their `username` (no authentication required).
        """
    )
    @ApiResponses( value =
        {
            @ApiResponse( responseCode = "200", description = "Game profile retrieved successfully", content = @Content( mediaType = "" ) ),
            @ApiResponse( responseCode = "401", description = "Unauthorized — required if viewing your own game profile", content = @Content( mediaType = "" ) ),
            @ApiResponse( responseCode = "404", description = "User not found", content = @Content( mediaType = "" ) )
        }
    )
    public @interface GetProfileOperation {}

    protected ResponseEntity<?> getProfile(@RequestParam( required = false ) String username )
    {
        if (username != null && !username.isBlank())
        {
            return getUserProfile( username );
        }
        else
        {
            return getOwnProfile();
        }
    }

    private ResponseEntity<?> getOwnProfile()
    {
        ResponseEntity<?> authResult = authenticationService.getAuthenticatedUserOrError();

        if (!( authResult.getBody() instanceof User user ))
        {
            return authResult;
        }

        return ServerApiResponse.generateResponseEntity(
                HttpStatus.OK,
                Constants.DEFAULT_SUCCESS_MESSAGE,
                new LeaderboardUserResponse( leaderboardService.getUserRank( user ), user )
        );
    }

    private ResponseEntity<?> getUserProfile( String username )
    {
        Optional<User> optionalUser = userService.findByUsername( username );

        if (optionalUser.isEmpty())
        {
            return ServerApiResponse.generateResponseEntity(
                    HttpStatus.NOT_FOUND,
                    "User not found. Please check the username and try again."
            );
        }

        User user = optionalUser.get();

        return ServerApiResponse.generateResponseEntity(
                HttpStatus.OK,
                Constants.DEFAULT_SUCCESS_MESSAGE,
                new LeaderboardUserResponse( leaderboardService.getUserRank( user ), user )
        );
    }
}
