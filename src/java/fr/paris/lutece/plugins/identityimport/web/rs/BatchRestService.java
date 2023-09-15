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
package fr.paris.lutece.plugins.identityimport.web.rs;

import fr.paris.lutece.plugins.identityimport.business.Client;
import fr.paris.lutece.plugins.identityimport.business.ClientHome;
import fr.paris.lutece.plugins.identityimport.web.request.IdentityBatchImportRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.ResponseStatus;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchImportRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchImportResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.Constants;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import fr.paris.lutece.plugins.rest.service.RestConstants;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

/**
 * BatchRest
 */
@Path( RestConstants.BASE_PATH + Constants.PLUGIN_PATH + Constants.VERSION_PATH_V3 + Constants.BATCH_PATH )
public class BatchRestService
{
    /**
     * Create Batch
     * 
     * @param request
     * @return
     */
    @POST
    @Path( StringUtils.EMPTY )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response importBatch( final BatchImportRequest request, @HeaderParam( Constants.PARAM_CLIENT_CODE ) String strHeaderClientAppCode,
            @HeaderParam( Constants.PARAM_CLIENT_TOKEN ) String strHeaderClientToken ) throws IdentityStoreException
    {
        BatchImportResponse response = new BatchImportResponse( );
        if ( StringUtils.isAllBlank( strHeaderClientAppCode, strHeaderClientToken ) )
        {
            response.setStatus( ResponseStatus.badRequest( ).setMessage( "You must provide a client_code or a client_token." )
                    .setMessageKey( Constants.PROPERTY_REST_ERROR_MUST_PROVIDE_CLIENT_CODE_OR_TOKEN ) );
            return Response.status( response.getStatus( ).getCode( ) ).entity( response ).type( MediaType.APPLICATION_JSON_TYPE ).build( );
        }
        final String clientAppCode;
        if ( StringUtils.isBlank( strHeaderClientAppCode ) )
        {
            final Optional<Client> client = ClientHome.findByToken( strHeaderClientToken );
            if ( client.isPresent( ) )
            {
                clientAppCode = client.get( ).getAppCode( );
            }
            else
            {
                response.setStatus( ResponseStatus.notFound( ).setMessage( "No client found with provided token" )
                        .setMessageKey( Constants.PROPERTY_REST_ERROR_NO_CLIENT_FOUND_WITH_TOKEN ) );
                return Response.status( response.getStatus( ).getCode( ) ).entity( response ).type( MediaType.APPLICATION_JSON_TYPE ).build( );
            }
        }
        else
        {
            clientAppCode = strHeaderClientAppCode;
        }
        final IdentityBatchImportRequest identityBatchImportRequest = new IdentityBatchImportRequest( request, clientAppCode );
        response = (BatchImportResponse) identityBatchImportRequest.doRequest( );
        return Response.status( response.getStatus( ).getCode( ) ).entity( response ).type( MediaType.APPLICATION_JSON_TYPE ).build( );
    }

}
