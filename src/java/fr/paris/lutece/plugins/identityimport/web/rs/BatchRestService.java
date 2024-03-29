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
package fr.paris.lutece.plugins.identityimport.web.rs;

import fr.paris.lutece.plugins.identityimport.web.request.IdentityBatchImportRequest;
import fr.paris.lutece.plugins.identityimport.web.request.IdentityBatchStatusRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchImportRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchImportResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchStatusRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchStatusResponse;
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
    public Response importBatch( final BatchImportRequest request, @HeaderParam( Constants.PARAM_CLIENT_CODE ) String strHeaderClientCode,
            @HeaderParam( Constants.PARAM_AUTHOR_NAME ) String authorName, @HeaderParam( Constants.PARAM_AUTHOR_TYPE ) String authorType,
            @HeaderParam( Constants.PARAM_CLIENT_TOKEN ) String strHeaderClientToken ) throws IdentityStoreException
    {
        final IdentityBatchImportRequest identityBatchImportRequest = new IdentityBatchImportRequest( request, strHeaderClientToken, strHeaderClientCode,
                authorName, authorType );
        final BatchImportResponse response = (BatchImportResponse) identityBatchImportRequest.doRequest( );
        return Response.status( response.getStatus( ).getHttpCode( ) ).entity( response ).type( MediaType.APPLICATION_JSON_TYPE ).build( );
    }

    /**
     * Get the status of the batch
     *
     * @param request
     *            the request containing the reference of the batch and the desired mode
     * @return a {@link BatchStatusResponse}
     */
    @POST
    @Path( Constants.BATCH_STATUS_PATH )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response getBacthStatus( final BatchStatusRequest request, @HeaderParam( Constants.PARAM_CLIENT_CODE ) String strHeaderClientCode,
            @HeaderParam( Constants.PARAM_AUTHOR_NAME ) String authorName, @HeaderParam( Constants.PARAM_AUTHOR_TYPE ) String authorType,
            @HeaderParam( Constants.PARAM_CLIENT_TOKEN ) String strHeaderClientToken ) throws IdentityStoreException
    {
        final IdentityBatchStatusRequest identityBatchStatusRequest = new IdentityBatchStatusRequest( request, strHeaderClientToken, strHeaderClientCode,
                authorName, authorType );
        final BatchStatusResponse response = (BatchStatusResponse) identityBatchStatusRequest.doRequest( );
        return Response.status( response.getStatus( ).getHttpCode( ) ).entity( response ).type( MediaType.APPLICATION_JSON_TYPE ).build( );
    }

}
