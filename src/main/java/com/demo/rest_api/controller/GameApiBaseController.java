package com.demo.rest_api.controller;

import com.demo.rest_api.dto.*;
import com.demo.rest_api.enums.RockPaperScissors;
import com.demo.rest_api.model.User;
import com.demo.rest_api.repository.UserRepository;
import com.demo.rest_api.service.AuthenticationService;
import com.demo.rest_api.service.LeaderboardService;
import com.demo.rest_api.service.UserService;
import com.demo.rest_api.utils.Constants;
import com.demo.rest_api.utils.EnumHelper;
import com.demo.rest_api.utils.NumberHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class GameApiBaseController
{
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

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

    protected ResponseEntity<?> getProfile( @RequestParam( required = false ) String username )
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

    @Target( ElementType.METHOD )
    @Retention( RetentionPolicy.RUNTIME )
    @SecurityRequirement( name = "bearerAuth" )
    @Operation(
        operationId = "2_3",
        summary = "Guess a number from 1 to 100 in this game. Use this endpoint to start a new round or continue the current round to play",
        description = """
            Guess and enter a number **from 1 to 100** in the `yourGuessedNumber` field. Then, press the **Execute** button and see the result of the game.
            
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
            content = @Content( mediaType = "" )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized — invalid or missing token",
            content = @Content( mediaType = "" )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input — number must be between 1 and 100",
            content = @Content( mediaType = "" )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden — guessing is not allowed at this time",
            content = @Content( mediaType = "" )
        )
    } )
    public @interface GuessNumberOperation {}

    protected ResponseEntity<?> guessNumber( int yourGuessedNumber )
    {
        ResponseEntity<?> authenticatedUserOrError = authenticationService.getAuthenticatedUserOrError();

        if (!( authenticatedUserOrError.getBody() instanceof User user ))
        {
            return authenticatedUserOrError;
        }

        if (yourGuessedNumber < 1 || yourGuessedNumber > 100)
        {
            return ServerApiResponse.generateResponseEntity(
                    HttpStatus.BAD_REQUEST,
                    "Please enter a number from 1 to 100 in the 'yourGuessedNumber' field."
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

            return ServerApiResponse.generateResponseEntity(
                    HttpStatus.OK,
                    roundNumberString + "Congratulations!!! You have successfully guessed the SECRET number (" + yourGuessedNumber + ") and earned 3 points! Your current score is " + user.getScore() + ". Use this endpoint to play a new round.",
                    new LeaderboardUserResponse( leaderboardService.getUserRank( user ), user )
                    );
        }

        if (yourGuessedNumber == trapNumber)
        {
            user.setGuessNumberTrap( 0 );
            user.setScore( user.getScore() - 1 );
            userService.save( user );

            return ServerApiResponse.generateResponseEntity(
                    HttpStatus.OK,
                    roundNumberString + "You have unfortunately guessed the TRAP number (" + yourGuessedNumber + ") and lost 1 point... Your current score is " + user.getScore() + ". Use this endpoint to continue guessing the BASIC or SECRET number.",
                    new LeaderboardUserResponse( leaderboardService.getUserRank( user ), user )
                    );
        }

        if (yourGuessedNumber > basicNumber)
        {
            userService.save( user );

            return ServerApiResponse.generateResponseEntity(
                    HttpStatus.OK,
                    roundNumberString + "Your guessed number (" + yourGuessedNumber + ") is too high! Try again.",
                    new UserResponse( user )
                    );
        }

        if (yourGuessedNumber < basicNumber)
        {
            userService.save( user );

            return ServerApiResponse.generateResponseEntity(
                    HttpStatus.OK,
                    roundNumberString + "Your guessed number (" + yourGuessedNumber + ") is too low! Try again.",
                    new UserResponse( user )
                    );
        }

        user.setHasGuessNumberStarted( false );
        user.setScore( user.getScore() + 1 );
        userService.save( user );

        return ServerApiResponse.generateResponseEntity(
                HttpStatus.OK,
                roundNumberString + "Congratulations! You have successfully guessed the BASIC number (" + yourGuessedNumber + ") and earned 1 point. Your current score is " + user.getScore() + ". Use this endpoint to play a new round.",
                new LeaderboardUserResponse( leaderboardService.getUserRank( user ), user )
                );
    }

    @Target( ElementType.METHOD )
    @Retention( RetentionPolicy.RUNTIME )
    @SecurityRequirement( name = "bearerAuth" )
    @Operation(
        operationId = "2_4",
        summary = "Guess the sequence of 5 numbers in this game. Use this endpoint to start a new round or continue the current round to play.",
        description = """
            Guess and enter the sequence of the 5 numbers (1, 2, 3, 4, 5) in the `yourGuessedNumber` field. The sequence can be any arrangement of these numbers (For example: 4, 3, 5, 1, 2). Then, press the **Execute** button and see the result of the game.
            
            ### Hints you will receive after each wrong guess:
            - **[X]**: Number X is at the correct position.
            - **-X-**: Number X is at the wrong position.
            - **?X?**: Number X is invalid. Valid numbers are only 1, 2, 3, 4, 5.
            - **#X#**: Number X is duplicated.
            
            > Your goal is to enter the 5 numbers in the sequence defined by the game for the current round to complete the round. Completing a round awards you +2 points.
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
            responseCode = "401",
            description = "Unauthorized — invalid or missing token",
            content = @Content( mediaType = "" )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input — must be exactly 5 numbers between 1 and 5",
            content = @Content( mediaType = "" )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden — guessing is not allowed at this time",
            content = @Content( mediaType = "" )
        )
    } )
    public @interface ArrangeNumbersOperation {}

    protected ResponseEntity<?> arrangeNumbers(
        @Parameter(
            description = "Guess the sequence of the 5 numbers defined by the game for the current round",
            required = true
        )
        @RequestParam( defaultValue = "1,2,3,4,5" )
        @Size( min = 5, max = 5, message = "You must provide exactly 5 numbers" )
        List<Integer> yourArrangedNumbers
    )
    {
        ResponseEntity<?> authenticatedUserOrError = authenticationService.getAuthenticatedUserOrError();

        if (!( authenticatedUserOrError.getBody() instanceof User user ))
        {
            return authenticatedUserOrError;
        }

        if (yourArrangedNumbers.size() != 5)
        {
            return ServerApiResponse.generateResponseEntity(
                    HttpStatus.BAD_REQUEST,
                    "Please enter the sequence of the 5 numbers (1, 2, 3, 4, 5) defined by the game for the current round in the 'yourGuessedNumber' field. The sequence can be any arrangement of these numbers."
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

            return ServerApiResponse.generateResponseEntity(
                    HttpStatus.OK,
                    roundNumberString + "Here is the hint to help you figure out the sequence of the 5 numbers: " + hint + ". Use this endpoint to try again.",
                    new UserResponse( user )
                    );
        }

        user.setHasArrangeNumbersStarted( false );
        user.setScore( user.getScore() + 2 );
        userService.save( user );

        String result = yourArrangedNumbers.stream().map( String::valueOf ).collect(Collectors.joining( "," ) );

        return ServerApiResponse.generateResponseEntity(
                HttpStatus.OK,
                roundNumberString + "Congratulations! You have successfully guessed the sequence of the 5 numbers (" + result + ") and earned 2 points. Your current score is " + user.getScore() + ". Use this endpoint to play a new round.",
                new LeaderboardUserResponse( leaderboardService.getUserRank( user ), user )
                );
    }

    @Target( ElementType.METHOD )
    @Retention( RetentionPolicy.RUNTIME )
    @SecurityRequirement( name = "bearerAuth" )
    @Operation(
        operationId = "2_6",
        summary = "Play the Rock Paper Scissors game with another user. Use this endpoint to start a new round or continue the current round to play the game.",
        description = """
            **Important:** You must have at least 1 point to play this game. If you do not have enough points, you can play other games to earn points or claim bonus points if you have not claimed them yet.
            
            1. The `opponentUsername` field is **optional**. You may choose a specific opponent by entering the opponent's username in the `opponentUsername` field or let the system find a random opponent with enough points (see **step 4**) by leaving the `opponentUsername` field empty.
            
            2. Select your choice — Rock, Paper or Scissors — from the drop-down list in the `yourChoice` field.
            > Rock beats Scissors.
            > Scissors beats Paper.
            > Paper beats Rock.
            
            3. Enter how many points that you want to stake in the `pointsToStake` field. If you win, you will receive the staked points from the opponent. If you lose, you will transfer the staked points to the opponent.
            
            4. If you leave the `opponentUsername` field empty, the system will automatically select a random opponent whose score is greater than or equal to the number of points that you entered in the `pointsToStake` field.
            
            5. Press the **Execute** button and see the result of the game.
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
            responseCode = "401",
            description = "Unauthorized — invalid or missing token",
            content = @Content( mediaType = "" )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content( mediaType = "" )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content( mediaType = "" )
        )
    } )
    public @interface PlayRockPaperScissorsOperation {}

    public ResponseEntity<?> playRockPaperScissors( String opponentUsername, RockPaperScissors yourChoice, int pointsToStake )
    {
        ResponseEntity<?> authenticatedUserOrError = authenticationService.getAuthenticatedUserOrError();

        if (!( authenticatedUserOrError.getBody() instanceof User user ))
        {
            return authenticatedUserOrError;
        }

        if (pointsToStake < 1)
        {
            return ServerApiResponse.generateResponseEntity(
                HttpStatus.BAD_REQUEST,
                "The value of the 'pointsToStake' field must be at least 1."
            );
        }

        if (pointsToStake > user.getScore())
        {
            return ServerApiResponse.generateResponseEntity(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "You cannot stake more points than you currently have (Max: " + user.getScore() + ")."
            );
        }

        Optional<User> optionalOpponentUser;
        User opponentUser = null;

        if (opponentUsername == null || opponentUsername.isBlank())
        {
            optionalOpponentUser = userService.findRandomUserWithMinimumScore( pointsToStake, List.of( user.getUsername() ) );

            if (optionalOpponentUser.isEmpty())
            {
                return ServerApiResponse.generateResponseEntity(
                        HttpStatus.NOT_FOUND,
                        "Opponent user not found. Try to lower down the value in the 'pointsToStake' field."
                );
            }
        }
        else
        {
            if (user.getUsername().equalsIgnoreCase( opponentUsername ))
            {
                return ServerApiResponse.generateResponseEntity(
                    HttpStatus.CONFLICT,
                    "You cannot choose yourself as your opponent. Please enter a different username in the 'opponentUsername' field."
                );
            }

            optionalOpponentUser = userService.findByUsername( opponentUsername );

            if (optionalOpponentUser.isEmpty())
            {
                return ServerApiResponse.generateResponseEntity(
                        HttpStatus.NOT_FOUND,
                        "Opponent user not found. Please make sure that you enter the correct username in the 'opponentUsername' field."
                        );
            }
        }

        opponentUser = optionalOpponentUser.get();
        opponentUsername = opponentUser.getUsername();

        if (pointsToStake > opponentUser.getScore())
        {
            return ServerApiResponse.generateResponseEntity(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "You cannot stake more points than your opponent currently have (Max: " + opponentUser.getScore() + ")."
                    );
        }

        if (user.getRockPaperScissorsCurrentRound() < 1)
        {
            user.setRockPaperScissorsCurrentRound( 1 );
        }
        else
        {
            user.setRockPaperScissorsCurrentRound( user.getRockPaperScissorsCurrentRound() + 1 );
        }

        user.setAttempts( user.getAttempts() + 1 );

        RockPaperScissors opponentChoice = EnumHelper.getRandomEnum( RockPaperScissors.class );

        String result = "[ ROUND " + user.getRockPaperScissorsCurrentRound() + " ] "
                        + "Your choice: { " + yourChoice.toString() + " } versus opponent " + opponentUsername + "'s choice: { " + opponentChoice.toString() + " } | ";

        if (yourChoice == opponentChoice)
        {
            result += "It is a draw. Both players keep their points.";
        }
        else if (yourChoice.beats( opponentChoice ))
        {
            result += "Congratulations! You won and received " + pointsToStake + " point(s) from '" + opponentUsername + "'.";

            opponentUser.setScore( opponentUser.getScore() - pointsToStake );
            user.setScore( user.getScore() + pointsToStake );

            userService.save( opponentUser );
            userService.save( user );
        }
        else
        {
            result += "You lost and transferred " + pointsToStake + " point(s) to '" + opponentUsername + "' .";

            user.setScore( user.getScore() - pointsToStake );
            opponentUser.setScore( opponentUser.getScore() + pointsToStake );

            userService.save( opponentUser );
            userService.save( user );
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put( "user", new LeaderboardUserResponse( leaderboardService.getUserRank( user ), user ) );
        data.put( "opponent", new LeaderboardUserResponse( leaderboardService.getUserRank( opponentUser ), opponentUser ) );

        return ServerApiResponse.generateResponseEntity(
                HttpStatus.OK,
                result + " Your current score is " + user.getScore() + ". Use this endpoint to play a new round.",
                data
                );
    }

    @Target( ElementType.METHOD )
    @Retention( RetentionPolicy.RUNTIME )
    @SecurityRequirement( name = "bearerAuth" )
    @Operation(
        operationId = "2_5",
        summary = "Practise the Rock Paper Scissors game for fun.",
        description = """
            Practise the Rock Paper Scissors game for fun. No points to earn. The number of attempts will not increase with each play.
            
            Select your choice — Rock, Paper or Scissors — from the drop-down list in the `yourChoice` field. Rock beats Scissors. Scissors beats Paper. Paper beats Rock.
            
            Then, press the **Execute** button and see the result of the game.
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
            responseCode = "401",
            description = "Unauthorized — invalid or missing token",
            content = @Content( mediaType = "" )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content( mediaType = "" )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content( mediaType = "" )
        )
    } )
    public @interface PractiseRockPaperScissorsOperation {}

    protected ResponseEntity<?> practiseRockPaperScissors( RockPaperScissors yourChoice )
    {
        ResponseEntity<?> authenticatedUserOrError = authenticationService.getAuthenticatedUserOrError();

        if (!( authenticatedUserOrError.getBody() instanceof User user ))
        {
            return authenticatedUserOrError;
        }

        RockPaperScissors opponentChoice = EnumHelper.getRandomEnum( RockPaperScissors.class );

        String result = "Your choice: { " + yourChoice.toString() + " } versus Opponent's choice: { " + opponentChoice.toString() + " } | ";

        if (yourChoice == opponentChoice)
        {
            result += "It is a draw.";
        }
        else if (yourChoice.beats( opponentChoice ))
        {
            result += "You won!";
        }
        else
        {
            result += "You lost...";
        }

        return ServerApiResponse.generateResponseEntity(
                HttpStatus.OK,
                result + " Use this endpoint to play again."
                );
    }

    @Target( ElementType.METHOD )
    @Retention( RetentionPolicy.RUNTIME )
    @Operation(
        operationId = "2_2",
        summary = "Get the top users from the leaderboard.",
        description = "Returns a list of users sorted by their score in descending order. You can optionally limit the number of users returned using the `limit` query parameter."
    )
    @ApiResponses( value =
    {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content( mediaType = "" )
        )
    } )
    public @interface GetLeaderboardOperation {}

    public ResponseEntity<?> getLeaderboard( int limit )
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

        return ServerApiResponse.generateResponseEntity(
                HttpStatus.OK,
                Constants.DEFAULT_SUCCESS_MESSAGE,
                leaderboardUsers
                );
    }

    @Target( ElementType.METHOD )
    @Retention( RetentionPolicy.RUNTIME )
    @SecurityRequirement( name = "bearerAuth" )
    @Operation(
        operationId = "2_7",
        summary = "Claim bonus points once every 3 hours.",
        description = """
        You can claim +1 bonus point every 3 hours. There is a 50% chance to receive +2 points instead!
        
        Once claimed, the bonus point(s) will be added to your current score.
        
        After claiming, you must wait for 3 hours before you are eligible to claim the next bonus points.
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
            responseCode = "401",
            description = "Unauthorized — invalid or missing token",
            content = @Content( mediaType = "" )
        ),
        @ApiResponse(
            responseCode = "425",
            description = "Too Early — Bonus points already claimed",
            content = @Content( mediaType = "" )
        )
    } )
    public @interface ClaimBonusPointOperation {}

    protected ResponseEntity<?> claimBonusPoint()
    {
        ResponseEntity<?> authenticatedUserOrError = authenticationService.getAuthenticatedUserOrError();

        if (!( authenticatedUserOrError.getBody() instanceof User user ))
        {
            return authenticatedUserOrError;
        }

        int COOLDOWN_HOURS = 3;
        Duration COOLDOWN = Duration.ofHours( COOLDOWN_HOURS );

        Instant timeNow = Instant.now();
        Instant lastClaimTime = user.getLastBonusClaimTime();

        if (lastClaimTime != null)
        {
            Duration elapsed = Duration.between( lastClaimTime, timeNow );

            if (elapsed.compareTo( COOLDOWN ) < 0)
            {
                Duration remaining = COOLDOWN.minus( elapsed );
                long hours = remaining.toHours();
                long minutes = remaining.toMinutesPart();
                long seconds = remaining.toSecondsPart();

                String timeLeftMessage;
                if (hours > 0)
                {
                    timeLeftMessage = String.format(
                        "%d hour%s, %d minute%s, and %d second%s",
                        hours, hours == 1 ? "" : "s",
                        minutes, minutes == 1 ? "" : "s",
                        seconds, seconds == 1 ? "" : "s"
                    );
                }
                else if (minutes > 0)
                {
                    timeLeftMessage = String.format(
                        "%d minute%s and %d second%s",
                        minutes, minutes == 1 ? "" : "s",
                        seconds, seconds == 1 ? "" : "s"
                    );
                }
                else
                {
                    timeLeftMessage = String.format(
                        "%d second%s",
                        seconds, seconds == 1 ? "" : "s"
                    );
                }

                return ServerApiResponse.generateResponseEntity(
                        HttpStatus.TOO_EARLY,
                        "Bonus points already claimed. Please try again after " + timeLeftMessage + " to claim your next bonus points."
                        );
            }
        }

        int bonusPoints = ( NumberHelper.isHit( 0.5 ) ) ? 2 : 1;

        user.setScore( user.getScore() + bonusPoints );
        user.setClaimedBonusPoints( user.getClaimedBonusPoints() + bonusPoints );
        user.setLastBonusClaimTime( timeNow );
        userRepository.save( user );

        String result = ( bonusPoints == 2 )
                        ? "Bonus points claimed! You received +2 points!"
                        : "Bonus point claimed! You received +1 point.";

        return ServerApiResponse.generateResponseEntity(
                HttpStatus.OK,
                result + " Your current score is " + user.getScore() + ". Please come back after " + COOLDOWN_HOURS + " hours to claim your next bonus points.",
                new LeaderboardUserResponse( leaderboardService.getUserRank( user ), user )
                );
    }
}
