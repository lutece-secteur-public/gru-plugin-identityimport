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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class provides Data Access methods for CandidateIdentity objects
 */
public final class CandidateIdentityHistoryDAO implements ICandidateIdentityHistoryDAO
{
    // Constants
    private static final String QUERY_SELECT_ALL = "SELECT ih.id_history, ih.id_wf_resource_history, ih.status, ih.comment, wrh.id_resource as id_candidate_identity FROM identityimport_candidate_identity_history ih JOIN idimport.workflow_resource_history wrh on ih.id_wf_resource_history = wrh.id_history";
    private static final String ID_LIST = "%{id_list}";
    private final static String SQL_QUERY_SELECT_BY_ID = QUERY_SELECT_ALL + " WHERE id_history = ?";
    private final static String SQL_QUERY_SELECT_BY_WF_RESOURCE_ID = QUERY_SELECT_ALL + " WHERE id_wf_resource_history = ?";

    private final static String SQL_QUERY_INSERT = "INSERT INTO identityimport_candidate_identity_history(id_wf_resource_history, status, comment) VALUES (?, ?, ?)";

    private final static String SQL_QUERY_DELETE = "DELETE FROM identityimport_candidate_identity_history WHERE id_history = ?";

    private static final String SQL_QUERY_DELETE_LISTS = "DELETE wrh, cih FROM workflow_resource_history wrh JOIN identityimport_candidate_identity_history cih ON wrh.id_history = cih.id_wf_resource_history where wrh.id_resource IN ( "
            + ID_LIST + " )";

    private final static String SQL_QUERY_UPDATE = "UPDATE identityimport_candidate_identity_history SET status = ?, comment = ? WHERE id_wf_resource_history = ?";

    private final static String SQL_QUERY_SELECT_ALL = QUERY_SELECT_ALL
            + " JOIN workflow_resource_history rh ON rh.id_history = ih.id_wf_resource_history WHERE rh.id_resource = ?";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( CandidateIdentityHistory candidateIdentityHistory, Plugin plugin )
    {
        try ( final DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, candidateIdentityHistory.getWfResourceHistoryId( ) );
            daoUtil.setString( nIndex++, candidateIdentityHistory.getStatus( ) );
            daoUtil.setString( nIndex, candidateIdentityHistory.getComment( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                candidateIdentityHistory.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<CandidateIdentityHistory> select( int nId, Plugin plugin )
    {
        try ( final DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID, plugin ) )
        {
            daoUtil.setInt( 1, nId );
            daoUtil.executeQuery( );
            CandidateIdentityHistory candidateIdentityHistory = null;

            if ( daoUtil.next( ) )
            {
                candidateIdentityHistory = this.getCandidateIdentityHistory( daoUtil );
            }

            return Optional.ofNullable( candidateIdentityHistory );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<CandidateIdentityHistory> selectByWfHistory( int nWfResourceHistoryId, Plugin plugin )
    {
        try ( final DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_WF_RESOURCE_ID, plugin ) )
        {
            daoUtil.setInt( 1, nWfResourceHistoryId );
            daoUtil.executeQuery( );
            CandidateIdentityHistory candidateIdentityHistory = null;

            if ( daoUtil.next( ) )
            {
                candidateIdentityHistory = this.getCandidateIdentityHistory( daoUtil );
            }
            return Optional.ofNullable( candidateIdentityHistory );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey, Plugin plugin )
    {
        try ( final DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteList( final List<Integer> idList, Plugin plugin )
    {
        if ( CollectionUtils.isNotEmpty( idList ) )
        {
            final String query = SQL_QUERY_DELETE_LISTS.replace( ID_LIST, idList.stream( ).map( String::valueOf ).collect( Collectors.joining( "," ) ) );
            try ( final DAOUtil daoUtil = new DAOUtil( query, plugin ) )
            {
                daoUtil.executeUpdate( );
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void update( CandidateIdentityHistory candidateIdentityHistory, Plugin plugin )
    {
        try ( final DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;

            daoUtil.setString( nIndex++, candidateIdentityHistory.getStatus( ) );
            daoUtil.setString( nIndex++, candidateIdentityHistory.getComment( ) );
            daoUtil.setInt( nIndex, candidateIdentityHistory.getWfResourceHistoryId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<CandidateIdentityHistory> selectAll( int nCandidateIdentityId, Plugin plugin )
    {
        final List<CandidateIdentityHistory> histories = new ArrayList<>( );
        try ( final DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL, plugin ) )
        {
            daoUtil.setInt( 1, nCandidateIdentityId );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                histories.add( this.getCandidateIdentityHistory( daoUtil ) );
            }

            return histories;
        }
    }

    private CandidateIdentityHistory getCandidateIdentityHistory( final DAOUtil daoUtil )
    {
        final CandidateIdentityHistory history = new CandidateIdentityHistory( );
        int nIndex = 1;
        history.setId( daoUtil.getInt( nIndex++ ) );
        history.setWfResourceHistoryId( daoUtil.getInt( nIndex++ ) );
        history.setStatus( daoUtil.getString( nIndex++ ) );
        history.setComment( daoUtil.getString( nIndex++ ) );
        history.setCandidateIdentityId( daoUtil.getInt( nIndex ) );
        return history;
    }
}
