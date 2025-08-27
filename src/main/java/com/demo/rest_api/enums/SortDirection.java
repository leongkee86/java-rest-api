/*
 * ****************************************************************************
 * File: SortDirection.java
 * Author: Lim Leong Kee
 * Email: leongkee86@gmail.com
 * Last Modified Date: 25 August 2025
 * 
 * Description:
 * This enum defines and manages sorting directions.
 * ****************************************************************************
 */

package com.demo.rest_api.enums;

import org.springframework.data.domain.Sort;

public enum SortDirection
{
    Ascending,
    Descending;

    public Sort.Direction toSpringSort()
    {
        return ( this == Ascending) ? Sort.Direction.ASC : Sort.Direction.DESC;
    }
}
