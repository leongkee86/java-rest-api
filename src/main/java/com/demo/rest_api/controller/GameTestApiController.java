package com.demo.rest_api.controller;

import com.demo.rest_api.enums.RockPaperScissors;
import com.demo.rest_api.utils.Constants;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping( "/api/test/game" )
@Tag( name = Constants.GAME_API_TEST )
@Validated
public class GameTestApiController extends GameApiBaseController
{
    @PostMapping( "/guessNumber" )
    @GuessNumberOperation
    public ResponseEntity<?> guessNumber(
        @Parameter(
            description = "Guess a number from 1 to 100",
            required = true
        )
        @RequestParam( defaultValue = "50" ) int yourGuessedNumber
    )
    {
        return super.guessNumber( yourGuessedNumber );
    }

    @PostMapping( "/arrangeNumbers" )
    @ArrangeNumbersOperation
    public ResponseEntity<?> arrangeNumbers(
        @Parameter(
            description = "Guess the sequence of the 5 numbers defined by the game for the current round",
            required = true
        )
        @RequestParam( defaultValue = "1,2,3,4,5" )
        @Size( min = 5, max = 5, message = "You must provide exactly 5 numbers" )
        List<Integer> yourArrangedNumbers
    )
    {
        return super.arrangeNumbers( yourArrangedNumbers );
    }

    @PostMapping( "/rockPaperScissors/challenge" )
    @PlayRockPaperScissorsOperation
    public ResponseEntity<?> playRockPaperScissors(
        @Parameter(
            description = "The username of the opponent that you choose to challenge"
        )
        @RequestParam( required = false ) String opponentUsername,
        @Parameter(
            description = "Select your choice",
            required = true
        )
        @RequestParam( defaultValue = "Rock" ) RockPaperScissors yourChoice,
        @Parameter(
            description = "The number of points that you want to stake",
            required = true
        )
        @RequestParam( defaultValue = "1" ) int pointsToStake
    )
    {
        return super.playRockPaperScissors( opponentUsername, yourChoice, pointsToStake );
    }

    @PostMapping( "/rockPaperScissors/practise" )
    @PractiseRockPaperScissorsOperation
    public ResponseEntity<?> practiseRockPaperScissors(
        @Parameter(
            description = "Select your choice",
            required = true
        )
        @RequestParam( defaultValue = "Rock" ) RockPaperScissors yourChoice
    )
    {
        return super.practiseRockPaperScissors( yourChoice );
    }

    @GetMapping( "/leaderboard" )
    @GetLeaderboardOperation
    public ResponseEntity<?> getLeaderboard( @RequestParam( defaultValue = "100" ) int limit )
    {
        return super.getLeaderboard( limit );
    }

    @PostMapping( "/claimBonusPoints" )
    @ClaimBonusPointOperation
    public ResponseEntity<?> claimBonusPoint()
    {
        return super.claimBonusPoint();
    }
}
