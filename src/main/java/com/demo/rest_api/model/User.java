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

    private boolean hasArrangeNumbersStarted = false;
    private int arrangeNumbersCurrentRound = 0;
    private int[] arrangedNumbers = null;

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

    public void setAttempts( int attempts )
    {
        this.attempts = attempts;
    }

    public int getAttempts()
    {
        return attempts;
    }

    public void updateRounds()
    {
        rounds = guessNumberCurrentRound + arrangeNumbersCurrentRound;
    }

    public int getRounds()
    {
        return rounds;
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

    public void setHasGuessNumberStarted( boolean hasGuessNumberStarted )
    {
        this.hasGuessNumberStarted = hasGuessNumberStarted;
    }

    public boolean getHasGuessNumberStarted()
    {
        return hasGuessNumberStarted;
    }

    public void setGuessNumberCurrentRound( int guessNumberCurrentRound )
    {
        this.guessNumberCurrentRound = guessNumberCurrentRound;
        updateRounds();
    }

    public int getGuessNumberCurrentRound()
    {
        return guessNumberCurrentRound;
    }

    public void setGuessNumberBasic( int guessNumberBasic )
    {
        this.guessNumberBasic = guessNumberBasic;
    }

    public int getGuessNumberBasic()
    {
        return guessNumberBasic;
    }

    public void setGuessNumberSecret( int guessNumberSecret )
    {
        this.guessNumberSecret = guessNumberSecret;
    }

    public int getGuessNumberSecret()
    {
        return guessNumberSecret;
    }

    public void setGuessNumberTrap( int guessNumberTrap )
    {
        this.guessNumberTrap = guessNumberTrap;
    }

    public int getGuessNumberTrap()
    {
        return guessNumberTrap;
    }

    public void setHasArrangeNumbersStarted( boolean hasArrangeNumbersStarted )
    {
        this.hasArrangeNumbersStarted = hasArrangeNumbersStarted;
    }

    public boolean getHasArrangeNumbersStarted()
    {
        return hasArrangeNumbersStarted;
    }

    public void setArrangeNumbersCurrentRound( int arrangeNumbersCurrentRound )
    {
        this.arrangeNumbersCurrentRound = arrangeNumbersCurrentRound;
        updateRounds();
    }

    public int getArrangeNumbersCurrentRound()
    {
        return arrangeNumbersCurrentRound;
    }

    public void setArrangedNumbers( int[] arrangedNumbers )
    {
        this.arrangedNumbers = arrangedNumbers;
    }

    public int[] getArrangedNumbers()
    {
        return arrangedNumbers;
    }
}
