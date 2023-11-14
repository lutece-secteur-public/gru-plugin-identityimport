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

import fr.paris.lutece.plugins.identityimport.business.CandidateIdentity;
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentityAttribute;
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentityHistory;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.CandidateIdentityAttributeDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.CandidateIdentityDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.ImportingHistoryDto;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;

import java.util.ArrayList;
import java.util.List;

public class CandidateIdentityService
{

    private static CandidateIdentityService instance;

    public static CandidateIdentityService instance( )
    {
        if ( instance == null )
        {
            instance = new CandidateIdentityService( );
        }
        return instance;
    }

    public CandidateIdentityDto getDto( final CandidateIdentity bean, final List<CandidateIdentityHistory> identityHistoryList,
            final List<ResourceHistory> resourceHistoryList )
    {
        if ( bean == null )
        {
            return null;
        }
        final CandidateIdentityDto dto = new CandidateIdentityDto( );
        dto.setCustomerId( bean.getCustomerId( ) );
        dto.setConnectionId( bean.getConnectionId( ) );
        dto.setExternalCustomerId( bean.getExternalCustomerId( ) );
        dto.setClientAppCode( bean.getClientAppCode( ) );
        dto.setStatus( bean.getStatus( ) );
        bean.getAttributes( ).forEach( attr -> {
            final CandidateIdentityAttributeDto attrDto = new CandidateIdentityAttributeDto( );
            attrDto.setKey( attr.getCode( ) );
            attrDto.setValue( attr.getValue( ) );
            attrDto.setCertProcess( attr.getCertProcess( ) );
            attrDto.setCertDate( attr.getCertDate( ) );
            dto.getAttributes( ).add( attrDto );
        } );
        if ( identityHistoryList != null )
        {
            identityHistoryList.forEach( hist -> {
                final ImportingHistoryDto histDto = new ImportingHistoryDto( );
                final ResourceHistory resourceHistory = resourceHistoryList.stream( ).filter( rh -> rh.getId( ) == hist.getWfResourceHistoryId( ) ).findFirst( )
                        .orElse( null );
                if ( resourceHistory != null )
                {
                    histDto.setDate( resourceHistory.getCreationDate( ) );
                    histDto.setActionName( resourceHistory.getAction( ).getName( ) );
                    histDto.setActionDescription( resourceHistory.getAction( ).getDescription( ) );
                    histDto.setUserAccessCode( resourceHistory.getUserAccessCode( ) );
                }
                histDto.setStatus( hist.getStatus( ) );
                histDto.setComment( hist.getComment( ) );
                dto.getIdentityHistory( ).add( histDto );
            } );
        }
        return dto;
    }

    public CandidateIdentity getBean( final CandidateIdentityDto dto )
    {
        if ( dto == null )
        {
            return null;
        }
        final CandidateIdentity bean = new CandidateIdentity( );
        bean.setCustomerId( dto.getCustomerId( ) );
        bean.setConnectionId( dto.getConnectionId( ) );
        bean.setExternalCustomerId( dto.getExternalCustomerId( ) );
        bean.setClientAppCode( dto.getClientAppCode( ) );
        bean.setStatus( dto.getStatus( ) );
        if ( !dto.getAttributes( ).isEmpty( ) )
        {
            bean.setAttributes( new ArrayList<>( dto.getAttributes( ).size( ) ) );
            dto.getAttributes( ).forEach( attrDto -> {
                final CandidateIdentityAttribute attr = new CandidateIdentityAttribute( );
                attr.setCode( attrDto.getKey( ) );
                attr.setValue( attrDto.getValue( ) );
                attr.setCertProcess( attrDto.getCertProcess( ) );
                attr.setCertDate( attrDto.getCertDate( ) );
                bean.getAttributes( ).add( attr );
            } );
        }
        return bean;
    }
}
