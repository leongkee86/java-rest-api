package com.demo.rest_api.service;

import com.demo.rest_api.model.User;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class LeaderboardService
{
    private final MongoTemplate mongoTemplate;

    public LeaderboardService( MongoTemplate mongoTemplate )
    {
        this.mongoTemplate = mongoTemplate;
    }

    public long getUserRank( User user)
    {
        Query query = new Query();

        Criteria betterRank = new Criteria().orOperator(
                Criteria.where( "score" ).gt( user.getScore() ),
                new Criteria().andOperator(
                        Criteria.where( "score" ).is( user.getScore() ),
                        Criteria.where( "attempts" ).lt( user.getAttempts() )
                ),
                new Criteria().andOperator(
                        Criteria.where( "score" ).is( user.getScore() ),
                        Criteria.where( "attempts" ).is( user.getAttempts() ),
                        Criteria.where( "rounds" ).lt( user.getRounds() )
                )
        );

        query.addCriteria( betterRank );

        return mongoTemplate.count( query, User.class ) + 1;
    }
}
