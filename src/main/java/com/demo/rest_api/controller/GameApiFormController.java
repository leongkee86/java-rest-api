package com.demo.rest_api.controller;

import com.demo.rest_api.enums.RockPaperScissors;
import com.demo.rest_api.utils.Constants;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Size;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping( "/api/game" + Constants.API_PATH_SUFFIX_FOR_FORM_URLENCODED )
@Tag( name = Constants.GAME_API_FORM)
@Validated
public class GameApiFormController extends GameApiBaseController
{
    @PostMapping(
        value = "/guessNumber",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @GuessNumberOperation
    public ResponseEntity<?> guessNumber(
        @Parameter(
            description = "Guess a number from 1 to 100",
            required = true
        )
        @RequestParam( defaultValue = "50" ) int yourGuessedNumber
    )
    {
        return super.processGuessingNumber( yourGuessedNumber );
    }

    @PostMapping(
        value = "/arrangeNumbers",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
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
        return super.processArrangingNumbers( yourArrangedNumbers );
    }

    @PostMapping(
        value = "/rockPaperScissors/challenge",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
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
        return super.processPlayingRockPaperScissors( opponentUsername, yourChoice, pointsToStake );
    }

    @PostMapping(
        value = "/rockPaperScissors/practise",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PractiseRockPaperScissorsOperation
    public ResponseEntity<?> practiseRockPaperScissors(
        @Parameter(
            description = "Select your choice",
            required = true
        )
        @RequestParam( defaultValue = "Rock" ) RockPaperScissors yourChoice
    )
    {
        return super.processPractisingRockPaperScissors( yourChoice );
    }

    @GetMapping(
        value = "/leaderboard",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @GetLeaderboardOperation
    public ResponseEntity<?> getLeaderboard(
        @RequestParam( required = false ) Integer page,
        @RequestParam( required = false ) Integer limit
    )
    {
        return super.processGettingLeaderboard( page, limit );
    }

    @PostMapping(
        value = "/claimBonusPoints",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ClaimBonusPointOperation
    public ResponseEntity<?> claimBonusPoints()
    {
        return super.processClaimingBonusPoints();
    }
}
