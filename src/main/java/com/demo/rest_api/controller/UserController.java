package com.demo.rest_api.controller;

import com.demo.rest_api.model.User;
import com.demo.rest_api.response.ApiResponse;
import com.demo.rest_api.utils.Constants;
import com.demo.rest_api.utils.StringHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping( "/users" )
public class UserController
{
    private final List<User> users = new ArrayList<User>();

    @PostMapping( "/users" )
    public ResponseEntity<?> createUser(@RequestBody User user )
    {
        if (StringHelper.isNullOrEmpty( user.getName() ))
        {
            return ResponseEntity
                    .status( HttpStatus.BAD_REQUEST )
                    .body(
                        new ApiResponse<>(
                            HttpStatus.BAD_REQUEST.value(),
                            "name is required",
                            null,
                            null
                        )
                    );
        }
        else if (StringHelper.isNullOrEmpty( user.getGender() ))
        {
            return ResponseEntity
                    .status( HttpStatus.BAD_REQUEST )
                    .body(
                        new ApiResponse<>(
                            HttpStatus.BAD_REQUEST.value(),
                            "gender is required",
                            null,
                            null
                        )
                    );
        }

        users.add( user );
        return ResponseEntity
                .status( HttpStatus.CREATED )
                .body(
                    new ApiResponse<>(
                        HttpStatus.CREATED.value(),
                        Constants.DEFAULT_SUCCESS_MESSAGE,
                        user,
                        null
                    )
                );
    }

    @GetMapping
    public ResponseEntity<?> getUsers()
    {
        return ResponseEntity
                .status( HttpStatus.OK )
                .body(
                    new ApiResponse<>(
                        HttpStatus.OK.value(),
                        Constants.DEFAULT_SUCCESS_MESSAGE,
                        users,
                        null
                    )
                );
    }
}
