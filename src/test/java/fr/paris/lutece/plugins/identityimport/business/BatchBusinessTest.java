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
 * This is the business class test for the object Batch
 */
public class BatchBusinessTest extends LuteceTestCase
{
    private static final Date DATE1 = new Date( 1000000L );
    private static final Date DATE2 = new Date( 2000000L );
    private static final String USER1 = "User1";
    private static final String USER2 = "User2";
    private static final String APPCODE1 = "AppCode1";
    private static final String APPCODE2 = "AppCode2";
    private static final String COMMENT1 = "Comment1";
    private static final String COMMENT2 = "Comment2";

    /**
     * test Batch
     */
    public void testBusiness( )
    {
        // Initialize an object
        Batch batch = new Batch( );
        batch.setDate( DATE1 );
        batch.setUser( USER1 );
        batch.setAppCode( APPCODE1 );
        batch.setComment( COMMENT1 );

        // Create test
        BatchHome.create( batch );
        Optional<Batch> optBatchStored = BatchHome.findByPrimaryKey( batch.getId( ) );
        Batch batchStored = optBatchStored.orElse( new Batch( ) );
        assertEquals( batchStored.getDate( ).toString( ), batch.getDate( ).toString( ) );
        assertEquals( batchStored.getUser( ), batch.getUser( ) );
        assertEquals( batchStored.getAppCode( ), batch.getAppCode( ) );
        assertEquals( batchStored.getComment( ), batch.getComment( ) );

        // Update test
        batch.setDate( DATE2 );
        batch.setUser( USER2 );
        batch.setAppCode( APPCODE2 );
        batch.setComment( COMMENT2 );
        BatchHome.update( batch );
        optBatchStored = BatchHome.findByPrimaryKey( batch.getId( ) );
        batchStored = optBatchStored.orElse( new Batch( ) );

        assertEquals( batchStored.getDate( ).toString( ), batch.getDate( ).toString( ) );
        assertEquals( batchStored.getUser( ), batch.getUser( ) );
        assertEquals( batchStored.getAppCode( ), batch.getAppCode( ) );
        assertEquals( batchStored.getComment( ), batch.getComment( ) );

        // List test
        BatchHome.getBatchsList( );

        // Delete test
        BatchHome.remove( batch.getId( ) );
        optBatchStored = BatchHome.findByPrimaryKey( batch.getId( ) );
        batchStored = optBatchStored.orElse( null );
        assertNull( batchStored );

    }

}
