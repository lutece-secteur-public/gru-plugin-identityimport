package fr.paris.lutece.plugins.identityimport.web.rs.error;

import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.ResponseDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.error.ErrorResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.Constants;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.ResponseStatusFactory;
import fr.paris.lutece.plugins.identitystore.web.exception.DuplicatesConsistencyException;
import fr.paris.lutece.plugins.rest.service.mapper.GenericUncaughtExceptionMapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper designed to intercept uncaught {@link DuplicatesConsistencyException}.<br/>
 */
@Provider
public class UncaughtDuplicatesConsistencyExceptionMapper extends GenericUncaughtExceptionMapper<DuplicatesConsistencyException, ResponseDto>
{

    public static final String ERROR_DUPLICATES_CONSISTENCY = "The sent request goes against duplicates consistency rules";

    @Override
    protected Response.Status getStatus(final DuplicatesConsistencyException e)
    {
        if ( e.getResponse( ) != null )
        {
            return Response.Status.fromStatusCode( e.getResponse( ).getStatus( ).getHttpCode( ) );
        }
        return Response.Status.CONFLICT;
    }

    @Override
    protected ResponseDto getBody( final DuplicatesConsistencyException e )
    {
        if ( e.getResponse( ) != null )
        {
            return e.getResponse( );
        }
        final ErrorResponse response = new ErrorResponse( );
        response.setStatus(ResponseStatusFactory.conflict().setMessage(ERROR_DUPLICATES_CONSISTENCY + " :: " + e.getMessage())
                                                .setMessageKey(Constants.PROPERTY_REST_ERROR_DUPLICATES_CONSISTENCY));
        return response;
    }

    @Override
    protected String getType( )
    {
        return MediaType.APPLICATION_JSON;
    }
}
