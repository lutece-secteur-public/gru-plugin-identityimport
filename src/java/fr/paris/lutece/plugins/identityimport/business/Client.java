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
package fr.paris.lutece.plugins.identityimport.business;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * This is the business class for the object Client
 */
public class Client implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private int _nId;

    private int _nDataRetentionPeriodInMonths;

    @NotEmpty( message = "#i18n{identityimport.validation.client.Name.notEmpty}" )
    @Size( max = 255, message = "#i18n{identityimport.validation.client.Name.size}" )
    private String _strName;

    @NotEmpty( message = "#i18n{identityimport.validation.client.AppCode.notEmpty}" )
    @Size( max = 50, message = "#i18n{identityimport.validation.client.AppCode.size}" )
    private String _strAppCode;

    private String _strToken;

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
     * Returns the Name
     * 
     * @return The Name
     */
    public String getName( )
    {
        return _strName;
    }

    /**
     * Sets the Name
     * 
     * @param strName
     *            The Name
     */
    public void setName( String strName )
    {
        _strName = strName;
    }

    /**
     * Returns the AppCode
     * 
     * @return The AppCode
     */
    public String getAppCode( )
    {
        return _strAppCode;
    }

    /**
     * Sets the AppCode
     * 
     * @param strAppCode
     *            The AppCode
     */
    public void setAppCode( String strAppCode )
    {
        _strAppCode = strAppCode;
    }

    /**
     * Returns the Token
     * 
     * @return The Token
     */
    public String getToken( )
    {
        return _strToken;
    }

    /**
     * Sets the Token
     * 
     * @param strToken
     *            The Token
     */
    public void setToken( String strToken )
    {
        _strToken = strToken;
    }

    public int getDataRetentionPeriodInMonths( )
    {
        return _nDataRetentionPeriodInMonths;
    }

    public void setDataRetentionPeriodInMonths( int dataRetentionPeriodInMonths )
    {
        this._nDataRetentionPeriodInMonths = dataRetentionPeriodInMonths;
    }
}
