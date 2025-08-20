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
            // Load .env variables into the system environment
            Dotenv dotenv = Dotenv.load();
            dotenv.entries().forEach( entry ->
                {
                    System.setProperty( entry.getKey(), entry.getValue() );
                }
            );
        }

		SpringApplication.run( RestApiApplication.class, args );
	}
}
