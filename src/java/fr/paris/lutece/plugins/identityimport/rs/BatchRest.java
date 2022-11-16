/*
 * Copyright (c) 2002-2022, City of Paris
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

package fr.paris.lutece.plugins.identityimport.rs;

import fr.paris.lutece.plugins.identityimport.business.Batch;
import fr.paris.lutece.plugins.identityimport.business.BatchHome;
import fr.paris.lutece.plugins.rest.service.RestConstants;
import fr.paris.lutece.util.json.ErrorJsonResponse;
import fr.paris.lutece.util.json.JsonResponse;
import fr.paris.lutece.util.json.JsonUtil;

import org.apache.commons.lang3.StringUtils;
import fr.paris.lutece.portal.service.util.AppLogService;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * BatchRest
 */
@Path( RestConstants.BASE_PATH + Constants.API_PATH + Constants.VERSION_PATH + Constants.BATCH_PATH )
public class BatchRest
{
    private static final int VERSION_1 = 1;
    
    /**
     * Get Batch List
     * @param nVersion the API version
     * @return the Batch List
     */
    @GET
    @Path( StringUtils.EMPTY )
    @Produces( MediaType.APPLICATION_JSON )
    public Response getBatchList( @PathParam( Constants.VERSION ) Integer nVersion )
    {
        if ( nVersion == VERSION_1 )
        {
            return getBatchListV1( );
        }
        AppLogService.error( Constants.ERROR_NOT_FOUND_VERSION );
        return Response.status( Response.Status.NOT_FOUND )
                .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), Constants.ERROR_NOT_FOUND_VERSION ) ) )
                .build( );
    }
    
    /**
     * Get Batch List V1
     * @return the Batch List for the version 1
     */
    private Response getBatchListV1( )
    {
        List<Batch> listBatchs = BatchHome.getBatchsList( );
        
        if ( listBatchs.isEmpty( ) )
        {
            return Response.status( Response.Status.NO_CONTENT )
                .entity( JsonUtil.buildJsonResponse( new JsonResponse( Constants.EMPTY_OBJECT ) ) )
                .build( );
        }
        return Response.status( Response.Status.OK )
                .entity( JsonUtil.buildJsonResponse( new JsonResponse( listBatchs ) ) )
                .build( );
    }
    
    /**
     * Create Batch
     * @param nVersion the API version
     * @param date the date
     * @param user the user
     * @param app_code the app_code
     * @param comment the comment
     * @return the Batch if created
     */
    @POST
    @Path( StringUtils.EMPTY )
    @Produces( MediaType.APPLICATION_JSON )
    public Response createBatch(
    @FormParam( Constants.BATCH_ATTRIBUTE_DATE ) String date,
    @FormParam( Constants.BATCH_ATTRIBUTE_USER ) String user,
    @FormParam( Constants.BATCH_ATTRIBUTE_APP_CODE ) String app_code,
    @FormParam( Constants.BATCH_ATTRIBUTE_COMMENT ) String comment,
    @PathParam( Constants.VERSION ) Integer nVersion )
    {
		if ( nVersion == VERSION_1 )
		{
		    return createBatchV1( date, user, app_code, comment );
		}
        AppLogService.error( Constants.ERROR_NOT_FOUND_VERSION );
        return Response.status( Response.Status.NOT_FOUND )
                .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), Constants.ERROR_NOT_FOUND_VERSION ) ) )
                .build( );
    }
    
    /**
     * Create Batch V1
     * @param date the date
     * @param user the user
     * @param app_code the app_code
     * @param comment the comment
     * @return the Batch if created for the version 1
     */
    private Response createBatchV1( String date, String user, String app_code, String comment )
    {
        if ( StringUtils.isEmpty( date ) || StringUtils.isEmpty( user ) || StringUtils.isEmpty( app_code ) || StringUtils.isEmpty( comment ) )
        {
            AppLogService.error( Constants.ERROR_BAD_REQUEST_EMPTY_PARAMETER );
            return Response.status( Response.Status.BAD_REQUEST )
                    .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.BAD_REQUEST.name( ), Constants.ERROR_BAD_REQUEST_EMPTY_PARAMETER ) ) )
                    .build( );
        }
        
        Batch batch = new Batch( );
	    batch.setDate( Date.valueOf( date ) );
    	batch.setUser( user );
    	batch.setAppCode( app_code );
    	batch.setComment( comment );
        BatchHome.create( batch );
        
        return Response.status( Response.Status.OK )
                .entity( JsonUtil.buildJsonResponse( new JsonResponse( batch ) ) )
                .build( );
    }
    
    /**
     * Modify Batch
     * @param nVersion the API version
     * @param id the id
     * @param date the date
     * @param user the user
     * @param app_code the app_code
     * @param comment the comment
     * @return the Batch if modified
     */
    @PUT
    @Path( Constants.ID_PATH )
    @Produces( MediaType.APPLICATION_JSON )
    public Response modifyBatch(
    @PathParam( Constants.ID ) Integer id,
    @FormParam( Constants.BATCH_ATTRIBUTE_DATE ) String date,
    @FormParam( Constants.BATCH_ATTRIBUTE_USER ) String user,
    @FormParam( Constants.BATCH_ATTRIBUTE_APP_CODE ) String app_code,
    @FormParam( Constants.BATCH_ATTRIBUTE_COMMENT ) String comment,
    @PathParam( Constants.VERSION ) Integer nVersion )
    {
        if ( nVersion == VERSION_1 )
        {
            return modifyBatchV1( id, date, user, app_code, comment );
        }
        AppLogService.error( Constants.ERROR_NOT_FOUND_VERSION );
        return Response.status( Response.Status.NOT_FOUND )
                .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), Constants.ERROR_NOT_FOUND_VERSION ) ) )
                .build( );
    }
    
    /**
     * Modify Batch V1
     * @param id the id
     * @param date the date
     * @param user the user
     * @param app_code the app_code
     * @param comment the comment
     * @return the Batch if modified for the version 1
     */
    private Response modifyBatchV1( Integer id, String date, String user, String app_code, String comment )
    {
        if ( StringUtils.isEmpty( date ) || StringUtils.isEmpty( user ) || StringUtils.isEmpty( app_code ) || StringUtils.isEmpty( comment ) )
        {
            AppLogService.error( Constants.ERROR_BAD_REQUEST_EMPTY_PARAMETER );
            return Response.status( Response.Status.BAD_REQUEST )
                    .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.BAD_REQUEST.name( ), Constants.ERROR_BAD_REQUEST_EMPTY_PARAMETER ) ) )
                    .build( );
        }
        
        Optional<Batch> optBatch = BatchHome.findByPrimaryKey( id );
        if ( !optBatch.isPresent( ) )
        {
            AppLogService.error( Constants.ERROR_NOT_FOUND_RESOURCE );
            return Response.status( Response.Status.NOT_FOUND )
                    .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), Constants.ERROR_NOT_FOUND_RESOURCE ) ) )
                    .build( );
        }
        else
        {
        	Batch batch = optBatch.get( );
		    batch.setDate( Date.valueOf( date ) );
	    	batch.setUser( user );
	    	batch.setAppCode( app_code );
	    	batch.setComment( comment );
	        BatchHome.update( batch );
	        
	        return Response.status( Response.Status.OK )
	                .entity( JsonUtil.buildJsonResponse( new JsonResponse( batch ) ) )
	                .build( );
        }
    }
    
    /**
     * Delete Batch
     * @param nVersion the API version
     * @param id the id
     * @return the Batch List if deleted
     */
    @DELETE
    @Path( Constants.ID_PATH )
    @Produces( MediaType.APPLICATION_JSON )
    public Response deleteBatch(
    @PathParam( Constants.VERSION ) Integer nVersion,
    @PathParam( Constants.ID ) Integer id )
    {
        if ( nVersion == VERSION_1 )
        {
            return deleteBatchV1( id );
        }
        AppLogService.error( Constants.ERROR_NOT_FOUND_VERSION );
        return Response.status( Response.Status.NOT_FOUND )
                .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), Constants.ERROR_NOT_FOUND_VERSION ) ) )
                .build( );
    }
    
    /**
     * Delete Batch V1
     * @param id the id
     * @return the Batch List if deleted for the version 1
     */
    private Response deleteBatchV1( Integer id )
    {
        Optional<Batch> optBatch = BatchHome.findByPrimaryKey( id );
        if ( !optBatch.isPresent( ) )
        {
            AppLogService.error( Constants.ERROR_NOT_FOUND_RESOURCE );
            return Response.status( Response.Status.NOT_FOUND )
                    .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), Constants.ERROR_NOT_FOUND_RESOURCE ) ) )
                    .build( );
        }
        
        BatchHome.remove( id );
        
        return Response.status( Response.Status.OK )
                .entity( JsonUtil.buildJsonResponse( new JsonResponse( Constants.EMPTY_OBJECT ) ) )
                .build( );
    }
    
    /**
     * Get Batch
     * @param nVersion the API version
     * @param id the id
     * @return the Batch
     */
    @GET
    @Path( Constants.ID_PATH )
    @Produces( MediaType.APPLICATION_JSON )
    public Response getBatch(
    @PathParam( Constants.VERSION ) Integer nVersion,
    @PathParam( Constants.ID ) Integer id )
    {
        if ( nVersion == VERSION_1 )
        {
            return getBatchV1( id );
        }
        AppLogService.error( Constants.ERROR_NOT_FOUND_VERSION );
        return Response.status( Response.Status.NOT_FOUND )
                .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), Constants.ERROR_NOT_FOUND_VERSION ) ) )
                .build( );
    }
    
    /**
     * Get Batch V1
     * @param id the id
     * @return the Batch for the version 1
     */
    private Response getBatchV1( Integer id )
    {
        Optional<Batch> optBatch = BatchHome.findByPrimaryKey( id );
        if ( !optBatch.isPresent( ) )
        {
            AppLogService.error( Constants.ERROR_NOT_FOUND_RESOURCE );
            return Response.status( Response.Status.NOT_FOUND )
                    .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), Constants.ERROR_NOT_FOUND_RESOURCE ) ) )
                    .build( );
        }
        
        return Response.status( Response.Status.OK )
                .entity( JsonUtil.buildJsonResponse( new JsonResponse( optBatch.get( ) ) ) )
                .build( );
    }
}