/*
 * ****************************************************************************
 * File: RockPaperScissors.java
 * Author: Lim Leong Kee
 * Email: leongkee86@gmail.com
 * Last Modified Date: 25 August 2025
 * 
 * Description:
 * This enum defines the three possible choices in the Rock-Paper-Scissors
 * game, and provides a way to model the game logic and determine the outcome
 * between two selected choices.
 * ****************************************************************************
 */

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
