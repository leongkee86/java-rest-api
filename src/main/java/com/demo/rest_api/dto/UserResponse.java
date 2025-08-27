/*
 * ****************************************************************************
 * File: UserResponse.java
 * Author: Lim Leong Kee
 * Email: leongkee86@gmail.com
 * Last Modified Date: 25 August 2025
 * 
 * Description:
 * This class is used to represent a response containing user data in a
 * simplified form for serialization purposes in APIs.
 * ****************************************************************************
 */

package com.demo.rest_api.dto;

import com.demo.rest_api.model.User;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect( fieldVisibility = JsonAutoDetect.Visibility.ANY )
public class UserResponse
{
    private String username;
    private String displayName;
    private int score;
    private int attempts = 0;
    private int rounds = 0;
    private float averageAttemptsPerRound = 0;
    private int claimedBonusPoints = 0;

    public UserResponse( User user )
    {
        username = user.getUsername();
        displayName = user.getDisplayName();
        score = user.getScore();
        attempts = user.getAttempts();
        rounds = user.getRounds();
        averageAttemptsPerRound = User.getAverageAttemptsPerRound( user );
        claimedBonusPoints = user.getClaimedBonusPoints();
    }
}
