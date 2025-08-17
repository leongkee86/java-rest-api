package com.demo.rest_api.repository;

import com.demo.rest_api.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User,String>
{
    Optional<User> findByUsername( String username );
}
