/*
 * ****************************************************************************
 * File: LeaderboardUserResponse.java
 * Author: Lim Leong Kee
 * Email: leongkee86@gmail.com
 * Last Modified Date: 27 August 2025
 * 
 * Description:
 * This class represents a response containing user data for the leaderboard.
 * ****************************************************************************
 */

package com.demo.rest_api.dto;

import com.demo.rest_api.model.User;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonAutoDetect( fieldVisibility = JsonAutoDetect.Visibility.ANY )
@JsonPropertyOrder(
    {
        "rank", "username", "displayName",
        "score", "attempts", "rounds",
        "averageAttemptsPerRound", "claimedBonusPoints"
    }
)
public class LeaderboardUserResponse extends UserResponse
{
    private long rank;

    public LeaderboardUserResponse( long rank, User user )
    {
        super( user );
        this.rank = rank;
    }
}
