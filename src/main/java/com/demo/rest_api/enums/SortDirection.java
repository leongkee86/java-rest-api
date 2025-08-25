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
