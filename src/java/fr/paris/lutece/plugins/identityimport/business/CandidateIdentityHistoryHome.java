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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;
import java.util.Optional;

public class CandidateIdentityHistoryHome
{
    // Static variable pointed at the DAO instance
    private static final ICandidateIdentityHistoryDAO _dao = SpringContextService.getBean( "identityimport.candidateIdentityHistoryDao" );
    private static final Plugin _plugin = PluginService.getPlugin( "identityimport" );

    /**
     * Insert a new record in the table.
     *
     * @param candidateIdentityHistory
     *            instance of the candidateIdentityHistory object to insert
     */
    public static void insert( CandidateIdentityHistory candidateIdentityHistory )
    {
        _dao.insert( candidateIdentityHistory, _plugin );
    }

    /**
     * Update the record in the table
     *
     * @param candidateIdentityHistory
     *            the reference of the candidateIdentityHistory to update
     */
    public static void update( CandidateIdentityHistory candidateIdentityHistory )
    {
        _dao.update( candidateIdentityHistory, _plugin );
    }

    /**
     * Select a record from the table
     *
     * @param nWfResourceHistoryId
     *            The identifier to select
     */
    public static Optional<CandidateIdentityHistory> selectByWfHistory( int nWfResourceHistoryId )
    {
        return _dao.selectByWfHistory( nWfResourceHistoryId, _plugin );
    }

    /**
     * Select a record from the table
     *
     * @param nId
     *            The identifier to select
     */
    public static Optional<CandidateIdentityHistory> select( int nId )
    {
        return _dao.select( nId, _plugin );
    }

    /**
     * Select a record from the table
     *
     * @param nCandidateIdentityId
     *            The identifier to select
     */
    public static List<CandidateIdentityHistory> selectAll( int nCandidateIdentityId )
    {
        return _dao.selectAll( nCandidateIdentityId, _plugin );
    }

    /**
     * Delete a record from the table
     *
     * @param nKey
     *            The identifier of the CandidateIdentity to delete
     */
    public static void delete( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Delete a list of records from the table
     *
     * @param idList
     *            The identifier list of the CandidateIdentity to delete
     */
    public static void delete( final List<Integer> idList )
    {
        _dao.deleteList( idList, _plugin );
    }
}
