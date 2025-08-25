package com.demo.rest_api.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class GuessNumberRequest
{
    @Parameter( description = "Guess a number from 1 to 100", required = true )
    @Schema( example = "50", defaultValue = "" )
    @Min( 1 )
    @Max( 100 )
    private int yourGuessedNumber;

    public void setYourGuessedNumber( int yourGuessedNumber )
    {
        this.yourGuessedNumber = yourGuessedNumber;
    }

    public int getYourGuessedNumber()
    {
        return yourGuessedNumber;
    }
}
