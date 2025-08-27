/*
 * ****************************************************************************
 * File: LoginRequest.java
 * Author: Lim Leong Kee
 * Email: leongkee86@gmail.com
 * Last Modified Date: 25 August 2025
 * 
 * Description:
 * This class represents the request body in an API request for user login.
 * ****************************************************************************
 */

package com.demo.rest_api.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest
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
