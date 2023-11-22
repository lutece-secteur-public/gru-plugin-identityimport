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

import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.List;
import java.util.Optional;

/**
 * ICandidateIdentityDAO Interface
 */
public interface ICandidateIdentityDAO
{
    /**
     * Insert a new record in the table.
     * 
     * @param candidateIdentity
     *            instance of the CandidateIdentity object to insert
     * @param plugin
     *            the Plugin
     */
    void insert( CandidateIdentity candidateIdentity, Plugin plugin );

    void deleteList( List<Integer> idList, Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param candidateIdentity
     *            the reference of the CandidateIdentity
     * @param plugin
     *            the Plugin
     */
    void store( CandidateIdentity candidateIdentity, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nKey
     *            The identifier of the CandidateIdentity to delete
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
     *            The identifier of the candidateIdentity
     * @param plugin
     *            the Plugin
     * @return The instance of the candidateIdentity
     */
    Optional<CandidateIdentity> load( int nKey, Plugin plugin );

    /**
     * Load the data of all the candidateIdentity objects and returns them as a list
     * 
     * @param plugin
     *            the Plugin
     * @return The list which contains the data of all the candidateIdentity objects
     */
    List<CandidateIdentity> selectCandidateIdentitiesList( Plugin plugin );

    /**
     * Load the id of all the candidateIdentity objects and returns them as a list
     * 
     * @param plugin
     *            the Plugin
     * @return The list which contains the id of all the candidateIdentity objects
     */
    List<Integer> selectIdCandidateIdentitiesList( int nBatchId, Plugin plugin );

    /**
     * Load the data of all the avant objects and returns them as a list
     * 
     * @param _plugin
     *            the Plugin
     * @param listIds
     *            liste of ids
     * @return The list which contains the data of all the avant objects
     */
    List<CandidateIdentity> selectCandidateIdentitiesListByIds( Plugin _plugin, List<Integer> listIds );

    /**
     * Check if at least one of the externalIds exists in the batch identified by batchReference
     * 
     * @param plugin
     *            the plugin
     * @param batchReference
     *            the reference of the batch
     * @param externalIds
     *            the list of external ids
     * @return true if at least one external id exists in the batch
     */
    boolean checkIfOneExists( Plugin plugin, final String batchReference, List<String> externalIds );

    List<ResourceState> selectIdentityStates( final int batchId, final Plugin plugin );

    List<ResourceHistory> getHistory( final int identityId, final Plugin plugin );
}
