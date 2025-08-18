package com.demo.rest_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class GuessNumberRequest
{
    @Schema(type = "integer", format = "int32", description = "An integer value")
    private int yourGuessedNumber;

    public int getYourGuessedNumber()
    {
        return this.yourGuessedNumber;
    }
}
