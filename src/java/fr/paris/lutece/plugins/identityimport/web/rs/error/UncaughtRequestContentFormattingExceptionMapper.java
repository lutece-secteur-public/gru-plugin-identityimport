package fr.paris.lutece.plugins.identityimport.web.rs.error;

import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.ResponseDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.error.ErrorResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.Constants;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.ResponseStatusFactory;
import fr.paris.lutece.plugins.identitystore.web.exception.RequestContentFormattingException;
import fr.paris.lutece.plugins.rest.service.mapper.GenericUncaughtExceptionMapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper designed to intercept uncaught {@link RequestContentFormattingException}.<br/>
 */
@Provider
public class UncaughtRequestContentFormattingExceptionMapper extends GenericUncaughtExceptionMapper<RequestContentFormattingException, ResponseDto>
{

    public static final String ERROR_REQUEST_CONTENT_BAD_FORMAT = "The sent request content is not properly formatted";

    @Override
    protected Response.Status getStatus(final RequestContentFormattingException e)
    {
        if ( e.getResponse( ) != null )
        {
            return Response.Status.fromStatusCode( e.getResponse( ).getStatus( ).getHttpCode( ) );
        }
        return Response.Status.CONFLICT;
    }

    @Override
    protected ResponseDto getBody( final RequestContentFormattingException e )
    {
        if ( e.getResponse( ) != null )
        {
            return e.getResponse( );
        }
        final ErrorResponse response = new ErrorResponse( );
        response.setStatus(ResponseStatusFactory.conflict().setMessage(ERROR_REQUEST_CONTENT_BAD_FORMAT + " :: " + e.getMessage())
                                                .setMessageKey(Constants.PROPERTY_REST_ERROR_REQUEST_CONTENT_BAD_FORMAT));
        return response;
    }

    @Override
    protected String getType( )
    {
        return MediaType.APPLICATION_JSON;
    }
}
