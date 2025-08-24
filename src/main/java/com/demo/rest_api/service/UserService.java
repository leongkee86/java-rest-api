package com.demo.rest_api.service;

import com.demo.rest_api.model.User;
import com.demo.rest_api.repository.UserRepository;
import jakarta.annotation.Nonnull;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sample;

@Service
public class UserService implements UserDetailsService
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Optional<User> findByUsername( String username )
    {
        String regex = "^" + Pattern.quote( username ) + "$"; // Ignore case.
        return userRepository.findByUsernameRegex( regex );
    }

    public void save( User user )
    {
        if (!user.getIsPasswordEncoded())
        {
            user.setPassword( passwordEncoder.encode( user.getPassword() ) );
            user.setIsPasswordEncoded( true );
        }

        userRepository.save( user );
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

    public Optional<User> findRandomUserWithMinimumScore( int minimumScore, List<String> excludedUsernames )
    {
        Aggregation aggregation = Aggregation.newAggregation(
            match(
                Criteria.where( "score" ).gte( minimumScore ) // Only include MongoDB documents where the "score" field is 'greater than or equal to' (gte) the value of "minimumScore".
                .and( "username" ).nin( excludedUsernames ) // Exclude usernames in the list.
            ),
            sample( 1 )
        );

        AggregationResults<@NotNull User> results = mongoTemplate.aggregate( aggregation, "user", User.class );
        List<User> users = results.getMappedResults();

        // Creates a stream from the list. Then, .findFirst() returns an Optional<User> containing the first element
        // of the stream if present, or Optional.empty() if the list is empty.
        return users.stream().findFirst();
    }
}
