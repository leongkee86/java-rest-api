package com.demo.rest_api.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringHelper
{
    public static boolean isNullOrEmpty( String string )
    {
        return ( string == null || string.isEmpty() );
    }

    /**
     * Converts a separator-separated string into a String array.
     * Trims whitespace from each item and skips empty strings.
     *
     * @param input The input string containing separated values.
     * @param separator The separator regex used to split the input string.
     * @return An array of strings, or an empty array if input is null or empty.
     */
    public static String[] splitStringToArray( String input, String separator )
    {
        if (input == null || input.trim().isEmpty())
        {
            return new String[ 0 ];
        }

        return Arrays.stream( input.split( separator ) )
                .map( String::trim )
                .filter( s -> !s.isEmpty() ) // Skip empty strings.
                .toArray( String[]::new );
    }

    /**
     * Converts a separator-separated string into a List<String>.
     * Trims whitespace from each item and skips empty strings.
     *
     * @param input The input string containing separated values.
     * @param separator The separator regex used to split the input string.
     * @return A List of strings, or an empty list if input is null or empty.
     */
    public static List<String> splitStringToList( String input, String separator )
    {
        if (input == null || input.trim().isEmpty())
        {
            return List.of();
        }

        return Arrays.stream( input.split( separator ) )
                .map( String::trim )
                .filter( s -> !s.isEmpty() ) // Skip empty strings.
                .collect( Collectors.toList() );
    }
}
