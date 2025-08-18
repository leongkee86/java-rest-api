package com.demo.rest_api.model;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document( collection = "user" )
public class User
{
    @BsonId
    private ObjectId _id = null;

    private String username = "";
    private String password = "";
    private boolean isPasswordEncoded = false;
    private int score = 0;
    private int attempts = 0;
    private int rounds = 0;

    private boolean hasGuessNumberStarted = false;
    private int guessNumberCurrentRound = 0;
    private int guessNumberBasic = 0;
    private int guessNumberSecret = 0;
    private int guessNumberTrap = 0;

    public User( String username, String password )
    {
        this._id = new ObjectId();
        this.username = username;
        this.password = password;
        this.score = 0;
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

    public void updateRounds()
    {
        rounds = guessNumberCurrentRound;
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

    public boolean getHasGuessNumberStarted()
    {
        return hasGuessNumberStarted;
    }

    public void setHasGuessNumberStarted( boolean hasGuessNumberStarted )
    {
        this.hasGuessNumberStarted = hasGuessNumberStarted;
    }

    public int getGuessNumberCurrentRound()
    {
        return guessNumberCurrentRound;
    }

    public void setGuessNumberCurrentRound( int guessNumberCurrentRound )
    {
        this.guessNumberCurrentRound = guessNumberCurrentRound;
        updateRounds();
    }

    public int getGuessNumberBasic()
    {
        return guessNumberBasic;
    }

    public void setGuessNumberBasic( int guessNumberBasic )
    {
        this.guessNumberBasic = guessNumberBasic;
    }

    public int getGuessNumberSecret()
    {
        return guessNumberSecret;
    }

    public void setGuessNumberSecret( int guessNumberSecret )
    {
        this.guessNumberSecret = guessNumberSecret;
    }

    public int getGuessNumberTrap()
    {
        return guessNumberTrap;
    }

    public void setGuessNumberTrap( int guessNumberTrap )
    {
        this.guessNumberTrap = guessNumberTrap;
    }
}
