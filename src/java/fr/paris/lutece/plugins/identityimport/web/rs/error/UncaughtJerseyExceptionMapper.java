package fr.paris.lutece.plugins.identityimport.web.rs.error;

import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.error.ErrorResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.Constants;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.ResponseStatusFactory;
import fr.paris.lutece.plugins.rest.service.mapper.GenericUncaughtJerseyExceptionMapper;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper designed to intercept uncaught {@link WebApplicationException}.<br/>
 */
@Provider
public class UncaughtJerseyExceptionMapper extends GenericUncaughtJerseyExceptionMapper<WebApplicationException, ErrorResponse>
{
    @Override
    protected ErrorResponse getBody( final WebApplicationException e )
    {
        final ErrorResponse response = new ErrorResponse( );
        response.setStatus(ResponseStatusFactory.fromHttpCode(e.getResponse().getStatus()).setMessage(e.getMessage())
                                                .setMessageKey(Constants.PROPERTY_REST_ERROR_API_COMMUNICATION));
        return response;
    }
}
