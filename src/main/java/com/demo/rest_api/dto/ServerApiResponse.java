/*
 * ****************************************************************************
 * File: ServerApiResponse.java
 * Author: Lim Leong Kee
 * Email: leongkee86@gmail.com
 * Last Modified Date: 23 August 2025
 * 
 * Description:
 * This class represents a standardized response structure for API responses.
 * ****************************************************************************
 */

package com.demo.rest_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@JsonInclude( JsonInclude.Include.NON_NULL )
@JsonPropertyOrder({ "status", "success", "message", "data", "metadata" })
public class ServerApiResponse<T>
{
    private int status;
    private boolean success;
    private String message;
    private T data;
    private Object metadata;

    public ServerApiResponse(int status, String message, T data, Object metadata )
    {
        this.setStatus( status );
        this.setMessage( message );
        this.setData( data );
        this.setMetadata( metadata );
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus( int status )
    {
        this.status = status;
        this.success = ( status >= 200 && status < 300 ); // auto-update success
    }

    public boolean isSuccess()
    {
        return success;
    }

    public void setSuccess( boolean success )
    {
        this.success = success;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

    public T getData()
    {
        return data;
    }

    public void setData( T data )
    {
        this.data = data;
    }

    public Object getMetadata()
    {
        return metadata;
    }

    public void setMetadata( Object metadata )
    {
        this.metadata = metadata;
    }

    public static ResponseEntity<?> generateResponseEntity( HttpStatus status )
    {
        return  generateResponseEntity( status, "" );
    }

    public static <T> ResponseEntity<?> generateResponseEntity( HttpStatus status, String message )
    {
        return  generateResponseEntity( status, message, null );
    }

    public static <T> ResponseEntity<?> generateResponseEntity( HttpStatus status, String message, T data )
    {
        return generateResponseEntity( status, message, data, null );
    }

    public static <T> ResponseEntity<?> generateResponseEntity( HttpStatus status, String message, T data, Object metadata )
    {
        return ResponseEntity
                .status( status )
                .body(
                    new ServerApiResponse<>(
                        status.value(),
                        message,
                        data,
                        metadata
                    )
                );
    }
}
