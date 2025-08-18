package com.demo.rest_api.controller;

import com.demo.rest_api.dto.ApiResponse;
import com.demo.rest_api.dto.LeaderboardUserResponse;
import com.demo.rest_api.dto.PlayRequest;
import com.demo.rest_api.dto.UserResponse;
import com.demo.rest_api.model.User;
import com.demo.rest_api.repository.UserRepository;
import com.demo.rest_api.service.LeaderboardService;
import com.demo.rest_api.service.UserService;
import com.demo.rest_api.utils.Constants;
import com.demo.rest_api.utils.NumberHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@Tag( name = "Game" )
@RequestMapping( "/api/game" )
public class GameController
{
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LeaderboardService leaderboardService;

    private ResponseEntity<?> getAuthenticatedUserOrError()
    {
        // At this point, Spring Security should have already set the authenticated user.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated())
        {
            return ResponseEntity
                    .status( HttpStatus.UNAUTHORIZED )
                    .body(
                        new ApiResponse<>(
                            HttpStatus.UNAUTHORIZED.value(),
                            "invalid token",
                            null,
                            null
                        )
                    );
        }

        String username = authentication.getName();  // from JWT token subject

        Optional<User> userOpt = userService.findByUsername( username );

        if (userOpt.isEmpty())
        {
            return ResponseEntity
                    .status( HttpStatus.NOT_FOUND )
                    .body(
                        new ApiResponse<>(
                            HttpStatus.NOT_FOUND.value(),
                            "Please use the 'api/auth/login' endpoint to log in first.",
                            null,
                            null
                        )
                    );
        }

        return ResponseEntity.ok(userOpt.get());
    }

    @PostMapping( "/profile" )
    @SecurityRequirement( name = "bearerAuth" )
    @Operation(
        summary = "View your profile information.",
        description = ""
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Unauthorized - invalid or missing token",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema( implementation = ErrorResponse.class )
        )
    )
    public ResponseEntity<?> getProfile()
    {
        ResponseEntity<?> authenticatedUserOrError = getAuthenticatedUserOrError();

        if (!( authenticatedUserOrError.getBody() instanceof User user ))
        {
            return authenticatedUserOrError;
        }

        return ResponseEntity
                .status( HttpStatus.OK )
                .body(
                    new ApiResponse<>(
                        HttpStatus.OK.value(),
                        Constants.DEFAULT_SUCCESS_MESSAGE,
                        new LeaderboardUserResponse( leaderboardService.getUserRank( user ), user ),
                        null
                    )
                );
    }

    @PostMapping( "/guessNumber" )
    @SecurityRequirement( name = "bearerAuth" )
    @Operation(
        summary = "Start a new round or continue the current round to play the game.",
        description = "Guess and enter a number **from 1 to 100** in the `yourGuessedNumber` field. Then, press the **Execute** button and see the result."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Unauthorized - invalid or missing token",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema( implementation = ErrorResponse.class )
        )
    )
    public ResponseEntity<?> guessNumber( @Valid @RequestBody PlayRequest request )
    {
        ResponseEntity<?> authenticatedUserOrError = getAuthenticatedUserOrError();

        if (!( authenticatedUserOrError.getBody() instanceof User user ))
        {
            return authenticatedUserOrError;
        }

        int number = request.getYourGuessedNumber();

        if (number < 1 || number > 100)
        {
            return ResponseEntity
                    .status( HttpStatus.BAD_REQUEST )
                    .body(
                        new ApiResponse<>(
                            HttpStatus.BAD_REQUEST.value(),
                            "Please enter a number from 1 to 100 in the 'yourGuessedNumber' field.",
                            null,
                            null
                        )
                    );
        }

        if (user.getCurrentNumber() == 0)
        {
            if (user.getRounds() < 1)
            {
                user.setRounds( 1 );
            }
            else
            {
                user.setRounds( user.getRounds() + 1 );
            }

            user.setCurrentNumber( NumberHelper.getRandomNumber( 1, 100 ) );
        }

        user.setAttempts( user.getAttempts() + 1 );

        String roundNumberString = "[ ROUND " + user.getRounds() + " ] ";

        int currentNumber = user.getCurrentNumber();

        if (number > currentNumber)
        {
            userService.save( user );

            return ResponseEntity
                    .status( HttpStatus.OK )
                    .body(
                        new ApiResponse<>(
                            HttpStatus.OK.value(),
                                roundNumberString + "Your guessed number (" + number + ") is too high! Try again.",
                            new UserResponse( user ),
                            null
                        )
                    );
        }

        if (number < currentNumber)
        {
            userService.save( user );

            return ResponseEntity
                    .status( HttpStatus.OK )
                    .body(
                        new ApiResponse<>(
                            HttpStatus.OK.value(),
                                roundNumberString + "Your guessed number (" + number + ") is too low! Try again.",
                            new UserResponse( user ),
                            null
                        )
                    );
        }

        user.setCurrentNumber( 0 );
        user.setScore( user.getScore() + 1 );
        userService.save( user );

        return ResponseEntity
                .status( HttpStatus.OK )
                .body(
                    new ApiResponse<>(
                        HttpStatus.OK.value(),
                        roundNumberString + "Congratulations! You have guessed the correct number (" + number + ") and earned 1 point. Your current score is " + user.getScore() + ". Use this endpoint to play a new round.",
                        new LeaderboardUserResponse( leaderboardService.getUserRank( user ), user ),
                        null
                    )
                );
    }

    @GetMapping( "/leaderboard" )
    @Operation(
        summary = "Get the top users from the leaderboard.",
        description = "Returns a list of users sorted by their score in descending order. You can optionally limit the number of users returned using the `limit` query parameter."
    )
    public ResponseEntity<?> getLeaderboard( @RequestParam( defaultValue = "100" ) int limit )
    {
        List<User> users = userRepository.findAll(
                                Sort.by(
                                    Sort.Order.desc( "score" ),     // highest score first
                                    Sort.Order.asc( "attempts" ),   // if scores equal, fewer attempts first
                                    Sort.Order.asc( "rounds" )       // if both equal, lower rounds first
                                )
                            )
                            .stream()
                            .limit( limit )
                            .toList();

        List<LeaderboardUserResponse> leaderboardUsers = new ArrayList<>();

        long rank = 1;
        for (User user : users)
        {
            leaderboardUsers.add( new LeaderboardUserResponse( rank, user ) );
            rank++;
        }

        return ResponseEntity
                .status( HttpStatus.OK )
                .body(
                    new ApiResponse<>(
                        HttpStatus.OK.value(),
                        Constants.DEFAULT_SUCCESS_MESSAGE,
                        leaderboardUsers,
                        null
                    )
                );
    }
}
