package com.demo.rest_api;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestApiApplication
{
	public static void main( String[] args )
    {
        if (System.getenv( "SERVER_PORT" ) == null)
        {
            // Load variables from the .env file into system properties (only if not already set).
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

            // Set all dotenv variables as system properties.
            dotenv.entries().forEach( entry ->
                {
                    String key = entry.getKey();

                    if (System.getProperty( key ) == null)
                    {
                        System.setProperty( key, entry.getValue() );
                    }
                }
            );
        }

		SpringApplication.run( RestApiApplication.class, args );
	}
}
