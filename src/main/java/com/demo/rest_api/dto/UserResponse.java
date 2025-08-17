package com.demo.rest_api.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect( fieldVisibility = JsonAutoDetect.Visibility.ANY )
public class UserResponse
{
    private String username;
    private int score;

    public UserResponse( String username, int score )
    {
        this.username = username;
        this.score = score;
    }
}
