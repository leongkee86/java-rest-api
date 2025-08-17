package com.demo.rest_api.utils;

import java.util.concurrent.ThreadLocalRandom;

public class NumberHelper
{
    public static int getRandomNumber( int min, int max )
    {
        return ThreadLocalRandom.current().nextInt( min, max + 1 );
    }
}
