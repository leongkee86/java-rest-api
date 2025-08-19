package com.demo.rest_api.controller;

import com.demo.rest_api.dto.*;
import com.demo.rest_api.model.User;
import com.demo.rest_api.repository.UserRepository;
import com.demo.rest_api.service.LeaderboardService;
import com.demo.rest_api.service.UserService;
import com.demo.rest_api.utils.Constants;
import com.demo.rest_api.utils.NumberHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping( "/api/game" )
@Tag( name = "Game" )
@Validated
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
                        new ServerApiResponse<>(
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
                        new ServerApiResponse<>(
                            HttpStatus.NOT_FOUND.value(),
                            "Please use the 'api/auth/login' endpoint to log in first.",
                            null,
                            null
                        )
                    );
        }

        return ResponseEntity.ok( userOpt.get() );
    }

    @GetMapping( "/profile" )
    @SecurityRequirement( name = "bearerAuth" )
    @Operation(
        summary = "View a user's game profile",
        description = """
        - View **your own game profile** (requires Bearer token) by omitting the `username` parameter.
        - View **another user's game profile** by providing their `username` (no authentication required).
        """
    )
    @ApiResponses( value =
        {
            @ApiResponse( responseCode = "200", description = "Game profile retrieved successfully" ),
            @ApiResponse( responseCode = "401", description = "Unauthorized — required if viewing your own game profile" ),
            @ApiResponse( responseCode = "404", description = "User not found" )
        }
    )
    public ResponseEntity<?> getProfile( @RequestParam( required = false ) String username )
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
        ResponseEntity<?> authResult = getAuthenticatedUserOrError();

        if (!( authResult.getBody() instanceof User user ))
        {
            return authResult;
        }

        return ResponseEntity
                .status( HttpStatus.OK )
                .body(
                    new ServerApiResponse<>(
                        HttpStatus.OK.value(),
                        Constants.DEFAULT_SUCCESS_MESSAGE,
                        new LeaderboardUserResponse( leaderboardService.getUserRank( user ), user ),
                        null
                    )
                );
    }

    private ResponseEntity<?> getUserProfile( String username )
    {
        Optional<User> optionalUser = userService.findByUsername( username );

        if (optionalUser.isEmpty())
        {
            return ResponseEntity
                    .status( HttpStatus.NOT_FOUND )
                    .body(
                        new ServerApiResponse<>(
                            HttpStatus.NOT_FOUND.value(),
                            "User not found",
                            null,
                            null
                        )
                    );
        }

        User user = optionalUser.get();

        return ResponseEntity
                .status( HttpStatus.OK )
                .body(
                    new ServerApiResponse<>(
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
        description = """
            Guess and enter a number **from 1 to 100** in the `yourGuessedNumber` field. Then, press the **Execute** button and see the result.
            
            ### In each round, there are 3 hidden numbers.
            - **Basic Number**: Gives a hint after each wrong guess. If correctly guessed, awards +1 point and complete the round.
            - **Secret Number**: No hints provided. If correctly guessed, awards +3 points and complete the round.
            - **Trap Number**: No hints provided. If unfortunately guessed, lose 1 point but the round continues.
            
            > Your goal is to guess either the Basic Number or Secret Number to complete the round — but beware of the Trap Number!
            """
    )
    @ApiResponses( value =
    {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized — invalid or missing token",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input — number must be between 1 and 100",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden — guessing is not allowed at this time",
            content = @Content
        )
    } )
    public ResponseEntity<?> guessNumber( @RequestParam( defaultValue = "50" ) int yourGuessedNumber )
    {
        ResponseEntity<?> authenticatedUserOrError = getAuthenticatedUserOrError();

        if (!( authenticatedUserOrError.getBody() instanceof User user ))
        {
            return authenticatedUserOrError;
        }

        if (yourGuessedNumber < 1 || yourGuessedNumber > 100)
        {
            return ResponseEntity
                    .status( HttpStatus.BAD_REQUEST )
                    .body(
                        new ServerApiResponse<>(
                            HttpStatus.BAD_REQUEST.value(),
                            "Please enter a number from 1 to 100 in the 'yourGuessedNumber' field.",
                            null,
                            null
                        )
                    );
        }

        if (!user.getHasGuessNumberStarted())
        {
            user.setHasGuessNumberStarted( true );

            if (user.getGuessNumberCurrentRound() < 1)
            {
                user.setGuessNumberCurrentRound( 1 );
            }
            else
            {
                user.setGuessNumberCurrentRound( user.getGuessNumberCurrentRound() + 1 );
            }

            int[] randomNumbers = NumberHelper.generateDistinctRandomNumbersInRange( 1, 100, 3 );

            user.setGuessNumberBasic( randomNumbers[ 0 ] );
            user.setGuessNumberSecret( randomNumbers[ 1 ] );
            user.setGuessNumberTrap( randomNumbers[ 2 ] );
        }

        user.setAttempts( user.getAttempts() + 1 );

        String roundNumberString = "[ ROUND " + user.getGuessNumberCurrentRound() + " ] ";

        int basicNumber = user.getGuessNumberBasic();
        int secretNumber = user.getGuessNumberSecret();
        int trapNumber = user.getGuessNumberTrap();

        if (yourGuessedNumber == secretNumber)
        {
            user.setHasGuessNumberStarted( false );
            user.setScore( user.getScore() + 3 );
            userService.save( user );

            return ResponseEntity
                    .status( HttpStatus.OK )
                    .body(
                        new ServerApiResponse<>(
                            HttpStatus.OK.value(),
                            roundNumberString + "Congratulations!!! You have correctly guessed the SECRET number (" + yourGuessedNumber + ") and earned 3 points! Your current score is " + user.getScore() + ". Use this endpoint to play a new round.",
                            new LeaderboardUserResponse( leaderboardService.getUserRank( user ), user ),
                            null
                        )
                    );
        }

        if (yourGuessedNumber == trapNumber)
        {
            user.setGuessNumberTrap( 0 );
            user.setScore( user.getScore() - 1 );
            userService.save( user );

            return ResponseEntity
                    .status( HttpStatus.OK )
                    .body(
                        new ServerApiResponse<>(
                            HttpStatus.OK.value(),
                            roundNumberString + "You have unfortunately guessed the TRAP number (" + yourGuessedNumber + ") and lost 1 point... Your current score is " + user.getScore() + ". Use this endpoint to continue guessing the BASIC or SECRET number.",
                            new LeaderboardUserResponse( leaderboardService.getUserRank( user ), user ),
                            null
                        )
                    );
        }

        if (yourGuessedNumber > basicNumber)
        {
            userService.save( user );

            return ResponseEntity
                    .status( HttpStatus.OK )
                    .body(
                        new ServerApiResponse<>(
                            HttpStatus.OK.value(),
                            roundNumberString + "Your guessed number (" + yourGuessedNumber + ") is too high! Try again.",
                            new UserResponse( user ),
                            null
                        )
                    );
        }

        if (yourGuessedNumber < basicNumber)
        {
            userService.save( user );

            return ResponseEntity
                    .status( HttpStatus.OK )
                    .body(
                        new ServerApiResponse<>(
                            HttpStatus.OK.value(),
                            roundNumberString + "Your guessed number (" + yourGuessedNumber + ") is too low! Try again.",
                            new UserResponse( user ),
                            null
                        )
                    );
        }

        user.setHasGuessNumberStarted( false );
        user.setScore( user.getScore() + 1 );
        userService.save( user );

        return ResponseEntity
                .status( HttpStatus.OK )
                .body(
                    new ServerApiResponse<>(
                        HttpStatus.OK.value(),
                        roundNumberString + "Congratulations! You have correctly guessed the BASIC number (" + yourGuessedNumber + ") and earned 1 point. Your current score is " + user.getScore() + ". Use this endpoint to play a new round.",
                        new LeaderboardUserResponse( leaderboardService.getUserRank( user ), user ),
                        null
                    )
                );
    }

    @PostMapping( "/arrangeNumbers" )
    @SecurityRequirement( name = "bearerAuth" )
    @Operation(
            summary = "Start a new round or continue the current round to play the game.",
            description = """
            Guess and enter the correct order of the 5 numbers (1, 2, 3, 4, 5) in the `yourGuessedNumber` field. The correct order may be any arrangement of these numbers (For example: 4, 3, 5, 1, 2). Then, press the **Execute** button and see the result.
            
            ### Hints you will receive after each wrong guess:
            - **[X]**: Number X is at the correct position.
            - **-X-**: Number X is at the wrong position.
            - **?X?**: Number X is invalid. Valid numbers are only 1, 2, 3, 4, 5.
            - **#X#**: Number X is duplicated.
            
            > Your goal is to enter the 5 numbers in the correct order to complete the round. Completing a round awards you +2 points.
            """
    )
    @ApiResponses( value =
    {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized — invalid or missing token",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input — must be exactly 5 numbers between 1 and 5",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden — guessing is not allowed at this time",
            content = @Content
        )
    } )
    public ResponseEntity<?> arrangeNumbers(
        @RequestParam( defaultValue = "1,2,3,4,5" )
        @Size( min = 5, max = 5, message = "You must provide exactly 5 numbers" )
        List<Integer> yourArrangedNumbers
    )
    {
        ResponseEntity<?> authenticatedUserOrError = getAuthenticatedUserOrError();

        if (!( authenticatedUserOrError.getBody() instanceof User user ))
        {
            return authenticatedUserOrError;
        }

        int[] numbers =yourArrangedNumbers.stream().mapToInt( Integer::intValue ).toArray();;

        if (yourArrangedNumbers.size() != 5)
        {
            return ResponseEntity
                    .status( HttpStatus.BAD_REQUEST )
                    .body(
                        new ServerApiResponse<>(
                            HttpStatus.BAD_REQUEST.value(),
                            "Please enter the 5 numbers (1, 2, 3, 4, 5) in the correct order in the 'yourGuessedNumber' field. The correct order may be any arrangement of these numbers.",
                            null,
                            null
                        )
                    );
        }

        if (!user.getHasArrangeNumbersStarted())
        {
            user.setHasArrangeNumbersStarted( true );

            if (user.getArrangeNumbersCurrentRound() < 1)
            {
                user.setArrangeNumbersCurrentRound( 1 );
            }
            else
            {
                user.setArrangeNumbersCurrentRound( user.getArrangeNumbersCurrentRound() + 1 );
            }

            user.setArrangedNumbers( NumberHelper.generateDistinctRandomNumbersInRange( 1, 5, 5 ) );
        }

        user.setAttempts( user.getAttempts() + 1 );

        String roundNumberString = "[ ROUND " + user.getArrangeNumbersCurrentRound() + " ] ";
        int[] arrangedNumbers = user.getArrangedNumbers();
        StringBuilder hint = new StringBuilder();

        List<Integer> checkedNumbers = new ArrayList<Integer>();
        int correctCount = 0;

        for (int i = 0; i < yourArrangedNumbers.size(); i++)
        {
            int number = yourArrangedNumbers.get( i );

            if (i > 0)
            {
                hint.append( " " );
            }

            if (number == arrangedNumbers[ i ])
            {
                correctCount++;
                hint.append( "[" ).append( number ).append( "]" );
            }
            else if (number < 1 || number > 5)
            {
                hint.append( "?" ).append( number ).append( "?" );
            }
            else if (checkedNumbers.contains( number ))
            {
                hint.append( "#" ).append( number ).append( "#" );
            }
            else
            {
                hint.append( "-" ).append( number ).append( "-" );
            }

            checkedNumbers.add( number );
        }

        if (correctCount < 5)
        {
            userService.save( user );

            return ResponseEntity
                    .status( HttpStatus.OK )
                    .body(
                        new ServerApiResponse<>(
                            HttpStatus.OK.value(),
                            roundNumberString + "Here is the hint to help you figure out the correct order of the 5 numbers: " + hint + ". Use this endpoint to try again.",
                            new UserResponse( user ),
                            null
                        )
                    );
        }

        user.setHasArrangeNumbersStarted( false );
        user.setScore( user.getScore() + 2 );
        userService.save( user );

        String result = yourArrangedNumbers.stream().map( String::valueOf ).collect(Collectors.joining( "," ) );

        return ResponseEntity
                .status( HttpStatus.OK )
                .body(
                    new ServerApiResponse<>(
                        HttpStatus.OK.value(),
                        roundNumberString + "Congratulations! You have correctly guessed the order of the 5 numbers (" + result + ") and earned 2 points. Your current score is " + user.getScore() + ". Use this endpoint to play a new round.",
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
    @ApiResponses( value =
    {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content
        )
    } )
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
                    new ServerApiResponse<>(
                        HttpStatus.OK.value(),
                        Constants.DEFAULT_SUCCESS_MESSAGE,
                        leaderboardUsers,
                        null
                    )
                );
    }
}
