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

/**
 * This is the business class test for the object CandidateIdentity
 */
public class CandidateIdentityBusinessTest extends LuteceTestCase
{
    private static final int IDBATCH1 = 1;
    private static final int IDBATCH2 = 2;
    private static final String CONNECTIONID1 = "ConnectionId1";
    private static final String CONNECTIONID2 = "ConnectionId2";
    private static final String CUSTOMERID1 = "CustomerId1";
    private static final String CUSTOMERID2 = "CustomerId2";

    /**
     * test CandidateIdentity
     */
    public void testBusiness( )
    {
        // Initialize an object
        CandidateIdentity candidateIdentity = new CandidateIdentity( );
        candidateIdentity.setIdBatch( IDBATCH1 );
        candidateIdentity.setConnectionId( CONNECTIONID1 );
        candidateIdentity.setCustomerId( CUSTOMERID1 );

        // Create test
        CandidateIdentityHome.create( candidateIdentity );
        Optional<CandidateIdentity> optCandidateIdentityStored = CandidateIdentityHome.findByPrimaryKey( candidateIdentity.getId( ) );
        CandidateIdentity candidateIdentityStored = optCandidateIdentityStored.orElse( new CandidateIdentity( ) );
        assertEquals( candidateIdentityStored.getIdBatch( ), candidateIdentity.getIdBatch( ) );
        assertEquals( candidateIdentityStored.getConnectionId( ), candidateIdentity.getConnectionId( ) );
        assertEquals( candidateIdentityStored.getCustomerId( ), candidateIdentity.getCustomerId( ) );

        // Update test
        candidateIdentity.setIdBatch( IDBATCH2 );
        candidateIdentity.setConnectionId( CONNECTIONID2 );
        candidateIdentity.setCustomerId( CUSTOMERID2 );
        CandidateIdentityHome.update( candidateIdentity );
        optCandidateIdentityStored = CandidateIdentityHome.findByPrimaryKey( candidateIdentity.getId( ) );
        candidateIdentityStored = optCandidateIdentityStored.orElse( new CandidateIdentity( ) );

        assertEquals( candidateIdentityStored.getIdBatch( ), candidateIdentity.getIdBatch( ) );
        assertEquals( candidateIdentityStored.getConnectionId( ), candidateIdentity.getConnectionId( ) );
        assertEquals( candidateIdentityStored.getCustomerId( ), candidateIdentity.getCustomerId( ) );

        // List test
        CandidateIdentityHome.getCandidateIdentitiesList( );

        // Delete test
        CandidateIdentityHome.remove( candidateIdentity.getId( ) );
        optCandidateIdentityStored = CandidateIdentityHome.findByPrimaryKey( candidateIdentity.getId( ) );
        candidateIdentityStored = optCandidateIdentityStored.orElse( null );
        assertNull( candidateIdentityStored );

    }

}
