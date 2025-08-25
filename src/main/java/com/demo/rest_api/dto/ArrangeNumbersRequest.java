package com.demo.rest_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class ArrangeNumbersRequest
{
    @NotNull( message = "List cannot be null" )
    @Size( min = 5, max = 5, message = "You must provide exactly 5 numbers" )
    @Schema(
        example = "[ 1, 2, 3, 4, 5 ]",
        defaultValue = ""
    )
    private List<Integer> yourArrangedNumbers;

    public void setYourArrangedNumbers( List<Integer> yourArrangedNumbers )
    {
        this.yourArrangedNumbers = yourArrangedNumbers;
    }

    public List<Integer> getYourArrangedNumbers()
    {
        return yourArrangedNumbers;
    }
}
