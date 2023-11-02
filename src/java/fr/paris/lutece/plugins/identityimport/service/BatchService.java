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
package fr.paris.lutece.plugins.identityimport.service;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.identityimport.business.Batch;
import fr.paris.lutece.plugins.identityimport.business.BatchHome;
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentity;
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentityAttribute;
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentityAttributeHome;
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentityHistoryHome;
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentityHome;
import fr.paris.lutece.plugins.identityimport.wf.WorkflowBean;
import fr.paris.lutece.plugins.identityimport.wf.WorkflowBeanService;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.AttributeDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.BatchDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.IdentityDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchStatusDto;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import fr.paris.lutece.portal.service.progressmanager.ProgressManagerService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.sql.TransactionManager;
import org.apache.commons.lang3.StringUtils;

import java.sql.Date;
import java.util.List;
import java.util.Objects;

public class BatchService
{
    private static final String BATCH_WFBEANSERVICE = "identityimport.batch.wfbeanservice";
    private static final String CANDIDATEIDENTITY_WFBEANSERVICE = "identityimport.candidateidentity.wfbeanservice";
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
            Batch bean = BatchHome.getBatch( batch.getReference( ) );
            if ( bean == null )
            {
                bean = this.getBean( batch );
                BatchHome.create( bean );
            }
            else
                if ( !Objects.equals( bean.getAppCode( ), batch.getAppCode( ) ) )
                {
                    throw new IdentityStoreException( "The provided client code " + batch.getAppCode( ) + " does not match the client code "
                            + bean.getAppCode( ) + " stored for given reference " + batch.getReference( ) );
                }
                else
                { // TODO voir comment gérer les états proprement
                    final WorkflowBean<Batch> workflowBean = _wfBatchBeanService.createWorkflowBean( bean, bean.getId( ), user );
                    if ( workflowBean.getState( ).getId( ) == 3 || workflowBean.getState( ).getId( ) == 10 )
                    {
                        throw new IdentityStoreException(
                                "Cannot process batch " + batch.getReference( ) + " in state " + workflowBean.getState( ).getName( ) );
                    }
                }

            // Init workflow resource
            final int batchId = bean.getId( );
            _wfBatchBeanService.createWorkflowBean( bean, batchId, user );

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
                candidateIdentity.setCustomerId( identity.getCustomerId( ) );
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
    public StringBuilder purgeBatches( final int batchLimit )
    {
        final StringBuilder msg = new StringBuilder( );
        TransactionManager.beginTransaction( null );

        try
        {

            final List<Batch> expiredBatches = BatchHome.findExpiredBatches( batchLimit );
            msg.append( expiredBatches.size( ) ).append( " expired batches found" ).append( System.lineSeparator( ) );
            for ( final Batch expiredBatch : expiredBatches )
            {
                msg.append( "Removing expired batch : " + expiredBatch.toLog( ) ).append( System.lineSeparator( ) );
                final List<Integer> idCandidateIdentitiesList = CandidateIdentityHome.getIdCandidateIdentitiesList( expiredBatch.getId( ) );
                CandidateIdentityHome.delete( idCandidateIdentitiesList );
                CandidateIdentityAttributeHome.delete( idCandidateIdentitiesList );
                CandidateIdentityHistoryHome.delete( idCandidateIdentitiesList );
                TransactionManager.commitTransaction( null );
            }
        }
        catch( final Exception e )
        {
            TransactionManager.rollBack( null );
        }

        // return message for daemons
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

    public BatchStatusDto getBatchStatus( String strBatchReference, String strMode ) throws IdentityStoreException
    {
        // TODO
        return null;
    }
}
