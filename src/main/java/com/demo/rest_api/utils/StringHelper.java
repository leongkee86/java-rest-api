/*
 * ****************************************************************************
 * File: StringHelper.java
 * Author: Lim Leong Kee
 * Email: leongkee86@gmail.com
 * Last Modified Date: 24 August 2025
 *
 * Description:
 * This class provides a set of helper methods for working with strings.
 * ****************************************************************************
 */

package com.demo.rest_api.utils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringHelper
{
    private static final String[] EMPTY_ARRAY = new String[ 0 ];

    /**
     * Checks whether the given string is {@code null} or exactly empty ("").
     * Whitespace-only strings are not considered empty.
     *
     * @param string The string to check.
     * @return {@code true} If the string is {@code null} or empty, {@code false} otherwise.
     */
    public static boolean isNullOrEmpty( String string )
    {
        return ( string == null || string.isEmpty() );
    }

    /**
     * Checks whether the given string is {@code null}, empty (""), or contains only whitespace.
     *
     * @param string The string to check.
     * @return {@code true} If the string is {@code null}, empty, or blank; {@code false} otherwise.
     */
    public static boolean isBlank( String string )
    {
        return ( string == null || string.isBlank() );
    }

    /**
     * Splits the given input string into an array of strings using the specified separator,
     * without trimming or filtering out empty elements.
     *
     * The separator can be treated either as a regular expression or as a literal string,
     * depending on the {@code isRegex} flag.
     *
     * @param input The input string to split. Returns an empty array if {@code null} or blank.
     * @param separator The separator string used to split the input. Treated as regex if
     *                  {@code isRegex} is {@code true}, otherwise treated as a literal string.
     * @param isRegex If {@code true}, {@code separator} is interpreted as a regex; if {@code false}, as a literal string.
     * @return An array of strings resulting from splitting the input, including empty strings if any.
     */
    public static String[] splitStringToArrayRaw( String input, String separator, boolean isRegex )
    {
        if (isBlank( input ))
        {
            return EMPTY_ARRAY;
        }

        return isRegex ? input.split( separator ) : input.split( Pattern.quote( separator ) );
    }

    /**
     * Converts a separator-separated string into a {@code String} array.
     * Trims whitespace from each item and skips empty strings.
     *
     * @param input The input string containing separated values.
     * @param separator The separator string (treated as regex or literal depending on {@code isRegex}).
     * @param isRegex If {@code true}, {@code separator} is treated as a regex; if {@code false}, as a literal string.
     * @return An array of strings, or an empty array if {@code input} is {@code null} or empty.
     */
    public static String[] splitStringToArray( String input, String separator, boolean isRegex )
    {
        String[] parts = splitStringToArrayRaw( input, separator, isRegex );

        if (parts.length == 0)
        {
            return EMPTY_ARRAY;
        }

        return Arrays.stream( parts )
                .map( String::trim )
                .filter( s -> !s.isEmpty() )
                .toArray( String[]::new );
    }

    /**
     * Converts a separator-separated string into a {@code List<String>}.
     * Trims whitespace from each item and skips empty strings.
     *
     * @param input The input string containing separated values.
     * @param separator The separator string (treated as regex or literal depending on {@code isRegex}).
     * @param isRegex If {@code true}, {@code separator} is treated as a regex; if {@code false}, as a literal string.
     * @return A {@code List<String>} containing the separated values, or an empty list if {@code input} is {@code null} or empty.
     */
    public static List<String> splitStringToList( String input, String separator, boolean isRegex )
    {
        String[] parts = splitStringToArrayRaw( input, separator, isRegex );

        if (parts.length == 0)
        {
            return List.of();
        }

        return Arrays.stream( parts )
                .map( String::trim )
                .filter( s -> !s.isEmpty() )
                .collect(Collectors.toList());
    }

    /**
     * Converts a separator-separated string into a {@code String} array.
     * Trims whitespace from each item and skips empty strings.
     * Treats the separator as a literal string.
     *
     * @param input The input string containing separated values.
     * @param separator The separator string, treated as a literal.
     * @return An array of {@code String}s, or an empty array if {@code input} is {@code null} or empty.
     */
    public static String[] splitStringToArray( String input, String separator )
    {
        return splitStringToArray( input, separator, false );
    }

    /**
     * Converts a separator-separated string into a {@code List<String>}.
     * Trims whitespace from each item and skips empty strings.
     * Treats the separator as a literal string by default.
     *
     * @param input The input string containing separated values.
     * @param separator The separator string treated as a literal.
     * @return A {@code List<String>}, or an empty list if {@code input} is {@code null} or empty.
     */
    public static List<String> splitStringToList( String input, String separator )
    {
        return splitStringToList( input, separator, false );
    }
}
