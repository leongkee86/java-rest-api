package com.demo.rest_api.service;

import com.demo.rest_api.model.User;
import com.demo.rest_api.repository.UserRepository;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService implements UserDetailsService
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

    @Override
    public @Nonnull UserDetails loadUserByUsername( @Nonnull String username ) throws UsernameNotFoundException
    {
        return findByUsername( username )
                .map( user -> new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    Collections.emptyList() // No roles or authorities.
                ) )
                .orElseThrow( () -> new UsernameNotFoundException( "User not found: " + username ) );
    }
}
