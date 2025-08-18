package com.demo.rest_api.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class NumberHelper
{
    public static int getRandomNumber( int min, int max )
    {
        return ThreadLocalRandom.current().nextInt( min, max + 1 );
    }

    /**
     * Generate an array of distinct random numbers within [min, max] inclusive.
     * @param min minimum value (inclusive)
     * @param max maximum value (inclusive)
     * @param count how many distinct numbers to generate
     * @return int[] of distinct random numbers
     * @throws IllegalArgumentException if count > (max - min + 1)
     */
    public static int[] generateDistinctRandomNumbersInRange( int min, int max, int count )
    {
        if (min > max)
        {
            throw new IllegalArgumentException( "min should be less than or equal to max." );
        }

        int rangeSize = max - min + 1;
        if (count > rangeSize)
        {
            throw new IllegalArgumentException( "Count cannot be greater than the size of the range." );
        }

        List<Integer> numbers = new ArrayList<>();
        for (int i = min; i <= max; i++)
        {
            numbers.add( i );
        }

        Collections.shuffle( numbers );

        int[] result = new int[ count ];
        for (int i = 0; i < count; i++)
        {
            result[ i ] = numbers.get( i );
        }

        return result;
    }
}
