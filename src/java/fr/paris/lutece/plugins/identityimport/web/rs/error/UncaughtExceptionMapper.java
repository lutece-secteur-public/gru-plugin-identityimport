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
package fr.paris.lutece.plugins.identityimport.web.rs.error;

import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.ResponseStatusType;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.error.ErrorResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.Constants;
import fr.paris.lutece.plugins.rest.service.mapper.GenericUncaughtExceptionMapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.Status;

/**
 * Exception mapper designed to intercept uncaught {@link Exception}.<br/>
 * It will only be triggered if no better suitable mappers are available for the fired exception.
 */
@Provider
public class UncaughtExceptionMapper extends GenericUncaughtExceptionMapper<Exception, ErrorResponse>
{
    @Override
    protected Status getStatus( )
    {
        return Status.INTERNAL_SERVER_ERROR;
    }

    @Override
    protected ErrorResponse buildEntity( final Exception e )
    {
        final ErrorResponse response = new ErrorResponse( );
        response.setStatus( ResponseStatusType.valueOf( getStatus( ).name( ) ) );
        response.setMessage( ERROR_DURING_TREATMENT + " :: " + e.getMessage( ) );
        response.setI18nMessageKey( Constants.PROPERTY_REST_ERROR_DURING_TREATMENT );
        return response;
    }

    @Override
    protected String getType( )
    {
        return MediaType.APPLICATION_JSON;
    }
}