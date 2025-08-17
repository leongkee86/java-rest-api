package com.demo.rest_api.model;

public class User
{
    private String name = "";
    private String gender = "";
    private String message = "";

    public User( String name, String gender, String message )
    {
        this.name = name;
        this.gender = gender;
        this.message = message;
    }

    public String getName()
    {
        return name;
    }

    public String getGender()
    {
        return gender;
    }

    public String getMessage()
    {
        return message;
    }
}
