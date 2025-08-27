/*
 * ****************************************************************************
 * File: ChangePasswordRequest.java
 * Author: Lim Leong Kee
 * Email: leongkee86@gmail.com
 * Last Modified Date: 25 August 2025
 * 
 * Description:
 * This class represents the request body in an API request for changing the
 * user's password.
 * ****************************************************************************
 */

package com.demo.rest_api.dto;

import com.demo.rest_api.utils.Constants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangePasswordRequest
{
    @NotBlank( message = "Password is required." )
    @Size( min = Constants.PASSWORD_LENGTH, message = "Password must be at least " + Constants.PASSWORD_LENGTH + " characters long." )
    private String password;

    public void setPassword( String password )
    {
        this.password = password;
    }

    public String getPassword()
    {
        return password;
    }
}
