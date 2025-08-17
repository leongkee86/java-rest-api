package com.demo.rest_api.service;

import com.demo.rest_api.model.User;
import com.demo.rest_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService
{
    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Optional<User> findByUsername( String username )
    {
        return userRepository.findByUsername( username );
    }

    public User save( User user )
    {
        user.setPassword( passwordEncoder.encode( user.getPassword() ) );
        return userRepository.save( user );
    }

    public boolean validatePassword( String rawPassword, String encodedPassword )
    {
        return passwordEncoder.matches( rawPassword, encodedPassword );
    }
}
