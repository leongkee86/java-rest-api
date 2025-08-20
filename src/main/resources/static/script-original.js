window.onload = function()
{
    let token = localStorage.getItem( "authToken" );
    let username = localStorage.getItem( "username" );

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

                let isSuccessful = false;
                const loginApiPath = "/api/auth/login";
                const logoutApiPath = "/api/auth/logout";

                const responseUrl = new URL( response.url );

                if (response.ok)
                {
                    if (responseUrl.pathname === loginApiPath)
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
                        
                        const jsonDataData = jsonData?.data;
                        token = jsonDataData?.token;

                        if (token)
                        {
                            username = jsonDataData?.user.username;

                            isSuccessful = true;
                            ui.preauthorizeApiKey( "bearerAuth", token );
                            localStorage.setItem( "authToken", token );
                            localStorage.setItem( "username", username );

                            updateUsernameDisplay( username );
                            alert( "Login successful! You have been authorized and can access the protected API endpoints now." );
                        }
                    }
                    else if (responseUrl.pathname === logoutApiPath)
                    {
                        if (token)
                        {
                            isSuccessful = true;
                            ui.preauthorizeApiKey( "bearerAuth", "" );
                            localStorage.removeItem( "authToken" );
                            localStorage.removeItem( "username" );

                            updateUsernameDisplay( null );
                            alert( "You have been successfully logged out. You are no longer authorized to access protected API endpoints. Please log in again to continue." );
                            location.reload();
                        }
                    }
                }

                if (!isSuccessful)
                {
                    if (responseUrl.pathname === loginApiPath)
                    {
                        alert( "Login failed. Please check your credentials and try again." );
                    }
                    else if (responseUrl.pathname === logoutApiPath)
                    {
                        alert( "Logout failed. Please try again." );
                    }
                }

                return response;
            },

            onComplete: () =>
            {
                if (token && username)
                {
                    ui.preauthorizeApiKey( "bearerAuth", token );
                    updateUsernameDisplay( username );
                    alert( "Welcome back! You have been automatically logged in, authorized, and can access the protected API endpoints now." );
                }
            }
        }
    )

    updateUsernameDisplay( username );

    function updateUsernameDisplay( username )
    {
        document.getElementById( 'username-display' ).textContent = ( username )
            ? `Welcome! You are currently logged in as "${username}".`
            : "You are not logged in yet. Please log in to continue.";
    }
}

const scrollWrapper = document.querySelector( '.scroll-wrapper' );

window.addEventListener( 'scroll', () =>
    {
        if (window.scrollY > 50)
        {
            scrollWrapper.classList.add( 'hidden' );
        }
        else
        {
            scrollWrapper.classList.remove( 'hidden' );
        }
    }
);
