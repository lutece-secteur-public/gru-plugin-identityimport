package fr.paris.lutece.plugins.identityimport.cache;

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

import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.AuthorType;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.RequestAuthor;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.referentiel.AttributeCertificationProcessusDto;
import fr.paris.lutece.plugins.identitystore.v3.web.service.ReferentialService;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import fr.paris.lutece.portal.service.cache.AbstractCacheableService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.util.List;

public class ReferentialCache extends AbstractCacheableService
{
    private static final String SERVICE_NAME = "ReferentialCacheService";
    private static final String KEY = "ReferentialCacheService";
    private static final String PROPERTY_IMPORT_CLIENT_CODE = "identityimport.client.code";
    private static final String IMPORT_CLIENT_CODE = AppPropertiesService.getProperty( PROPERTY_IMPORT_CLIENT_CODE );
    private final ReferentialService _referentialService;

    public ReferentialCache( ReferentialService srService )
    {
        _referentialService = srService;
        this.initCache( );
    }

    public void put( final String processus, final List<AttributeCertificationProcessusDto> referential )
    {
        if ( this.getKeys( ).contains( processus ) )
        {
            this.removeKey( processus );
        }
        this.putInCache( processus, referential );
        AppLogService.debug( "Processus added to cache: " + processus );
    }

    public void remove( final String processus )
    {
        if ( this.getKeys( ).contains( processus ) )
        {
            this.removeKey( processus );
        }
        AppLogService.debug( "Processus removed from cache: " + processus );
    }

    public List<AttributeCertificationProcessusDto> get( ) throws IdentityStoreException
    {
        final List<AttributeCertificationProcessusDto> processusDtos = _referentialService.getProcessList( IMPORT_CLIENT_CODE, this.buildAuthor( ) )
                .getProcessus( );
        this.put( KEY, processusDtos );
        return processusDtos;
    }

    private RequestAuthor buildAuthor( )
    {
        return new RequestAuthor( "IdentityDesk_ServiceReferentialCache", AuthorType.application.name( ) );
    }

    @Override
    public String getName( )
    {
        return SERVICE_NAME;
    }
}
