package com.demo.rest_api.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document( collection = "user" )
public class User
{
    private String id;
    private String username;
    private String password;
    private int score;

    public User( String username, String password )
    {
        this.username = username;
        this.password = password;
        this.score = 0;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public String getId()
    {
        return this.id;
    }

    public String getUsername()
    {
        return this.username;
    }

    public String getPassword()
    {
        return this.password;
    }

    public int getScore()
    {
        return this.score;
    }
}
