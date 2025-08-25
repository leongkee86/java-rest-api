package com.demo.rest_api.dto;

import jakarta.validation.constraints.NotBlank;

public class RegisterLoginRequest
{
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public String getUsername()
    {
        return this.username;
    }

    public String getPassword()
    {
        return this.password;
    }
}
