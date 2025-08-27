/*
 * ****************************************************************************
 * File: GameApiJsonController.java
 * Author: Lim Leong Kee
 * Email: leongkee86@gmail.com
 * Last Modified Date: 27 August 2025
 * 
 * Description:
 * This class provides REST API endpoints that use query and JSON parameters in
 * API requests for game-related API operations. It extends the
 * GameApiBaseController class to reuse core logic for API operations. It
 * integrates with Swagger UI using annotations to automatically generate API
 * documentation.
 * ****************************************************************************
 */

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
@Tag( name = Constants.GAME_API_JSON)
@Validated
public class GameApiJsonController extends GameApiBaseController
{
    @PostMapping(
        value = "/guessNumber",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @GuessNumberOperation
    public ResponseEntity<?> guessNumber( @RequestBody GuessNumberRequest request )
    {
        return super.processGuessingNumber( request.getYourGuessedNumber() );
    }

    @PostMapping(
        value = "/arrangeNumbers",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ArrangeNumbersOperation
    public ResponseEntity<?> arrangeNumbers( @RequestBody ArrangeNumbersRequest request )
    {
        return super.processArrangingNumbers( request.getYourArrangedNumbers() );
    }

    @PostMapping(
        value = "/rockPaperScissors/challenge",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PlayRockPaperScissorsOperation
    public ResponseEntity<?> playRockPaperScissors( @RequestBody PlayRockPaperScissorsRequest request )
    {
        return super.processPlayingRockPaperScissors( request.getOpponentUsername(), request.getYourChoice(), request.getPointsToStake() );
    }

    @PostMapping(
        value = "/rockPaperScissors/practise",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PractiseRockPaperScissorsOperation
    public ResponseEntity<?> practiseRockPaperScissors( @RequestBody PractiseRockPaperScissorsRequest request )
    {
        return super.processPractisingRockPaperScissors( request.getYourChoice() );
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
