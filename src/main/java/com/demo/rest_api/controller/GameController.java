package com.demo.rest_api.controller;

import com.demo.rest_api.dto.ApiResponse;
import com.demo.rest_api.dto.LeaderboardUserResponse;
import com.demo.rest_api.dto.PlayRequest;
import com.demo.rest_api.dto.UserResponse;
import com.demo.rest_api.model.User;
import com.demo.rest_api.repository.UserRepository;
import com.demo.rest_api.service.UserService;
import com.demo.rest_api.utils.Constants;
import com.demo.rest_api.utils.NumberHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    private boolean hasGameStarted = false;
    private int currentNumber = 0;

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
    public ResponseEntity<?> guessNumber( @RequestBody PlayRequest request )
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

        User user = userOpt.get();

        if (!hasGameStarted)
        {
            hasGameStarted = true;
            currentNumber = NumberHelper.getRandomNumber( 1, 100 );
        }

        int number = request.getYourGuessedNumber();

        if (number > currentNumber)
        {
            return ResponseEntity
                    .status( HttpStatus.OK )
                    .body(
                        new ApiResponse<>(
                            HttpStatus.OK.value(),
                            "Your guessed number is too high! Try again.",
                            null,
                            null
                        )
                    );
        }

        if (number < currentNumber)
        {
            return ResponseEntity
                    .status( HttpStatus.OK )
                    .body(
                        new ApiResponse<>(
                            HttpStatus.OK.value(),
                            "Your guessed number is too low! Try again.",
                            null,
                            null
                        )
                    );
        }

        hasGameStarted = false;
        user.setScore( user.getScore() + 1 );
        userService.save( user );

        return ResponseEntity
                .status( HttpStatus.OK )
                .body(
                    new ApiResponse<>(
                        HttpStatus.OK.value(),
                            "Congratulations! You have guessed the correct number and earned 1 point. Your current score is " + user.getScore() + ". Use this endpoint to play a new round.",
                        new UserResponse( user.getUsername(), user.getScore() ),
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
        List<User> users = userRepository.findAll( Sort.by( Sort.Direction.DESC, "score" ) )
                            .stream()
                            .limit( limit )
                            .toList();

        List<LeaderboardUserResponse> leaderboardUsers = new ArrayList<>();

        int rank = 1;
        for (User user : users)
        {
            leaderboardUsers.add( new LeaderboardUserResponse( rank, user.getUsername(), user.getScore() ) );
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
