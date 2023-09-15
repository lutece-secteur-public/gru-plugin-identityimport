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

import fr.paris.lutece.plugins.identityimport.cache.ServiceContractCache;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.AttributeDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.IdentityDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.contract.AttributeDefinitionDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.contract.CertificationProcessusDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.contract.ServiceContractDto;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.Objects;
import java.util.Optional;

public class ServiceContractService
{

    private final ServiceContractCache _cache = SpringContextService.getBean( "identity.serviceContractCacheService" );

    private static ServiceContractService instance;

    public static ServiceContractService instance( )
    {
        if ( instance == null )
        {
            instance = new ServiceContractService( );
            instance._cache.resetCache( );
        }
        return instance;
    }

    public void validateIdentity( final IdentityDto identity, final String clientCode ) throws IdentityStoreException
    {
        // TODO couldn't it be an API ?
        final ServiceContractDto serviceContract = _cache.get( clientCode );
        if ( serviceContract == null )
        {
            throw new IdentityStoreException( "No service contract could be found with client code " + clientCode );
        }
        for ( final AttributeDto attribute : identity.getAttributes( ) )
        {
            final AttributeDefinitionDto attributeDefinition = serviceContract.getAttributeDefinitions( ).stream( )
                    .filter( a -> Objects.equals( a.getKeyName( ), attribute.getKey( ) ) ).findFirst( ).orElseThrow(
                            ( ) -> new IdentityStoreException( "Attribute " + attribute.getKey( ) + " does not exist in the service contract definition" ) );

            if ( !attributeDefinition.getAttributeRight( ).isWritable( ) )
            {
                throw new IdentityStoreException( "Attribute " + attribute.getKey( ) + " is not writable in the service contract definition" );
            }

            final Optional<CertificationProcessusDto> certificationProcessus = attributeDefinition.getAttributeCertifications( ).stream( )
                    .filter( a -> Objects.equals( a.getCode( ), attribute.getCertifier( ) ) ).findFirst( );
            if ( !certificationProcessus.isPresent( ) )
            {
                throw new IdentityStoreException( "Attribute certifier " + attribute.getCertifier( ) + " of attribute " + attribute.getKey( )
                        + " is not authorized in the service contract definition" );
            }
        }
    }

    /**
     * Get active service contract corresponding to the provided client code.
     * 
     * @param clientCode
     *            the client code
     * @return ServiceContractDto instance
     */
    public ServiceContractDto getActiveServiceContract( final String clientCode ) throws IdentityStoreException
    {
        return _cache.get( clientCode );
    }
}
