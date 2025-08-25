package com.demo.rest_api.controller;

import com.demo.rest_api.dto.ArrangeNumbersRequest;
import com.demo.rest_api.dto.GuessNumberRequest;
import com.demo.rest_api.dto.PlayRockPaperScissorsRequest;
import com.demo.rest_api.dto.PractiseRockPaperScissorsRequest;
import com.demo.rest_api.utils.Constants;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( "/api/game" )
@Tag( name = Constants.GAME_API )
@Validated
public class GameApiController extends GameApiBaseController
{
    @PostMapping( value = "/guessNumber", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE } )
    @GuessNumberOperation
    public ResponseEntity<?> guessNumber( @RequestBody GuessNumberRequest request )
    {
        return super.guessNumber( request.getYourGuessedNumber() );
    }

    @PostMapping( value = "/arrangeNumbers", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE } )
    @ArrangeNumbersOperation
    public ResponseEntity<?> arrangeNumbers( @RequestBody ArrangeNumbersRequest request )
    {
        return super.arrangeNumbers( request.getYourArrangedNumbers() );
    }

    @PostMapping( value = "/rockPaperScissors/challenge", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE } )
    @PlayRockPaperScissorsOperation
    public ResponseEntity<?> playRockPaperScissors( @RequestBody PlayRockPaperScissorsRequest request )
    {
        return super.playRockPaperScissors( request.getOpponentUsername(), request.getYourChoice(), request.getPointsToStake() );
    }

    @PostMapping( value = "/rockPaperScissors/practise", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE } )
    @PractiseRockPaperScissorsOperation
    public ResponseEntity<?> practiseRockPaperScissors( @RequestBody PractiseRockPaperScissorsRequest request )
    {
        return super.practiseRockPaperScissors( request.getYourChoice() );
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
