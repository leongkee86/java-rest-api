package com.demo.rest_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public class ArrangeNumberRequest
{
    @Schema(
        description = "An array of 5 integer values",
        example = "[ 1, 2, 3, 4, 5 ]"
    )
    @Size( min = 5, max = 5, message = "Array must contain exactly 5 numbers." )
    private int[] yourArrangedNumbers;

    public int[] getYourArrangedNumbers()
    {
        return this.yourArrangedNumbers;
    }
}
