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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Date;

/**
 * This is the business class for the object Batch
 */
public class Batch implements Serializable
{

    private static final String RESOURCE_TYPE = "IDENTITYIMPORT_BATCH_RESOURCE";
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private int _nId;
    private String _strReference;
    private Date _dateDate;

    @NotEmpty( message = "#i18n{identityimport.validation.batch.User.notEmpty}" )
    @Size( max = 255, message = "#i18n{identityimport.validation.batch.User.size}" )
    private String _strUser;

    @NotEmpty( message = "#i18n{identityimport.validation.batch.AppCode.notEmpty}" )
    @Size( max = 50, message = "#i18n{identityimport.validation.batch.AppCode.size}" )
    private String _strAppCode;

    private String _strComment;

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
     * Returns the Date
     * 
     * @return The Date
     */
    public Date getDate( )
    {
        return _dateDate;
    }

    /**
     * Sets the Date
     * 
     * @param dateDate
     *            The Date
     */
    public void setDate( Date dateDate )
    {
        _dateDate = dateDate;
    }

    /**
     * Returns the Reference
     *
     * @return The Reference
     */
    public String getReference( )
    {
        return _strReference;
    }

    /**
     * Sets the Reference
     *
     * @param strReference
     *            The Reference
     */
    public void setReference( String strReference )
    {
        _strReference = strReference;
    }

    /**
     * Returns the User
     * 
     * @return The User
     */
    public String getUser( )
    {
        return _strUser;
    }

    /**
     * Sets the User
     * 
     * @param strUser
     *            The User
     */
    public void setUser( String strUser )
    {
        _strUser = strUser;
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
     * Returns the Comment
     * 
     * @return The Comment
     */
    public String getComment( )
    {
        return _strComment;
    }

    /**
     * Sets the Comment
     * 
     * @param strComment
     *            The Comment
     */
    public void setComment( String strComment )
    {
        _strComment = strComment;
    }

    public String toLog( )
    {
        return "[ID : " + getId( ) + "]" + "[REFERENCE : " + getReference( ) + "]" + "[DATE : " + getDate( ) + "]" + "[USER : " + getUser( ) + "]"
                + "[APP CODE : " + getAppCode( ) + "]";
    }
}
