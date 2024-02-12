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
package fr.paris.lutece.plugins.identityimport.service;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.identityimport.business.Batch;
import fr.paris.lutece.plugins.identityimport.business.BatchHome;
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentity;
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentityAttribute;
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentityAttributeHome;
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentityHistory;
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentityHistoryHome;
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentityHome;
import fr.paris.lutece.plugins.identityimport.business.ResourceState;
import fr.paris.lutece.plugins.identityimport.wf.WorkflowBean;
import fr.paris.lutece.plugins.identityimport.wf.WorkflowBeanService;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.AttributeDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.BatchDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.IdentityDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchResourceStateDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchStatisticsDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchStatusDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchStatusMode;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchStatusResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.ImportingHistoryDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.Constants;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.ResponseStatusFactory;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.portal.service.progressmanager.ProgressManagerService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.sql.TransactionManager;
import org.apache.commons.lang3.StringUtils;

import java.sql.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class BatchService
{
    private static final String BATCH_WFBEANSERVICE = "identityimport.batch.wfbeanservice";
    private static final String CANDIDATEIDENTITY_WFBEANSERVICE = "identityimport.candidateidentity.wfbeanservice";

    private static final int ARCHIVE_ACTION_ID = AppPropertiesService.getPropertyInt( "identityimport.archive.action.id", 0 );
    private static final int VALIDATE_BATCH_ACTION_ID = AppPropertiesService.getPropertyInt( "identityimport.validate.action.id", 0 );
    private final WorkflowBeanService<CandidateIdentity> _wfIdentityBeanService = SpringContextService.getBean( CANDIDATEIDENTITY_WFBEANSERVICE );
    private final WorkflowBeanService<Batch> _wfBatchBeanService = SpringContextService.getBean( BATCH_WFBEANSERVICE );
    private final ProgressManagerService progressManagerService = ProgressManagerService.getInstance( );
    private final BatchValidationService validationService = BatchValidationService.instance( );

    private static BatchService instance;

    public static BatchService instance( )
    {
        if ( instance == null )
        {
            instance = new BatchService( );
        }
        return instance;
    }

    public int importBatch( final BatchDto batch, final User user, final String feedToken ) throws IdentityStoreException
    {
        // Ensure that provided batch size does not exceed the limit defined in properties
        validationService.validateImportBatchLimit( batch );

        TransactionManager.beginTransaction( null );

        try
        {
            // Validate the batch definition and compliance to client service contract
            if ( StringUtils.isNotEmpty( feedToken ) )
            {
                progressManagerService.initFeed( feedToken, batch.getIdentities( ).size( ) );
                progressManagerService.addReport( feedToken, "Validating batch ..." );
            }
            validationService.validateBatch( batch );

            // Try to retrieve the batch by its reference, if exists ensure that both side information is consistent, if not, create it.
            if ( StringUtils.isNotEmpty( feedToken ) )
            {
                progressManagerService.addReport( feedToken, "Creating batch..." );
            }

            if ( BatchHome.getBatch( batch.getReference( ) ) != null )
            {
                throw new IdentityStoreException( "A batch already exists with this reference." );
            }
            final Batch bean = this.getBean( batch );
            bean.setDate( new Date( System.currentTimeMillis( ) ) );
            BatchHome.create( bean );

            // Init workflow resource
            final int batchId = bean.getId( );
            final WorkflowBean<Batch> batchWorkflowBean = _wfBatchBeanService.createWorkflowBean( bean, batchId, user );

            if ( StringUtils.isNotEmpty( feedToken ) )
            {
                progressManagerService.addReport( feedToken, "Batch created with id " + batchId );
            }

            // Import identities
            final String appCode = bean.getAppCode( );
            if ( StringUtils.isNotEmpty( feedToken ) )
            {
                progressManagerService.addReport( feedToken, "Creating candidate identities..." );
            }
            for ( final IdentityDto identity : batch.getIdentities( ) )
            {
                final CandidateIdentity candidateIdentity = new CandidateIdentity( );
                candidateIdentity.setIdBatch( batchId );
                candidateIdentity.setExternalCustomerId( identity.getExternalCustomerId( ) );
                candidateIdentity.setConnectionId( identity.getConnectionId( ) );
                candidateIdentity.setClientAppCode( appCode );
                CandidateIdentityHome.create( candidateIdentity );
                _wfIdentityBeanService.createWorkflowBean( candidateIdentity, candidateIdentity.getId( ), candidateIdentity.getIdBatch( ), user );

                for ( final AttributeDto importedAttribute : identity.getAttributes( ) )
                {
                    final CandidateIdentityAttribute candidateAttribute = new CandidateIdentityAttribute( );
                    candidateAttribute.setIdIdentity( candidateIdentity.getId( ) );
                    candidateAttribute.setCode( importedAttribute.getKey( ) );
                    candidateAttribute.setValue( importedAttribute.getValue( ) );
                    candidateAttribute.setCertDate( new Date( importedAttribute.getCertificationDate( ).getTime( ) ) );
                    candidateAttribute.setCertProcess( importedAttribute.getCertifier( ) );
                    CandidateIdentityAttributeHome.create( candidateAttribute );
                }
                if ( StringUtils.isNotEmpty( feedToken ) )
                {
                    progressManagerService.incrementSuccess( feedToken, 1 );
                }
            }
            if ( StringUtils.isNotEmpty( feedToken ) )
            {
                progressManagerService.addReport( feedToken, "Created" + batch.getIdentities( ).size( ) + " candidate identities" );
            }
            TransactionManager.commitTransaction( null );
            _wfBatchBeanService.processActionNoUser( batchWorkflowBean, VALIDATE_BATCH_ACTION_ID, null, Locale.getDefault( ) );
            return bean.getId( );
        }
        catch( final Exception e )
        {
            TransactionManager.rollBack( null );
            if ( StringUtils.isNotEmpty( feedToken ) )
            {
                progressManagerService.addReport( feedToken, "An error occurred during creation..." );
            }
            throw new IdentityStoreException( e.getMessage( ), e );
        }
    }

    /**
     * Find expired batches (limited to batchLimit param) and purge them. Deletion of {@link CandidateIdentity}, {@link CandidateIdentityAttribute},
     * {@link CandidateIdentityHistory}
     * 
     * @param batchLimit
     * @return
     */
    public StringBuilder purgeBatches( final int batchLimit ) throws IdentityStoreException
    {
        final StringBuilder msg = new StringBuilder( );
        final List<Batch> expiredBatches = BatchHome.findExpiredBatches( batchLimit );
        msg.append( expiredBatches.size( ) ).append( " expired batches found" ).append( System.lineSeparator( ) );

        if ( ARCHIVE_ACTION_ID > 0 )
        {
            for ( final Batch expiredBatch : expiredBatches )
            {
                msg.append( "Removing expired batch : " ).append( expiredBatch.toLog( ) ).append( System.lineSeparator( ) );
                final WorkflowBean<Batch> workflowBean = _wfBatchBeanService.createWorkflowBean( expiredBatch, expiredBatch.getId( ), null );
                _wfBatchBeanService.processAutomaticAction( workflowBean, ARCHIVE_ACTION_ID, null, Locale.getDefault( ) );
            }
        }
        else
        {
            AppLogService.error( "Property identityimport.archive.action.id must be defined in order to perform archiving task." );
        }

        // return message for daemons
        return msg;
    }

    /**
     * Purge given batch. Deletion of {@link CandidateIdentity}, {@link CandidateIdentityAttribute}, {@link CandidateIdentityHistory}
     *
     * @param batchId
     *            the id of the batch
     * @return
     */
    public void purgeBatch( final int batchId ) throws IdentityStoreException
    {
        final Optional<Batch> expiredBatch = BatchHome.findByPrimaryKey( batchId );
        if ( expiredBatch.isPresent( ) )
        {
            final Batch batch = expiredBatch.get( );
            TransactionManager.beginTransaction( null );

            try
            {
                final List<Integer> idCandidateIdentitiesList = CandidateIdentityHome.getIdCandidateIdentitiesList( batch.getId( ) );
                CandidateIdentityHome.delete( idCandidateIdentitiesList );
                CandidateIdentityAttributeHome.delete( idCandidateIdentitiesList );
                CandidateIdentityHistoryHome.delete( idCandidateIdentitiesList );
                TransactionManager.commitTransaction( null );
            }
            catch( final Exception e )
            {
                TransactionManager.rollBack( null );
                throw new IdentityStoreException( "An error occurred during batch purge.", e );
            }
        }
        else
        {
            throw new IdentityStoreException( "Could not find batch with ID " + batchId );
        }
    }

    /**
     * Find newly created batches (limited to batchLimit param) and launch them automatically.
     *
     * @param batchLimit
     *            the limit
     * @return logs
     */
    public StringBuilder launchBatches( final int batchLimit )
    {
        final StringBuilder msg = new StringBuilder( );
        try
        {
            final List<Batch> initialStateBatches = BatchHome.findInitialStateBatches( batchLimit );
            if ( !initialStateBatches.isEmpty( ) )
            {
                msg.append( initialStateBatches.size( ) ).append( " initial state batches found" ).append( System.lineSeparator( ) );
                final int actionId = BatchHome.getBatchInitialActionId( );
                for ( final Batch batch : initialStateBatches )
                {
                    msg.append( "Launching batch : " ).append( batch.toLog( ) ).append( System.lineSeparator( ) );
                    final WorkflowBean<Batch> workflowBean = _wfBatchBeanService.createWorkflowBean( batch, batch.getId( ), null );
                    _wfBatchBeanService.processAutomaticAction( workflowBean, actionId, null, Locale.getDefault( ) );
                }
            }
            else
            {
                msg.append( "No initial state batches found." ).append( System.lineSeparator( ) );
            }
        }
        catch( final Exception e )
        {
            msg.append( "Error occurred while launching batches automatically :: " ).append( System.lineSeparator( ) ).append( e.getMessage( ) )
                    .append( System.lineSeparator( ) );
        }
        return msg;
    }

    /**
     * Find batches that can be closed, and close them
     * 
     * @param batchLimit
     *            the limit
     * @return logs
     */
    public StringBuilder closeBatches( int batchLimit )
    {
        final StringBuilder msg = new StringBuilder( );
        try
        {
            final List<Batch> closableBatches = BatchHome.findClosableBatches( batchLimit );
            if ( !closableBatches.isEmpty( ) )
            {
                msg.append( closableBatches.size( ) ).append( " in treatment state batches found" ).append( System.lineSeparator( ) );
                final int actionId = BatchHome.getBatchInTreatmentActionId( );
                for ( final Batch batch : closableBatches )
                {
                    msg.append( "Closing batch : " ).append( batch.toLog( ) ).append( System.lineSeparator( ) );
                    final WorkflowBean<Batch> workflowBean = _wfBatchBeanService.createWorkflowBean( batch, batch.getId( ), null );
                    _wfBatchBeanService.processAutomaticAction( workflowBean, actionId, null, Locale.getDefault( ) );
                }
            }
            else
            {
                msg.append( "No in treatment state batches found." ).append( System.lineSeparator( ) );
            }
        }
        catch( final Exception e )
        {
            msg.append( "Error occurred while closing batches automatically :: " ).append( System.lineSeparator( ) ).append( e.getMessage( ) )
                    .append( System.lineSeparator( ) );
        }
        return msg;
    }

    public Batch getBean( BatchDto batch )
    {
        final Batch bean = new Batch( );
        bean.setReference( batch.getReference( ) );
        bean.setComment( batch.getComment( ) );
        bean.setDate( batch.getDate( ) );
        bean.setUser( batch.getUser( ) );
        bean.setAppCode( batch.getAppCode( ) );
        return bean;
    }

    public BatchDto getDto( Batch batch )
    {
        final BatchDto dto = new BatchDto( );
        dto.setReference( batch.getReference( ) );
        dto.setComment( batch.getComment( ) );
        dto.setDate( batch.getDate( ) );
        dto.setUser( batch.getUser( ) );
        dto.setAppCode( batch.getAppCode( ) );
        return dto;
    }

    public BatchStatusResponse getBatchStatus( final String strBatchReference, final BatchStatusMode mode ) throws IdentityStoreException
    {
        final BatchStatusResponse response = new BatchStatusResponse( );
        final Batch batch = BatchHome.getBatch( strBatchReference );
        if ( batch == null )
        {
            response.setStatus(
                    ResponseStatusFactory.notFound( ).setMessageKey( Constants.PROPERTY_REST_ERROR_BATCH_NOT_FOUND ).setMessage( "Batch not found." ) );
            return response;
        }
        final ResourceState batchState = BatchHome.getBatchState( batch.getId( ) );
        if ( batchState == null )
        {
            response.setStatus( ResponseStatusFactory.notFound( ).setMessageKey( Constants.PROPERTY_REST_ERROR_BATCH_STATE_NOT_FOUND )
                    .setMessage( "State of batch not found." ) );
            return response;
        }

        final BatchStatusDto batchStatus = new BatchStatusDto( );

        batchStatus.setReference( batch.getReference( ) );
        batchStatus.setClientCode( batch.getAppCode( ) );
        batchStatus.setUser( batch.getUser( ) );
        batchStatus.setComment( batch.getComment( ) );
        batchStatus.setCreationDate( batch.getDate( ) );
        batchStatus.setStatus( batchState.getName( ) );
        batchStatus.setStatusDescription( batchState.getDescription( ) );

        final BatchStatisticsDto statisticsDto = new BatchStatisticsDto( );
        batchStatus.setStatistics( statisticsDto );
        final List<ResourceState> candidateIdentityStates = CandidateIdentityHome.getCandidateIdentityStates( batch.getId( ) );
        candidateIdentityStates.stream( ).filter( r -> r.getResourceCount( ) > 0 ).forEach( resourceState -> {
            final BatchResourceStateDto resourceStateDto = new BatchResourceStateDto( );
            statisticsDto.getResourceStates( ).add( resourceStateDto );
            resourceStateDto.setName( resourceState.getName( ) );
            resourceStateDto.setDescription( resourceState.getDescription( ) );
            resourceStateDto.setInitialState( resourceStateDto.isInitialState( ) );
            resourceStateDto.setResourceCount( resourceState.getResourceCount( ) );
        } );

        statisticsDto.setTotalResourceCount( candidateIdentityStates.stream( ).mapToInt( ResourceState::getResourceCount ).sum( ) );

        if ( mode != BatchStatusMode.IDENTITIES_ONLY )
        {
            final List<ResourceHistory> batchHistory = BatchHome.getBatchHistory( batch.getId( ) );
            for ( final ResourceHistory history : batchHistory )
            {
                final ImportingHistoryDto historyDto = new ImportingHistoryDto( );
                historyDto.setActionName( history.getAction( ).getName( ) );
                historyDto.setActionDescription( history.getAction( ).getDescription( ) );
                historyDto.setDate( history.getCreationDate( ) );
                historyDto.setUserAccessCode( history.getUserAccessCode( ) );
                batchStatus.getBatchHistory( ).add( historyDto );
            }
        }
        if ( mode != BatchStatusMode.BATCH_ONLY )
        {
            final List<CandidateIdentity> identities = CandidateIdentityHome
                    .getCandidateIdentitiesListByIds( CandidateIdentityHome.getIdCandidateIdentitiesList( batch.getId( ) ) );
            identities.forEach( candidate -> {
                final List<CandidateIdentityHistory> identityHistoryList = CandidateIdentityHistoryHome.selectAll( candidate.getId( ) );
                final List<ResourceHistory> history = CandidateIdentityHome.getHistory( candidate.getId( ) );
                batchStatus.getIdentities( ).add( CandidateIdentityService.instance( ).getDto( candidate, identityHistoryList, history ) );
            } );
        }

        response.setBatchStatus( batchStatus );
        response.setStatus( ResponseStatusFactory.ok( ).setMessageKey( Constants.PROPERTY_REST_INFO_SUCCESSFUL_OPERATION )
                .setMessage( "Status du batch récupéré avec succès" ) );
        return response;
    }
}
