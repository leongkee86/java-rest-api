package com.demo.rest_api.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect( fieldVisibility = JsonAutoDetect.Visibility.ANY )
public class LeaderboardUserResponse
{
    private int rank;
    private String username;
    private int score;

    public LeaderboardUserResponse( int rank, String username, int score )
    {
        this.rank = rank;
        this.username = username;
        this.score = score;
    }
}
