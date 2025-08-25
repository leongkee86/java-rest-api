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
