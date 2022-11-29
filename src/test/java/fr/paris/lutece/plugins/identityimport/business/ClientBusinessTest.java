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

import fr.paris.lutece.test.LuteceTestCase;

import java.util.Optional;

/**
 * This is the business class test for the object Client
 */
public class ClientBusinessTest extends LuteceTestCase
{
    private static final String NAME1 = "Name1";
    private static final String NAME2 = "Name2";
    private static final String APPCODE1 = "AppCode1";
    private static final String APPCODE2 = "AppCode2";
    private static final String TOKEN1 = "Token1";
    private static final String TOKEN2 = "Token2";

    /**
     * test Client
     */
    public void testBusiness( )
    {
        // Initialize an object
        Client client = new Client( );
        client.setName( NAME1 );
        client.setAppCode( APPCODE1 );
        client.setToken( TOKEN1 );

        // Create test
        ClientHome.create( client );
        Optional<Client> optClientStored = ClientHome.findByPrimaryKey( client.getId( ) );
        Client clientStored = optClientStored.orElse( new Client( ) );
        assertEquals( clientStored.getName( ), client.getName( ) );
        assertEquals( clientStored.getAppCode( ), client.getAppCode( ) );
        assertEquals( clientStored.getToken( ), client.getToken( ) );

        // Update test
        client.setName( NAME2 );
        client.setAppCode( APPCODE2 );
        client.setToken( TOKEN2 );
        ClientHome.update( client );
        optClientStored = ClientHome.findByPrimaryKey( client.getId( ) );
        clientStored = optClientStored.orElse( new Client( ) );

        assertEquals( clientStored.getName( ), client.getName( ) );
        assertEquals( clientStored.getAppCode( ), client.getAppCode( ) );
        assertEquals( clientStored.getToken( ), client.getToken( ) );

        // List test
        ClientHome.getClientsList( );

        // Delete test
        ClientHome.remove( client.getId( ) );
        optClientStored = ClientHome.findByPrimaryKey( client.getId( ) );
        clientStored = optClientStored.orElse( null );
        assertNull( clientStored );

    }

}
