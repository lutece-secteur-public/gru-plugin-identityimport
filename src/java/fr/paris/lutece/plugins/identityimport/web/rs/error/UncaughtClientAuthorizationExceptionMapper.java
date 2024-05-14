package fr.paris.lutece.plugins.identityimport.web.rs.error;

import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.ResponseDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.error.ErrorResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.Constants;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.ResponseStatusFactory;
import fr.paris.lutece.plugins.identitystore.web.exception.ClientAuthorizationException;
import fr.paris.lutece.plugins.rest.service.mapper.GenericUncaughtExceptionMapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper designed to intercept uncaught {@link ClientAuthorizationException}.<br/>
 */
@Provider
public class UncaughtClientAuthorizationExceptionMapper extends GenericUncaughtExceptionMapper<ClientAuthorizationException, ResponseDto>
{

    public static final String ERROR_CLIENT_AUTHORIZATION = "The client doesn't have the necessary authorizations to perform this request";

    @Override
    protected Response.Status getStatus(final ClientAuthorizationException e)
    {
        if ( e.getResponse( ) != null )
        {
            return Response.Status.fromStatusCode( e.getResponse( ).getStatus( ).getHttpCode( ) );
        }
        return Response.Status.FORBIDDEN;
    }

    @Override
    protected ResponseDto getBody( final ClientAuthorizationException e )
    {
        if ( e.getResponse( ) != null )
        {
            return e.getResponse( );
        }
        final ErrorResponse response = new ErrorResponse( );
        response.setStatus(ResponseStatusFactory.forbidden().setMessage(ERROR_CLIENT_AUTHORIZATION + " :: " + e.getMessage())
                                                .setMessageKey(Constants.PROPERTY_REST_ERROR_CLIENT_AUTHORIZATION));
        return response;
    }

    @Override
    protected String getType( )
    {
        return MediaType.APPLICATION_JSON;
    }
}
