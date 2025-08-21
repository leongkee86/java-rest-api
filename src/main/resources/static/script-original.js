window.onload = function()
{
    let token = localStorage.getItem( "authToken" );
    let username = "";

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
                        const responseData = JSON.parse( response.data )?.data;

                        if (responseData)
                        {
                            token = responseData.token;

                            if (token)
                            {
                                isSuccessful = true;
                                ui.preauthorizeApiKey( "bearerAuth", token );
                                localStorage.setItem( "authToken", token );

                                updateUsernameDisplay( responseData.user );

                                setTimeout( () =>
                                {
                                    alert( "Login successful! You have been authorized and can access the protected API endpoints now." );
                                }
                                , 100 );
                            }
                        }
                    }
                    else if (responseUrl.pathname === logoutApiPath)
                    {
                        if (token)
                        {
                            isSuccessful = true;
                            ui.preauthorizeApiKey( "bearerAuth", "" );
                            localStorage.removeItem( "authToken" );

                            updateUsernameDisplay( null );

                            setTimeout( () =>
                            {
                                alert( "You have been successfully logged out. You are no longer authorized to access protected API endpoints. Please log in again to continue." );
                                location.reload();
                            }
                            , 100 );
                        }
                    }
                    else
                    {
                        const responseData = JSON.parse( response.data )?.data;

                        if (responseData)
                        {
                            const responseDataUser = responseData.user;

                            if (responseDataUser)
                            {
                                if (responseDataUser.username === username)
                                {
                                    updateUsernameDisplay( responseDataUser );
                                }
                            }
                            else if (responseData.username === username)
                            {
                                updateUsernameDisplay( responseData );
                            }
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
                        alert( "Logout failed. Please make sure you are logged in and try again." );
                    }
                }

                return response;
            },

            onComplete: () =>
            {
                if (token)
                {
                    if (navigator.onLine)
                    {
                        // fetch( "http://localhost:8080/api/game/profile",
                        fetch( "https://leongkee-java-rest-api.onrender.com/api/game/profile",
                            {
                                method: 'GET',
                                headers:
                                {
                                    'Authorization': 'Bearer ' + token
                                }
                            } )
                            .then( response =>
                                {
                                    if (response.ok)
                                    {
                                        return response.json();
                                    }
                                    else
                                    {
                                        throw new Error( 'HTTP error! Status: ' + response.status );
                                    }
                                }
                            )
                            .then( data =>
                                {
                                    ui.preauthorizeApiKey( "bearerAuth", token );

                                    const user = data.data;
                                    updateUsernameDisplay( user );

                                    setTimeout( () =>
                                    {
                                        alert( `Welcome back, ${user.username}! You have been automatically logged in, authorized, and can access the protected API endpoints now.` );
                                    }
                                    , 100 );
                                }
                            )
                            .catch( error =>
                                {
                                    localStorage.removeItem( "authToken" );
                                    updateUsernameDisplay( null );

                                    setTimeout( () =>
                                    {
                                        alert( "Something went wrong or your session has expired. Please log in again." );
                                    }
                                    , 100 );
                                }
                            );
                    }
                    else
                    {
                        alert( "You are currently offline. Auto-login will not work without an internet connection. Please check your internet connection and refresh this page to try again." );
                    }
                }

                const system = ui.getSystem();
                
                if (system.authActions && system.authActions.logout)
                {
                    const originalLogout = system.authActions.logout;

                    system.authActions.logout = ( ...args ) =>
                    {
                        localStorage.removeItem( "authToken" );
                        updateUsernameDisplay( null );
                        
                        setTimeout( () =>
                        {
                            alert( "You have been successfully logged out. You are no longer authorized to access protected API endpoints. Please log in again to continue." );
                            location.reload();
                        }
                        , 100 );

                        return originalLogout( ...args );
                    };
                }
            }
        }
    )

    function updateUsernameDisplay( user )
    {
        if (user)
        {
            username = user.username;
        }

        document.getElementById( 'user-info-display' ).innerHTML = ( user )
            ? `Welcome! You are currently logged in as <strong>"${user.username}"</strong>.<br>Your current score: <strong>${user.score}</strong>`
            : "You are not logged in yet. Please log in to continue.";
    }

    updateUsernameDisplay( null );

    // Watch for buttons added later (when operations are expanded).
    const observer = new MutationObserver( mutations =>
        {
            for (const mutation of mutations)
            {
                for (const node of mutation.addedNodes)
                {
                    if (node.nodeType === 1)
                    {
                        clickTryItOutButtons( node );
                    }
                }
            }
        }
    );

    observer.observe( document.getElementById( 'swagger-ui' ),
        {
            childList: true,
            subtree: true
        }
    );

    function clickTryItOutButtons( root = document )
    {
        const buttons = Array.from( root.querySelectorAll( 'button.try-out__btn' ) )
            .filter( btn => btn.textContent.trim() === 'Try it out');

        buttons.forEach( btn =>
            {
                btn.click();
            }
        );
    }
}

const scrollWrapper = document.querySelector( '.scroll-wrapper' );

window.addEventListener( 'scroll', () =>
    {
        if (window.scrollY > 600)
        {
            scrollWrapper.classList.add( 'hidden' );
        }
        else
        {
            scrollWrapper.classList.remove( 'hidden' );
        }
    }
);
