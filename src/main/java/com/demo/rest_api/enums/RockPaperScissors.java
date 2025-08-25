package com.demo.rest_api.enums;

public enum RockPaperScissors
{
    Rock,
    Paper,
    Scissors;

    public boolean beats( RockPaperScissors other )
    {
        return ( this == Rock && other == Scissors )
                || ( this == Scissors && other == Paper )
                || ( this == Paper && other == Rock );
    }
}
