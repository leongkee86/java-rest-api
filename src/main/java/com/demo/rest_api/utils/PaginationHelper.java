/*
 * ****************************************************************************
 * File: PaginationHelper.java
 * Author: Lim Leong Kee
 * Email: leongkee86@gmail.com
 * Last Modified Date: 26 August 2025
 *
 * Description:
 * This class provides a set of helper methods for handling pagination in
 * MongoDB queries.
 * ****************************************************************************
 */

package com.demo.rest_api.utils;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

public class PaginationHelper
{
    public record PaginationMetadata( int page, int limit, long totalItems, int totalPages ) {}

    /**
     * Applies pagination to the given query and returns pagination metadata.
     *
     * @param query      MongoDB query object to apply skip and limit.
     * @param page       1-based page number.
     * @param limit      Number of items per page.
     * @param totalItems Total number of matching documents.
     * @return PaginationMetadata
     */
    public static PaginationMetadata applyPagination( Query query, int page, int limit, long totalItems )
    {
        if (page < 1)
        {
            throw new IllegalArgumentException( "Page must be at least 1." );
        }

        if (limit < 1)
        {
            throw new IllegalArgumentException( "Limit must be at least 1." );
        }

        long skip = ( long )( page - 1 ) * limit;
        query.skip( skip ).limit( limit );

        int totalPages = ( totalItems == 0 ) ? 0 : ( int ) Math.ceil( ( double )totalItems / limit );

        return new PaginationMetadata( page, limit, totalItems, totalPages );
    }

    /**
     * Counts documents matching the given query, excluding pagination (skip & limit).
     *
     * @param mongoTemplate The MongoTemplate instance.
     * @param originalQuery The original query with filters (may include skip/limit).
     * @param entityClass   The entity class.
     * @return Total number of documents matching the filters.
     */
    public static long countWithoutPagination( MongoTemplate mongoTemplate, Query originalQuery, Class<?> entityClass )
    {
        // Create a count query with the same filters but without skip or limit.
        Query countQuery = Query.of( originalQuery ).limit( -1 ).skip( -1 );
        return mongoTemplate.count( countQuery, entityClass );
    }
}
