package com.demo.rest_api.utils;

import org.springframework.http.MediaType;

public final class Constants
{
    private Constants()
    {
        // private constructor to prevent instantiation
    }

    // Configuration
    public static final int USERNAME_LENGTH = 3;
    public static final int PASSWORD_LENGTH = 3;
    public static final int DISPLAY_NAME_LENGTH = 3;

    // Security
    public static final String AUTH_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    // Message
    public static final String DEFAULT_SUCCESS_MESSAGE = "Request processed successfully.";

    // Tag
    public static final String QUERY_PARAM_FOR_TESTING_ONLY = "Query Parameters (For testing only)";
    public static final String APPLICATION_JSON_OR_FORM_URLENCODED = "GET : Query | POST : " + MediaType.APPLICATION_JSON_VALUE + " OR " + MediaType.APPLICATION_FORM_URLENCODED_VALUE;
    public static final String AUTH_API_TEST = "Auth APIs — " + QUERY_PARAM_FOR_TESTING_ONLY;
    public static final String AUTH_API = "Auth APIs — " + APPLICATION_JSON_OR_FORM_URLENCODED;
    public static final String GAME_API_TEST = "Game APIs — " + QUERY_PARAM_FOR_TESTING_ONLY;
    public static final String GAME_API = "Game APIs — " + APPLICATION_JSON_OR_FORM_URLENCODED;
    public static final String USER_API_TEST = "User APIs — " + QUERY_PARAM_FOR_TESTING_ONLY;
    public static final String USER_API = "User APIs — " + APPLICATION_JSON_OR_FORM_URLENCODED;

    // Key
    public static final String DATABASE_USER_USERNAME_KEY = "username";
    public static final String DATABASE_USER_SCORE_KEY = "score";
    public static final String DATABASE_USER_ATTEMPTS_KEY = "attempts";
    public static final String DATABASE_USER_ROUNDS_KEY = "rounds";
}
