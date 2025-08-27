/*
 * ****************************************************************************
 * File: UserRepository.java
 * Author: Lim Leong Kee
 * Email: leongkee86@gmail.com
 * Last Modified Date: 25 August 2025
 * 
 * Description:
 * This interface provides CRUD operations for the User entity, extending
 * MongoRepository to work with MongoDB.
 * ****************************************************************************
 */

package com.demo.rest_api.repository;

import com.demo.rest_api.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User,String>
{
    Optional<User> findByUsername( String username );

    @Query( "{ 'username' : { $regex: ?0, $options: 'i' } }" )
    Optional<User> findByUsernameRegex( String regex );

    boolean existsByUsername(String username);

    void deleteByUsername( String username );
}
