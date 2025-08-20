window.onload = function()
{
    let token = null;

    const ui = SwaggerUIBundle(
        {
            url: "swagger.json",
            dom_id: '#swagger-ui',
            presets:
            [
                SwaggerUIBundle.presets.apis,
                SwaggerUIStandalonePreset
            ],
            layout: "BaseLayout",

            operationsSorter: ( a, b ) =>
            {
                const opIdA = a.get( "operation" )?.get( "operationId" ) || "";
                const opIdB = b.get( "operation" )?.get( "operationId" ) || "";
                return opIdA.localeCompare( opIdB );
            },

            requestInterceptor: function( request )
            {
                if (token)
                {
                    request.headers[ 'Authorization' ] = "Bearer " + token;
                }

                return request;
            },
            
            responseInterceptor: async ( response ) =>
            {
                if (!response || !response.url)
                {
                    return response;
                }

                const responseUrl = new URL( response.url );

                if (response.status === 200)
                {
                    if (responseUrl.pathname === "/api/auth/login")
                    {
                        let jsonData;

                        if (typeof response.data === "string")
                        {
                            try
                            {
                                jsonData = JSON.parse( response.data );
                            }
                            catch ( e )
                            {
                                alert( "Something went wrong." );
                                return response;
                            }
                        }
                        else
                        {
                            jsonData = response.data;
                        }
                        
                        token = jsonData?.data?.token;

                        if (token)
                        {
                            ui.preauthorizeApiKey( "bearerAuth", token );
                            alert( "Login successful! You are now authorized and can access protected endpoints." );
                        }
                        else
                        {
                            alert( "Login failed. Please try again." );
                        }
                    }
                    else if (responseUrl.pathname === "/api/auth/logout")
                    {
                        if (token)
                        {
                            ui.preauthorizeApiKey( "bearerAuth", "" );
                            alert( "Logout successful! Please log in again to access protected endpoints." );
                        }
                        else
                        {
                            alert( "Logout failed. Please try again." );
                        }

                        location.reload();
                    }
                }

                return response;
            }
        }
    )
}
