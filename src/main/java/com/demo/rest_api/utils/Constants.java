package com.demo.rest_api.utils;

public final class Constants
{
    private Constants()
    {
        // private constructor to prevent instantiation
    }

    // Security
    public static final String AUTH_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    // Message
    public static final String DEFAULT_SUCCESS_MESSAGE = "Request processed successfully.";
}
