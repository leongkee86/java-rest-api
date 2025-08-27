/*
 * ****************************************************************************
 * File: PractiseRockPaperScissorsRequest.java
 * Author: Lim Leong Kee
 * Email: leongkee86@gmail.com
 * Last Modified Date: 25 August 2025
 * 
 * Description:
 * This class represents a request body in an API request for a practice
 * session of the Rock-Paper-Scissors game.
 * ****************************************************************************
 */

package com.demo.rest_api.dto;

import com.demo.rest_api.enums.RockPaperScissors;
import jakarta.validation.constraints.NotNull;

public class PractiseRockPaperScissorsRequest
{
    @NotNull( message = "Your choice is required" )
    private RockPaperScissors yourChoice = RockPaperScissors.Rock;

    public void setYourChoice( RockPaperScissors yourChoice )
    {
        this.yourChoice = yourChoice;
    }

    public RockPaperScissors getYourChoice()
    {
        return yourChoice;
    }
}
