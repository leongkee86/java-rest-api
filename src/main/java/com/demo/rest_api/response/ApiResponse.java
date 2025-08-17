package com.demo.rest_api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude( JsonInclude.Include.NON_NULL )
@JsonPropertyOrder({ "status", "success", "message", "data", "metadata" })
public class ApiResponse<T>
{
    private int status;
    private boolean success;
    private String message;
    private T data;
    private Object metadata;

    public ApiResponse( int status, String message, T data, Object metadata )
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
}
