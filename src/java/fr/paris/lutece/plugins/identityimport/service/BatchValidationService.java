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

import fr.paris.lutece.plugins.identityimport.business.CandidateIdentityHome;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.AttributeDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.BatchDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.IdentityDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.Constants;
import fr.paris.lutece.plugins.identitystore.web.exception.DuplicatesConsistencyException;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import fr.paris.lutece.plugins.identitystore.web.exception.RequestFormatException;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.stream.Collectors;

public class BatchValidationService
{
    private final int importBatchLimit = AppPropertiesService.getPropertyInt( "identityimport.identitystore.api.batch.identity.limit", 100 );
    private static final String MESSAGE_KEY_BATCH_OVER_IDENTITY_LIMIT = "identityimport.error.batch.over.identity.limit";
    private static final String MESSAGE_KEY_BATCH_NOT_PROVIDED = "identityimport.error.batch.not.provided";
    private static final String MESSAGE_KEY_BATCH_WITHOUT_USER = "identityimport.error.batch.without.user";
    private static final String MESSAGE_KEY_BATCH_WITHOUT_CLIENT_CODE = "identityimport.error.batch.without.client.code";
    private static final String MESSAGE_KEY_BATCH_WITHOUT_REFERENCE = "identityimport.error.batch.without.reference";
    private static final String MESSAGE_KEY_BATCH_WITHOUT_IDENTITIES = "identityimport.error.batch.without.identities";
    private static final String MESSAGE_KEY_BATCH_WITH_IDENTITY_DUPLICATES = "identityimport.error.batch.with.identity.duplicate";
    private static final String MESSAGE_KEY_BATCH_WITH_IDENTITY_WITHOUT_EXTERNAL_CUID = "identityimport.error.batch.with.identity.without.external.cuid";
    private static final String MESSAGE_KEY_BATCH_WITH_IDENTITY_WITHOUT_CERTIFIER = "identityimport.error.batch.with.identity.without.certifier";
    private static final String MESSAGE_KEY_BATCH_WITH_IDENTITY_WITHOUT_CERTIFICATION_DATE = "identityimport.error.batch.with.identity.without.certification.date";
    private static final String MESSAGE_KEY_BATCH_WITH_IDENTITY_WITHOUT_MINIMUM_ATTRIBUTES = "identityimport.error.batch.with.identity.without.minimum.attributes";

    private static BatchValidationService _instance;

    private BatchValidationService( )
    {
    }

    public static BatchValidationService instance( )
    {
        if ( _instance == null )
        {
            _instance = new BatchValidationService( );
        }
        return _instance;
    }

    public void validateImportBatchLimit( final BatchDto batch ) throws RequestFormatException
    {
        if ( batch.getIdentities( ).size( ) > importBatchLimit )
        {
            throw new RequestFormatException( "The imported batch exceeds limit of " + importBatchLimit + " identities.",
                    MESSAGE_KEY_BATCH_OVER_IDENTITY_LIMIT );
        }
    }

    /**
     * Validate that batch definition is consistent. Check if the format and the compliance to the client Service Contract.
     *
     * @param batch
     *            the batch to validate
     * @throws IdentityStoreException
     *             in case of error
     */
    public void validateBatchFromIhm(final BatchDto batch) throws IdentityStoreException
    {
        validateBatchNotNull(batch);
        validateUser(batch);
        validateClientCode(batch);
        validateReference(batch);
        validateIdentitiesUniqueness(batch);
        validateIdentities(batch);
        ServiceContractService.instance().validateImportAuthorization(batch.getClientCode());
        for(final IdentityDto identity : batch.getIdentities( ) ) {
            ServiceContractService.instance( ).validateIdentityAgainstServiceContract(identity, batch.getClientCode());
        }
    }

    public void validateBatchNotNull(final BatchDto batch) throws RequestFormatException {
        if ( batch == null )
        {
            throw new RequestFormatException( "The provided batch is null", MESSAGE_KEY_BATCH_NOT_PROVIDED );
        }
    }

    public void validateUser(final BatchDto batch) throws RequestFormatException {
        if ( StringUtils.isEmpty( batch.getUser( ) ) )
        {
            throw new RequestFormatException( "The provided batch user is null", MESSAGE_KEY_BATCH_WITHOUT_USER );
        }
    }

    public void validateClientCode(final BatchDto batch) throws RequestFormatException {
        if ( StringUtils.isEmpty( batch.getClientCode( ) ) )
        {
            throw new RequestFormatException( "The provided batch client code is null", MESSAGE_KEY_BATCH_WITHOUT_CLIENT_CODE );
        }
    }

    public void validateReference(final BatchDto batch) throws RequestFormatException {
        if ( StringUtils.isEmpty( batch.getReference( ) ) )
        {
            throw new RequestFormatException( "The provided batch reference is null", MESSAGE_KEY_BATCH_WITHOUT_REFERENCE );
        }
    }

    public void validateIdentitiesUniqueness( final BatchDto batch ) throws DuplicatesConsistencyException {
        if ( CandidateIdentityHome.checkIfOneExists( batch.getReference( ),
                                                     batch.getIdentities( ).stream( ).map( IdentityDto::getExternalCustomerId ).collect( Collectors.toList( ) ) ) )
        {
            throw new DuplicatesConsistencyException("At least one of the provided identities already exists in the current batch",
                                                     MESSAGE_KEY_BATCH_WITH_IDENTITY_DUPLICATES );
        }
    }

    public void validateIdentities(final BatchDto batch) throws RequestFormatException {
        if ( batch.getIdentities( ).isEmpty( ) )
        {
            throw new RequestFormatException( "No identities found in imported batch", MESSAGE_KEY_BATCH_WITHOUT_IDENTITIES );
        }
        final IdentityDto [ ] identitiesArray = batch.getIdentities( ).toArray( new IdentityDto [ ] { } );
        for ( int index = 0; index < identitiesArray.length; index++ )
        {
            final IdentityDto identity = identitiesArray [index];

            if ( StringUtils.isEmpty( identity.getExternalCustomerId( ) ) )
            {
                throw new RequestFormatException( "The provided external customer id of identity " + index + " is empty",
                                                  MESSAGE_KEY_BATCH_WITH_IDENTITY_WITHOUT_EXTERNAL_CUID );
            }

            for ( final AttributeDto attribute : identity.getAttributes( ) )
            {
                if ( StringUtils.isEmpty( attribute.getCertifier( ) ) )
                {
                    throw new RequestFormatException(
                            "The provided attribute " + attribute.getKey( ) + " certifier of identity " + identity.getExternalCustomerId( ) + " is null",
                            MESSAGE_KEY_BATCH_WITH_IDENTITY_WITHOUT_CERTIFIER );
                }
                if ( attribute.getCertificationDate( ) == null )
                {
                    throw new RequestFormatException( "The provided attribute " + attribute.getKey( ) + " certification date of identity "
                                                      + identity.getExternalCustomerId( ) + " is null", MESSAGE_KEY_BATCH_WITH_IDENTITY_WITHOUT_CERTIFICATION_DATE );
                }
            }

            this.validateMinimumAttributes( identity );
        }
    }

    private void validateMinimumAttributes( final IdentityDto identity ) throws RequestFormatException
    {
        this.checkAttributeExists( identity, Constants.PARAM_FAMILY_NAME );
        this.checkAttributeExists( identity, Constants.PARAM_FIRST_NAME );
        this.checkAttributeExists( identity, Constants.PARAM_BIRTH_DATE );
    }

    private void checkAttributeExists( final IdentityDto identity, final String attributeKey ) throws RequestFormatException
    {
        identity.getAttributes( ).stream( ).filter( attributeDto -> Objects.equals( attributeDto.getKey( ), attributeKey ) ).findAny( )
                .orElseThrow( ( ) -> new RequestFormatException(
                        "No " + attributeKey + " attribute found in identity with external ID " + identity.getExternalCustomerId( ),
                        MESSAGE_KEY_BATCH_WITH_IDENTITY_WITHOUT_MINIMUM_ATTRIBUTES ) );
    }

}
