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
     * Converts a comma-separated string into a List<String>.
     * Trims whitespace from each item.
     *
     * @param input The comma-separated string.
     * @return A List of strings, or an empty list if input is null or empty.
     */
    public static List<String> csvToList( String input )
    {
        if (input == null || input.trim().isEmpty())
        {
            return List.of();
        }

        return Arrays.stream( input.split( "," ) )
                .map( String::trim )
                .filter( s -> !s.isEmpty() ) // Skip empty strings.
                .collect( Collectors.toList() );
    }
}
