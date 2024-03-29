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

import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;
import java.util.Optional;

/**
 * IBatchDAO Interface
 */
public interface IBatchDAO
{
    /**
     * Insert a new record in the table.
     *
     * @param batch
     *            instance of the Batch object to insert
     * @param plugin
     *            the Plugin
     */
    void insert( Batch batch, Plugin plugin );

    /**
     * Update the record in the table
     *
     * @param batch
     *            the reference of the Batch
     * @param plugin
     *            the Plugin
     */
    void store( Batch batch, Plugin plugin );

    /**
     * Delete a record from the table
     *
     * @param nKey
     *            The identifier of the Batch to delete
     * @param plugin
     *            the Plugin
     */
    void delete( int nKey, Plugin plugin );

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Load the data from the table
     *
     * @param nKey
     *            The identifier of the batch
     * @param plugin
     *            the Plugin
     * @return The instance of the batch
     */
    Optional<Batch> load( int nKey, Plugin plugin );

    /**
     * Load the data of all the batch objects and returns them as a list
     *
     * @param plugin
     *            the Plugin
     * @return The list which contains the data of all the batch objects
     */
    List<Batch> selectBatchsList( Plugin plugin );

    /**
     * Load the data of all the expired batch objects and returns them as a list. <br>
     * A batch is considered expired if it is finalized and it's creation date exceeds data retention time of the client.
     * 
     * @param plugin
     *            the Plugin
     * @return The list which contains the data of all the expired batch objects
     */
    List<Batch> selectExpiredBatchsList( final int batchLimit, Plugin plugin );

    /**
     * Load the data of all the batches in the initial workflow state and returns them as a list
     *
     * @param batchLimit
     *            the limit
     * @param plugin
     *            the plugin
     * @return batches in the initial workflow state
     */
    List<Batch> selectInitialStateBatches( final int batchLimit, final Plugin plugin );

    /**
     * Load the data of all the batches in the in treatment workflow state and returns them as a list
     *
     * @param batchLimit
     *            the limit
     * @param plugin
     *            the plugin
     * @return batches in the initial workflow state
     */
    List<Batch> selectClosableBatches( int batchLimit, Plugin plugin );

    /**
     * Load the id of all the batch objects that are in given state and returns them as a list
     *
     * @param batchStateId
     *            the id of the batch state to filter results with
     * @param filterAppCode
     *            the application code to filter the results with
     * @param plugin
     *            the Plugin
     * @return The list which contains the id of all the batch objects
     */
    List<Integer> selectIdBatchsList( final Integer batchStateId, final String filterAppCode, Plugin plugin );

    /**
     * Load the data of all the batch objects and returns them as a referenceList
     *
     * @param plugin
     *            the Plugin
     * @return The referenceList which contains the data of all the batch objects
     */
    ReferenceList selectBatchsReferenceList( Plugin plugin );

    /**
     * Load the data of all the avant objects and returns them as a list
     *
     * @param _plugin
     *            the Plugin
     * @param listIds
     *            liste of ids
     * @return The list which contains the data of all the avant objects
     */
    List<Batch> selectBatchsListByIds( Plugin _plugin, List<Integer> listIds );

    Batch selectBatchByReference( Plugin plugin, String reference );

    List<ResourceState> selectBatchStates( final String filterAppCode, Plugin plugin );

    ResourceState selectBatchState( final int batchId, final Plugin plugin );

    int countIdentities( int resourceId, Plugin plugin );

    List<ResourceHistory> getBatchHistory( final int batchId, final Plugin plugin );

    /**
     * Get the id of the action for batches in initial state
     *
     * @param plugin
     *            the plugin
     * @return the action id
     */
    int getBatchInitialActionId( final Plugin plugin ) throws IdentityStoreException;

    /**
     * Get the id of the action for batches in treatment state
     *
     * @param plugin
     *            the plugin
     * @return the action id
     */
    int getBatchInTreatmentActionId( final Plugin plugin ) throws IdentityStoreException;
}
