package com.demo.rest_api.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document( collection = "user" )
public class User
{
    private String id = "";
    private String username = "";
    private String password = "";
    private boolean isPasswordEncoded = false;
    private int score = 0;
    private int attempts = 0;
    private int rounds = 0;

    private int currentNumber = 0;

    public User( String username, String password )
    {
        this.username = username;
        this.password = password;
        this.score = 0;
    }

    public String getId()
    {
        return this.id;
    }

    public String getUsername()
    {
        return this.username;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public String getPassword()
    {
        return this.password;
    }

    public void setIsPasswordEncoded( boolean isPasswordEncoded )
    {
        this.isPasswordEncoded = isPasswordEncoded;
    }

    public boolean getIsPasswordEncoded()
    {
        return this.isPasswordEncoded;
    }

    public void setScore( int score )
    {
        this.score = score;
    }

    public int getScore()
    {
        return this.score;
    }

    public int getAttempts()
    {
        return attempts;
    }

    public void setAttempts( int attempts )
    {
        this.attempts = attempts;
    }

    public int getRounds()
    {
        return rounds;
    }

    public void setRounds(int rounds)
    {
        this.rounds = rounds;
    }

    public static float getAverageAttemptsPerRound( User user )
    {
        int attempts = user.getAttempts();

        if (attempts == 0)
        {
            return 0;
        }

        return ( float )attempts / user.getRounds();
    }

    public int getCurrentNumber()
    {
        return currentNumber;
    }

    public void setCurrentNumber( int currentNumber )
    {
        this.currentNumber = currentNumber;
    }
}
