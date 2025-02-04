/*
 * Copyright (c) 2002-2024, City of Paris
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
import fr.paris.lutece.plugins.identityimport.service.BatchService;
import fr.paris.lutece.plugins.identityimport.service.ImportClientService;
import fr.paris.lutece.plugins.identityimport.service.ServiceContractService;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.AbstractIdentityStoreRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.BatchRequestValidator;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.BatchDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.contract.ServiceContractDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchImportRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchImportResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.Constants;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.ResponseStatusFactory;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;

import java.util.UUID;

public class IdentityBatchImportRequest extends AbstractIdentityStoreRequest
{
    protected BatchImportRequest _request;
    protected String strHeaderAppCode;

    public IdentityBatchImportRequest( final BatchImportRequest request, final String strHeaderAppCode, final String strClientCode,
            final String strAuthorName, final String strAuthorType ) throws IdentityStoreException
    {
        super( strClientCode, strAuthorName, strAuthorType );
        this._request = request;
        this.strHeaderAppCode = strHeaderAppCode;
    }

    @Override
    protected void validateSpecificRequest( ) throws IdentityStoreException
    {
        BatchRequestValidator.instance( ).checkImportRequest( _request );
    }

    @Override
    protected BatchImportResponse doSpecificRequest( ) throws IdentityStoreException
    {
        final BatchImportResponse response = new BatchImportResponse( );
        final Client client = ImportClientService.instance().getClient(strHeaderAppCode, _strClientCode);
        final ServiceContractDto activeServiceContract = ServiceContractService.instance( ).getActiveServiceContract( client.getClientCode() );
        if ( activeServiceContract == null )
        {
            response.setStatus( ResponseStatusFactory.unauthorized( ).setMessageKey( Constants.PROPERTY_REST_ERROR_SERVICE_CONTRACT_NOT_FOUND ) );
            response.getStatus( ).setMessage( "Service contract not found." );
            return response;
        }
        if ( !activeServiceContract.isAuthorizedImport( ) )
        {
            response.setStatus( ResponseStatusFactory.failure( ).setMessageKey( Constants.PROPERTY_REST_ERROR_IMPORT_UNAUTHORIZED ) );
            response.getStatus().setMessage("Import feature is not authorized in the client service contract.");
            return response;
        }

        try
        {
            final BatchDto batch = _request.getBatch( );
            batch.setAppCode( client.getAppCode() );
            batch.setClientCode( client.getClientCode() );
            batch.setReference( UUID.randomUUID( ).toString( ) );
            BatchService.instance( ).importBatch( batch, null, null );
            response.setReference( batch.getReference( ) );
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
