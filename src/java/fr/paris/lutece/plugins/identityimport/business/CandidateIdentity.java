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
package fr.paris.lutece.plugins.identityimport.business;

import javax.validation.constraints.Size;

import java.io.Serializable;
import java.util.List;

/**
 * This is the business class for the object CandidateIdentity
 */
public class CandidateIdentity implements Serializable
{
    private static final long serialVersionUID = 1L;

    private static final String CONSTANT_RESOURCE_IDENTITY_CANDIDATE = "IDENTITYIMPORT_CANDIDATE_RESOURCE";

    // Variables declarations
    private int _nId;

    private int _nIdBatch;

    @Size( max = 255, message = "#i18n{identityimport.validation.candidateidentity.ConnectionId.size}" )
    private String _strConnectionId;

    @Size( max = 255, message = "#i18n{identityimport.validation.candidateidentity.CustomerId.size}" )
    private String _strCustomerId;

    @Size( max = 255, message = "#i18n{identityimport.validation.candidateidentity.ExternalCustomerId.size}" )
    private String _strExternalCustomerId;

    private String _strClientAppCode;

    @Size( max = 255, message = "#i18n{identityimport.validation.candidateidentity.Status.size}" )
    private String strStatus;

    private List<CandidateIdentityAttribute> _listAttributes;

    /**
     * Returns the Id
     * 
     * @return The Id
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * Sets the Id
     * 
     * @param nId
     *            The Id
     */
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     * Returns the IdBatch
     * 
     * @return The IdBatch
     */
    public int getIdBatch( )
    {
        return _nIdBatch;
    }

    /**
     * Sets the IdBatch
     * 
     * @param nIdBatch
     *            The IdBatch
     */
    public void setIdBatch( int nIdBatch )
    {
        _nIdBatch = nIdBatch;
    }

    /**
     * Returns the ConnectionId
     * 
     * @return The ConnectionId
     */
    public String getConnectionId( )
    {
        return _strConnectionId;
    }

    /**
     * Sets the ConnectionId
     * 
     * @param strConnectionId
     *            The ConnectionId
     */
    public void setConnectionId( String strConnectionId )
    {
        _strConnectionId = strConnectionId;
    }

    /**
     * Returns the CustomerId
     * 
     * @return The CustomerId
     */
    public String getCustomerId( )
    {
        return _strCustomerId;
    }

    /**
     * Sets the CustomerId
     * 
     * @param strCustomerId
     *            The CustomerId
     */
    public void setCustomerId( String strCustomerId )
    {
        _strCustomerId = strCustomerId;
    }

    /**
     * Returns the ExternalCustomerId
     * 
     * @return The ExternalCustomerId
     */
    public String getExternalCustomerId( )
    {
        return _strExternalCustomerId;
    }

    /**
     * Sets the ExternalCustomerId
     * 
     * @param strExternalCustomerId
     *            The ExternalCustomerId
     */
    public void setExternalCustomerId( String strExternalCustomerId )
    {
        _strExternalCustomerId = strExternalCustomerId;
    }

    public String getClientAppCode( )
    {
        return _strClientAppCode;
    }

    public void setClientAppCode( String _strClientAppCode )
    {
        this._strClientAppCode = _strClientAppCode;
    }

    /**
     * set attributes
     * 
     * @param list
     */
    public void setAttributes( List<CandidateIdentityAttribute> list )
    {
        _listAttributes = list;

    }

    /**
     * get attributes
     * 
     * @param list
     */
    public List<CandidateIdentityAttribute> getAttributes( )
    {
        return _listAttributes;
    }

    /**
     * get status
     * 
     * @return the status
     */
    public String getStatus( )
    {
        return strStatus;
    }

    /**
     * set Status
     * 
     * @param strStatus
     */
    public void setStatus( String strStatus )
    {
        this.strStatus = strStatus;
    }
}
