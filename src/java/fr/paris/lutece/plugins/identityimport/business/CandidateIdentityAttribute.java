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
package fr.paris.lutece.plugins.identityimport.business;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * This is the business class for the object CandidateIdentityAttribute
 */
public class CandidateIdentityAttribute implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private int _nId;
    
    private int _nIdIdentity;

    @NotEmpty( message = "#i18n{identityimport.validation.candidateidentityattribute.Key.notEmpty}" )
    @Size( max = 255, message = "#i18n{identityimport.validation.candidateidentityattribute.Key.size}" )
    private String _strCode;

    private String _strValue;

    @Size( max = 255, message = "#i18n{identityimport.validation.candidateidentityattribute.CertProcess.size}" )
    private String _strCertProcess;
    @NotNull( message = "#i18n{portal.validation.message.notEmpty}" )
    private Date _dateCertDate;

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
     * Returns the Key
     * 
     * @return The Key
     */
    public String getCode( )
    {
        return _strCode;
    }

    /**
     * Sets the Key
     * 
     * @param strKey
     *            The Key
     */
    public void setCode( String strKey )
    {
        _strCode = strKey;
    }

    /**
     * Returns the Value
     * 
     * @return The Value
     */
    public String getValue( )
    {
        return _strValue;
    }

    /**
     * Sets the Value
     * 
     * @param strValue
     *            The Value
     */
    public void setValue( String strValue )
    {
        _strValue = strValue;
    }

    /**
     * Returns the CertProcess
     * 
     * @return The CertProcess
     */
    public String getCertProcess( )
    {
        return _strCertProcess;
    }

    /**
     * Sets the CertProcess
     * 
     * @param strCertProcess
     *            The CertProcess
     */
    public void setCertProcess( String strCertProcess )
    {
        _strCertProcess = strCertProcess;
    }

    /**
     * Returns the CertDate
     * 
     * @return The CertDate
     */
    public Date getCertDate( )
    {
        return _dateCertDate;
    }

    /**
     * Sets the CertDate
     * 
     * @param dateCertDate
     *            The CertDate
     */
    public void setCertDate( Date dateCertDate )
    {
        _dateCertDate = dateCertDate;
    }

    /**
     * get identity id
     * 
     * @return the id
     */
	public int getIdentityId() {
		return _nIdIdentity;
	}

	/**
	 * set identity id
	 * 
	 * @param _nIdIdentity
	 */
	public void setIdentityId(int _nIdIdentity) {
		this._nIdIdentity = _nIdIdentity;
	}

}
