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

import fr.paris.lutece.test.LuteceTestCase;

import java.util.Optional;

import java.sql.Date;

/**
 * This is the business class test for the object CandidateIdentityAttribute
 */
public class CandidateIdentityAttributeBusinessTest extends LuteceTestCase
{
    private static final String KEY1 = "Key1";
    private static final String KEY2 = "Key2";
    private static final String VALUE1 = "Value1";
    private static final String VALUE2 = "Value2";
    private static final String CERTPROCESS1 = "CertProcess1";
    private static final String CERTPROCESS2 = "CertProcess2";
    private static final Date CERTDATE1 = new Date( 1000000L );
    private static final Date CERTDATE2 = new Date( 2000000L );

    /**
     * test CandidateIdentityAttribute
     */
    public void testBusiness( )
    {
        // Initialize an object
        CandidateIdentityAttribute candidateIdentityAttribute = new CandidateIdentityAttribute( );
        candidateIdentityAttribute.setCode( KEY1 );
        candidateIdentityAttribute.setValue( VALUE1 );
        candidateIdentityAttribute.setCertProcess( CERTPROCESS1 );
        candidateIdentityAttribute.setCertDate( CERTDATE1 );

        // Create test
        CandidateIdentityAttributeHome.create( candidateIdentityAttribute );
        Optional<CandidateIdentityAttribute> optCandidateIdentityAttributeStored = CandidateIdentityAttributeHome
                .findByPrimaryKey( candidateIdentityAttribute.getId( ) );
        CandidateIdentityAttribute candidateIdentityAttributeStored = optCandidateIdentityAttributeStored.orElse( new CandidateIdentityAttribute( ) );
        assertEquals( candidateIdentityAttributeStored.getCode( ), candidateIdentityAttribute.getCode( ) );
        assertEquals( candidateIdentityAttributeStored.getValue( ), candidateIdentityAttribute.getValue( ) );
        assertEquals( candidateIdentityAttributeStored.getCertProcess( ), candidateIdentityAttribute.getCertProcess( ) );
        assertEquals( candidateIdentityAttributeStored.getCertDate( ).toString( ), candidateIdentityAttribute.getCertDate( ).toString( ) );

        // Update test
        candidateIdentityAttribute.setCode( KEY2 );
        candidateIdentityAttribute.setValue( VALUE2 );
        candidateIdentityAttribute.setCertProcess( CERTPROCESS2 );
        candidateIdentityAttribute.setCertDate( CERTDATE2 );
        CandidateIdentityAttributeHome.update( candidateIdentityAttribute );
        optCandidateIdentityAttributeStored = CandidateIdentityAttributeHome.findByPrimaryKey( candidateIdentityAttribute.getId( ) );
        candidateIdentityAttributeStored = optCandidateIdentityAttributeStored.orElse( new CandidateIdentityAttribute( ) );

        assertEquals( candidateIdentityAttributeStored.getCode( ), candidateIdentityAttribute.getCode( ) );
        assertEquals( candidateIdentityAttributeStored.getValue( ), candidateIdentityAttribute.getValue( ) );
        assertEquals( candidateIdentityAttributeStored.getCertProcess( ), candidateIdentityAttribute.getCertProcess( ) );
        assertEquals( candidateIdentityAttributeStored.getCertDate( ).toString( ), candidateIdentityAttribute.getCertDate( ).toString( ) );

        // Delete test
        CandidateIdentityAttributeHome.remove( candidateIdentityAttribute.getId( ) );
        optCandidateIdentityAttributeStored = CandidateIdentityAttributeHome.findByPrimaryKey( candidateIdentityAttribute.getId( ) );
        candidateIdentityAttributeStored = optCandidateIdentityAttributeStored.orElse( null );
        assertNull( candidateIdentityAttributeStored );

    }

}
