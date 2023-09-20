/*
 * Copyright (c) 2002-2023, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.identityimport.web.request;

import fr.paris.lutece.plugins.identityimport.business.Client;
import fr.paris.lutece.plugins.identityimport.business.ClientHome;
import fr.paris.lutece.plugins.identityimport.service.BatchService;
import fr.paris.lutece.plugins.identityimport.service.ServiceContractService;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.AbstractIdentityStoreRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.BatchDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.contract.ServiceContractDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchImportRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchImportResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.Constants;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.ResponseStatusFactory;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.UUID;

public class IdentityBatchImportRequest extends AbstractIdentityStoreRequest
{
    protected BatchImportRequest _request;
    protected String _strHeaderClientToken;

    public IdentityBatchImportRequest( final BatchImportRequest request, final String strHeaderClientToken, final String strClientCode,
            final String strAuthorName, final String strAuthorType ) throws IdentityStoreException
    {
        super( strClientCode, strAuthorName, strAuthorType );
        this._request = request;
        this._strHeaderClientToken = strHeaderClientToken;
    }

    @Override
    protected void validateSpecificRequest( ) throws IdentityStoreException
    {
    }

    @Override
    protected BatchImportResponse doSpecificRequest( ) throws IdentityStoreException
    {
        final BatchImportResponse response = new BatchImportResponse( );
        if ( StringUtils.isAllBlank( _strClientCode, _strHeaderClientToken ) )
        {
            response.setStatus( ResponseStatusFactory.badRequest( ).setMessage( "You must provide a client_code or a client_token." )
                    .setMessageKey( Constants.PROPERTY_REST_ERROR_MUST_PROVIDE_CLIENT_CODE_OR_TOKEN ) );
            return response;
        }
        final String clientAppCode;
        if ( StringUtils.isBlank( _strClientCode ) )
        {
            final Optional<Client> client = ClientHome.findByToken( _strHeaderClientToken );
            if ( client.isPresent( ) )
            {
                clientAppCode = client.get( ).getAppCode( );
            }
            else
            {
                response.setStatus( ResponseStatusFactory.notFound( ).setMessage( "No client found with provided token" )
                        .setMessageKey( Constants.PROPERTY_REST_ERROR_NO_CLIENT_FOUND_WITH_TOKEN ) );
                return response;
            }
        }
        else
        {
            clientAppCode = _strClientCode;
        }
        final ServiceContractDto activeServiceContract = ServiceContractService.instance( ).getActiveServiceContract( clientAppCode );
        if ( activeServiceContract == null )
        {
            response.setStatus( ResponseStatusFactory.notFound( ).setMessageKey( Constants.PROPERTY_REST_ERROR_SERVICE_CONTRACT_NOT_FOUND ) );
            return response;
        }
        if ( !activeServiceContract.isAuthorizedImport( ) )
        {
            response.setStatus( ResponseStatusFactory.unauthorized( ).setMessageKey( Constants.PROPERTY_REST_ERROR_IMPORT_UNAUTHORIZED ) );
            return response;
        }
        final BatchDto batch = _request.getBatch( );
        batch.setAppCode( clientAppCode );
        if ( StringUtils.isEmpty( batch.getReference( ) ) )
        {
            batch.setReference( UUID.randomUUID( ).toString( ) );
            response.setReference( batch.getReference( ) );
        }
        try
        {
            BatchService.instance( ).importBatch( batch, null, null );
            response.setStatus( ResponseStatusFactory.success( ).setMessageKey( Constants.PROPERTY_REST_INFO_SUCCESSFUL_OPERATION ) );
        }
        catch( final IdentityStoreException e )
        {
            response.setStatus(
                    ResponseStatusFactory.failure( ).setMessage( e.getMessage( ) ).setMessageKey( Constants.PROPERTY_REST_ERROR_DURING_TREATMENT ) );
        }

        return response;
    }
}
