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

import fr.paris.lutece.plugins.identityimport.service.BatchService;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.BatchRequestValidator;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.BatchDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.ResponseStatusType;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchImportRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchImportResponse;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

public class IdentityBatchImportRequest extends AbstractRequest
{
    protected BatchImportRequest _request;

    public IdentityBatchImportRequest( final BatchImportRequest request, final String strClientCode )
    {
        super( strClientCode );
        this._request = request;
    }

    @Override
    protected void validRequest( ) throws IdentityStoreException
    {
        BatchRequestValidator.instance( ).checkClientApplication( _strClientCode );
        BatchRequestValidator.instance( ).checkOrigin( _request.getOrigin( ) );
    }

    @Override
    protected BatchImportResponse doSpecificRequest( ) throws IdentityStoreException
    {
        final BatchImportResponse response = new BatchImportResponse( );
        final BatchDto batch = _request.getBatch( );
        batch.setAppCode(_strClientCode);
        if ( StringUtils.isEmpty( batch.getReference( ) ) )
        {
            batch.setReference( UUID.randomUUID( ).toString( ) );
            response.setReference( batch.getReference( ) );
        }
        try
        {
            BatchService.instance( ).importBatch( batch, null, null );
            response.setStatus( ResponseStatusType.SUCCESS );
        }
        catch( final IdentityStoreException e )
        {
            response.setStatus( ResponseStatusType.FAILURE );
            response.setMessage(e.getMessage());
        }

        return response;
    }
}
