package com.demo.rest_api.repository;

import com.demo.rest_api.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User,String>
{
    // Add custom queries if needed.
}
