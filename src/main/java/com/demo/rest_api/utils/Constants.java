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
    public static final String API_TAG_GET_QUERY = "GET : Query";
    public static final String API_TAG_GET_QUERY_PATH = "GET : Query, Path";
    public static final String API_TAG_FORM = MediaType.APPLICATION_FORM_URLENCODED_VALUE + " (Scroll down for JSON version)";
    public static final String API_TAG_JSON = MediaType.APPLICATION_JSON_VALUE;
    public static final String AUTH_API_FORM = "Auth APIs — POST : " + API_TAG_FORM;
    public static final String AUTH_API_JSON = "Auth APIs — POST : " + API_TAG_JSON;
    public static final String USER_API_FORM = "User APIs — " + API_TAG_GET_QUERY_PATH + " | PUT : " + API_TAG_FORM;
    public static final String USER_API_JSON = "User APIs — " + API_TAG_GET_QUERY_PATH + " | PUT : " + API_TAG_JSON;
    public static final String GAME_API_FORM = "Game APIs — " + API_TAG_GET_QUERY + " | POST : " + API_TAG_FORM;
    public static final String GAME_API_JSON = "Game APIs — " + API_TAG_GET_QUERY + " | POST : " + API_TAG_JSON;

    // Key
    public static final String DATABASE_USER_USERNAME_KEY = "username";
    public static final String DATABASE_USER_SCORE_KEY = "score";
    public static final String DATABASE_USER_ATTEMPTS_KEY = "attempts";
    public static final String DATABASE_USER_ROUNDS_KEY = "rounds";

    // URL
    public static final String API_PATH_SUFFIX_FOR_FORM_URLENCODED = "/form";
}
