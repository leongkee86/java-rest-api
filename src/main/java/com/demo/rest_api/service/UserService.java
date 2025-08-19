package com.demo.rest_api.service;

import com.demo.rest_api.model.User;
import com.demo.rest_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService
{
    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Optional<User> findByUsername( String username )
    {
        // Ignore case.
        String regex = "^" + Pattern.quote( username ) + "$";
        return userRepository.findByUsernameRegex( regex );
    }

    public User save( User user )
    {
        if (!user.getIsPasswordEncoded())
        {
            user.setPassword( passwordEncoder.encode( user.getPassword() ) );
            user.setIsPasswordEncoded( true );
        }

        return userRepository.save( user );
    }

    public boolean validatePassword( String rawPassword, String encodedPassword )
    {
        return passwordEncoder.matches( rawPassword, encodedPassword );
    }
}
