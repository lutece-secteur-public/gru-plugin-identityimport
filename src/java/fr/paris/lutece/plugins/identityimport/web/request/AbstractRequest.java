package fr.paris.lutece.plugins.identityimport.web.request;

import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import fr.paris.lutece.portal.service.util.AppException;

public abstract class AbstractRequest {
    protected final String _strClientCode;

    protected AbstractRequest( String strClientCode )
    {
        _strClientCode = strClientCode;
    }

    /**
     * Valid the request according to parameter
     *
     * @throws AppException
     *             if request not valid
     * @throws IdentityStoreException
     */
    protected abstract void validRequest( ) throws IdentityStoreException;

    /**
     * Specific action for the request
     *
     * @return html/json string response
     * @throws AppException
     *             in case of request fail
     */
    protected abstract Object doSpecificRequest( ) throws IdentityStoreException;

    /**
     * Do the request, call the inner validRequest and doSpecificRequest
     *
     * @return html/json string response
     * @throws AppException
     *             in case of failure
     */
    public Object doRequest( ) throws IdentityStoreException
    {
        this.validRequest( );
        return doSpecificRequest( );
    }
}
