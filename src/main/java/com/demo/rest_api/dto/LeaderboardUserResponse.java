package com.demo.rest_api.dto;

import com.demo.rest_api.model.User;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect( fieldVisibility = JsonAutoDetect.Visibility.ANY )
public class LeaderboardUserResponse
{
    private long rank;
    private String username;
    private int score;
    private int attempts = 0;
    private int rounds = 0;
    private float averageAttemptsPerRound = 0;

    public LeaderboardUserResponse( long rank, User user )
    {
        this.rank = rank;

        username = user.getUsername();
        score = user.getScore();
        attempts = user.getAttempts();
        rounds = user.getRounds();
        averageAttemptsPerRound = User.getAverageAttemptsPerRound( user );
    }
}
