/*
 * ****************************************************************************
 * File: UserApiBaseController.java
 * Author: Lim Leong Kee
 * Email: leongkee86@gmail.com
 * Last Modified Date: 27 August 2025
 * 
 * Description:
 * This class is responsible for handling API operations to manage user account
 * settings. It provides reusable methods that extending classes can expose as
 * REST API endpoints. It integrates with Swagger UI using annotations to
 * automatically generate API documentation.
 * ****************************************************************************
 */

package com.demo.rest_api.controller;

import com.demo.rest_api.dto.LeaderboardUserResponse;
import com.demo.rest_api.dto.ServerApiResponse;
import com.demo.rest_api.dto.UserResponse;
import com.demo.rest_api.enums.SortDirection;
import com.demo.rest_api.model.User;
import com.demo.rest_api.repository.UserRepository;
import com.demo.rest_api.service.AuthenticationService;
import com.demo.rest_api.service.LeaderboardService;
import com.demo.rest_api.service.UserService;
import com.demo.rest_api.utils.Constants;
import com.demo.rest_api.utils.PaginationHelper;
import com.demo.rest_api.utils.StringHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;
import java.util.regex.Pattern;

public class UserApiBaseController
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private LeaderboardService leaderboardService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Target( ElementType.METHOD )
    @Retention( RetentionPolicy.RUNTIME )
    @SecurityRequirement( name = "bearerAuth" )
    @Operation(
        operationId = "2_1",
        summary = "View your own game profile.",
        description = """
            View the information of your own game profile.
            """
    )
    @ApiResponses( value =
        {
            @ApiResponse( responseCode = "200", description = "Game profile retrieved successfully", content = @Content( mediaType = "" ) ),
            @ApiResponse( responseCode = "401", description = "Unauthorized — invalid or missing token", content = @Content( mediaType = "" ) ),
            @ApiResponse( responseCode = "404", description = "User not found", content = @Content( mediaType = "" ) )
        }
    )
    public @interface GetOwnProfileOperation {}

    protected ResponseEntity<?> processGettingProfile()
    {
        return processGettingProfile( null );
    }

    @Target( ElementType.METHOD )
    @Retention( RetentionPolicy.RUNTIME )
    @Operation(
        operationId = "2_1",
        summary = "View a specific user's game profile.",
        description = """
            View the information of a specific user's game profile by providing their `username`.
            """
    )
    @ApiResponses( value =
        {
            @ApiResponse( responseCode = "200", description = "Game profile retrieved successfully", content = @Content( mediaType = "" ) ),
            @ApiResponse( responseCode = "404", description = "User not found", content = @Content( mediaType = "" ) )
        }
    )
    public @interface GetUserProfileOperation {}

    protected ResponseEntity<?> processGettingProfile( String username )
    {
        if (StringHelper.isBlank( username ))
        {
            return getOwnProfile();
        }
        else
        {
            return getUserProfile( username );
        }
    }

    private ResponseEntity<?> getOwnProfile()
    {
        ResponseEntity<?> authenticatedUserOrError = authenticationService.getAuthenticatedUserOrError();

        if (!( authenticatedUserOrError.getBody() instanceof User user ))
        {
            return authenticatedUserOrError;
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

    @Target( ElementType.METHOD )
    @Retention( RetentionPolicy.RUNTIME )
    @SecurityRequirement( name = "bearerAuth" )
    @Operation(
        operationId = "2_3",
        summary = "Change the display name of your account.",
        description = "Change the display name of your account."
    )
    @ApiResponses( value =
        {
            @ApiResponse( responseCode = "200", description = "OK", content = @Content( mediaType = "" ) ),
            @ApiResponse( responseCode = "401", description = "Unauthorized — invalid or missing token", content = @Content( mediaType = "" ) )
        }
    )
    public @interface ChangeDisplayNameOperation {}

    protected ResponseEntity<?> processChangingDisplayName( String displayName )
    {
        ResponseEntity<?> authenticatedUserOrError = authenticationService.getAuthenticatedUserOrError();

        if (!( authenticatedUserOrError.getBody() instanceof User user ))
        {
            return authenticatedUserOrError;
        }

        if (displayName.length() < Constants.DISPLAY_NAME_LENGTH)
        {
            return ServerApiResponse.generateResponseEntity(
                HttpStatus.BAD_REQUEST,
                "Display name must be at least " + Constants.DISPLAY_NAME_LENGTH + " characters long."
            );
        }

        user.setDisplayName( displayName );
        userService.save( user );

        return ServerApiResponse.generateResponseEntity(
                HttpStatus.OK,
                "The display name of your account has been successfully changed.",
                new UserResponse( user )
        );
    }

    @Target( ElementType.METHOD )
    @Retention( RetentionPolicy.RUNTIME )
    @SecurityRequirement( name = "bearerAuth" )
    @Operation(
        operationId = "2_4",
        summary = "Change the password of your account.",
        description = "Change the password of your account."
    )
    @ApiResponses( value =
        {
            @ApiResponse( responseCode = "200", description = "OK", content = @Content( mediaType = "" ) ),
            @ApiResponse( responseCode = "401", description = "Unauthorized — invalid or missing token", content = @Content( mediaType = "" ) )
        }
    )
    public @interface ChangePasswordOperation {}

    protected ResponseEntity<?> processChangingPassword( String password )
    {
        ResponseEntity<?> authenticatedUserOrError = authenticationService.getAuthenticatedUserOrError();

        if (!( authenticatedUserOrError.getBody() instanceof User user ))
        {
            return authenticatedUserOrError;
        }

        if (password.length() < Constants.PASSWORD_LENGTH)
        {
            return ServerApiResponse.generateResponseEntity(
                HttpStatus.BAD_REQUEST,
                "Password must be at least " + Constants.PASSWORD_LENGTH + " characters long."
            );
        }

        user.setPassword( password );
        user.setIsPasswordEncoded( false );
        userService.save( user );

        return ServerApiResponse.generateResponseEntity(
            HttpStatus.OK,
                "The password of your account has been successfully changed.",
            new UserResponse( user )
        );
    }

    @Target( ElementType.METHOD )
    @Retention( RetentionPolicy.RUNTIME )
    @SecurityRequirement( name = "bearerAuth" )
    @Operation(
        operationId = "2_5",
        summary = "Delete your account permanently.",
        description = "Delete your account permanently."
    )
    @ApiResponses( value =
        {
            @ApiResponse( responseCode = "200", description = "OK", content = @Content( mediaType = "" ) ),
            @ApiResponse( responseCode = "401", description = "Unauthorized — invalid or missing token", content = @Content( mediaType = "" ) )
        }
    )
    public @interface DeleteAccountOperation {}

    protected ResponseEntity<?> processDeletingAccount()
    {
        ResponseEntity<?> authenticatedUserOrError = authenticationService.getAuthenticatedUserOrError();

        if (!( authenticatedUserOrError.getBody() instanceof User user ))
        {
            return authenticatedUserOrError;
        }

        String username = user.getUsername();

        if (!userService.deleteUserByUsername( username ))
        {
            return ServerApiResponse.generateResponseEntity(
                HttpStatus.NOT_FOUND,
                "User not found. Unable to delete your account with the username '" + username + "'."
            );
        }

        return ServerApiResponse.generateResponseEntity(
            HttpStatus.OK,
            "Your account with the username '" + username + "' has been successfully deleted."
        );
    }

    @Target( ElementType.METHOD )
    @Retention( RetentionPolicy.RUNTIME )
    @Operation(
        operationId = "2_2",
        summary = "Get a filtered, sorted, and paginated list of users.",
        description = """
            Retrieves a paginated list of users filtered using **optional** parameters: minimum score (`minimumScore`), maximum score (`maximumScore`), and case-insensitive **username** keyword matching (`usernameKeyword`). Results are sorted in ascending or descending order based on the **required** `sortDirection` parameter.
            
            Supports **optional pagination** using the `page` (1-based) and `limit` query parameters to control the page number and the number of results per page.
            
            If only `limit` is provided (without `page`), the first `limit` number of filtered users will be returned. For example, `limit=10` returns the first 10 filtered users.
            """
    )
    @ApiResponses( value =
    {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content( mediaType = "" )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content( mediaType = "" )
        )
    } )
    public @interface FilterAndSortOperation {}

    public ResponseEntity<?> processFilteringAndSorting(
        Integer minimumScore, Integer maximumScore, String usernameKeyword, SortDirection sortDirection,
        Integer page, Integer limit )
    {
        Query query = new Query();

        if (minimumScore != null && maximumScore != null)
        {
            query.addCriteria( Criteria.where( Constants.DATABASE_USER_SCORE_KEY ).gte( minimumScore ).lte( maximumScore ) );
        }
        else if (minimumScore != null)
        {
            query.addCriteria( Criteria.where( Constants.DATABASE_USER_SCORE_KEY ).gte( minimumScore ) );
        }
        else if (maximumScore != null)
        {
            query.addCriteria( Criteria.where( Constants.DATABASE_USER_SCORE_KEY ).lte( maximumScore ) );
        }

        if (!StringHelper.isBlank( usernameKeyword ))
        {
            query.addCriteria(
                Criteria.where( Constants.DATABASE_USER_USERNAME_KEY )
                    .regex( ".*" + Pattern.quote( usernameKeyword.trim() ) + ".*", "i" )
            );
        }

        Sort.Direction direction = sortDirection.toSpringSort();
        query.with( Sort.by( direction, Constants.DATABASE_USER_SCORE_KEY ) );

        PaginationHelper.PaginationMetadata paginationMetadata = null;

        if (page != null && limit != null)
        {
            long totalMatchedUsers = PaginationHelper.countWithoutPagination( mongoTemplate, query, User.class );

            try
            {
                paginationMetadata = PaginationHelper.applyPagination( query, page, limit, totalMatchedUsers );
            }
            catch ( IllegalArgumentException exception )
            {
                return ServerApiResponse.generateResponseEntity(
                        HttpStatus.BAD_REQUEST,
                        exception.toString().replace( "java.lang.IllegalArgumentException: ", "" )
                );
            }
        }
        else if (limit != null)
        {
            query.limit( limit );
        }

        List<User> users = mongoTemplate.find( query, User.class );
        List<UserResponse> responseUsers = new ArrayList<>();

        if (!users.isEmpty())
        {
            for (User user : users)
            {
                responseUsers.add( new UserResponse( user ) );
            }
        }

        Map<String,Object> metadata = new LinkedHashMap<>();
        metadata.put( "totalUsers", userRepository.count() );
        metadata.put( "returnedUsers", responseUsers.size() );
        metadata.put( "pagination", paginationMetadata );

        return ServerApiResponse.generateResponseEntity(
                HttpStatus.OK,
                Constants.DEFAULT_SUCCESS_MESSAGE,
                responseUsers,
                metadata
        );
    }
}
