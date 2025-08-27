/*
 * ****************************************************************************
 * File: RegisterRequest.java
 * Author: Lim Leong Kee
 * Email: leongkee86@gmail.com
 * Last Modified Date: 25 August 2025
 * 
 * Description:
 * This class represents a request body in an API request for user
 * registration.
 * ****************************************************************************
 */

package com.demo.rest_api.dto;

import jakarta.validation.constraints.NotBlank;

public class RegisterRequest
{
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private String displayName;

    public String getUsername()
    {
        return this.username;
    }

    public String getPassword()
    {
        return this.password;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }
}
