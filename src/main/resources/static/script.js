window.onload = function()
{
    let token = null;
    let isSorted = false;

    const customOpOrder =
    [
        "auth/register",
        "auth/login",
        "auth/logout",
        "game/profile",
        "game/leaderboard",
        "game/guessNumber",
        "game/arrangeNumbers",
        "game/rockPaperScissors/practise",
        "game/rockPaperScissors/challenge"
    ];
    
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
                if (isSorted)
                {
                    return;
                }

                isSorted = true;

                const opIdA = a.get( "operation" )?.get( "operationId" ) || "";
                const opIdB = b.get( "operation" )?.get( "operationId" ) || "";

                const indexA = customOpOrder.indexOf( opIdA );
                const indexB = customOpOrder.indexOf( opIdB );

                if (indexA === -1 && indexB === -1)
                {
                    // Fallback to alphabetical.
                    return opIdA.localeCompare( opIdB );
                }

                if (indexA === -1)
                {
                    return 1;
                }

                if (indexB === -1)
                {
                    return -1;
                }

                return indexA - indexB;
            },

            // Add Authorization header if token exists.
            requestInterceptor: function( request )
            {
                if (token)
                {
                    request.headers[ 'Authorization' ] = "Bearer " + token;
                }

                return request;
            },

            // Intercept response to capture token from Login API.
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
