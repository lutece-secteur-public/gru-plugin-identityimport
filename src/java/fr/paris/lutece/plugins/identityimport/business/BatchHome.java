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

import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.BatchDto;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;
import java.util.Optional;

/**
 * This class provides instances management methods (create, find, ...) for Batch objects
 */
public final class BatchHome
{
    // Static variable pointed at the DAO instance
    private static IBatchDAO _dao = SpringContextService.getBean( "identityimport.batchDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "identityimport" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private BatchHome( )
    {
    }

    /**
     * Create an instance of the batch class
     * 
     * @param batch
     *            The instance of the Batch which contains the informations to store
     * @return The instance of batch which has been created with its primary key.
     */
    public static Batch create( Batch batch )
    {
        _dao.insert( batch, _plugin );

        return batch;
    }

    /**
     * Update of the batch which is specified in parameter
     * 
     * @param batch
     *            The instance of the Batch which contains the data to store
     * @return The instance of the batch which has been updated
     */
    public static Batch update( Batch batch )
    {
        _dao.store( batch, _plugin );

        return batch;
    }

    /**
     * Remove the batch whose identifier is specified in parameter
     * 
     * @param nKey
     *            The batch Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a batch whose identifier is specified in parameter
     * 
     * @param nKey
     *            The batch primary key
     * @return an instance of Batch
     */
    public static Optional<Batch> findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the batch objects and returns them as a list
     * 
     * @return the list which contains the data of all the batch objects
     */
    public static List<Batch> getBatchsList( )
    {
        return _dao.selectBatchsList( _plugin );
    }

    /**
     * Load the id of all the batch objects and returns them as a list
     * 
     * @return the list which contains the id of all the batch objects
     */
    public static List<Integer> getIdBatchsList( )
    {
        return _dao.selectIdBatchsList( _plugin );
    }

    /**
     * Load the data of all the batch objects and returns them as a referenceList
     * 
     * @return the referenceList which contains the data of all the batch objects
     */
    public static ReferenceList getBatchsReferenceList( )
    {
        return _dao.selectBatchsReferenceList( _plugin );
    }

    /**
     * Load the data of all the avant objects and returns them as a list
     * 
     * @param listIds
     *            liste of ids
     * @return the list which contains the data of all the avant objects
     */
    public static List<Batch> getBatchsListByIds( List<Integer> listIds )
    {
        return _dao.selectBatchsListByIds( _plugin, listIds );
    }

    public static Batch getBatch( final String reference )
    {
        return _dao.selectBatchByReference( _plugin, reference );
    }

    public static Optional<Batch> getBatch( final int id )
    {
        return _dao.load( id, _plugin );
    }
}
