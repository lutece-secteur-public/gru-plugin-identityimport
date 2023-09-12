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
import org.apache.commons.lang3.StringUtils;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class provides Data Access methods for CandidateIdentity objects
 */
public final class CandidateIdentityDAO implements ICandidateIdentityDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT_ALL = "WITH history AS ( select rh.id_resource, icih.status, icih.comment, max(creation_date) from workflow_resource_history rh left join identityimport_candidate_identity_history icih on icih.id_wf_resource_history = rh.id_history group by rh.id_resource ,icih.status, icih.comment ) " +
            "SELECT i.id_candidate_identity, i.id_batch, i.connection_id, i.customer_id, i.client_id, ib.app_code, history.status, history.comment FROM identityimport_candidate_identity i JOIN identityimport_batch ib ON i.id_batch = ib.id_batch LEFT JOIN history ON history.id_resource = i.id_candidate_identity";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_ALL + " WHERE i.id_candidate_identity = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO identityimport_candidate_identity ( id_batch, connection_id, customer_id, client_id) VALUES ( ?, ?, ?, ?) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM identityimport_candidate_identity WHERE id_candidate_identity = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE identityimport_candidate_identity SET id_candidate_identity = ?, id_batch = ?, connection_id = ?, customer_id = ?, client_id = ? WHERE id_candidate_identity = ?";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_candidate_identity FROM identityimport_candidate_identity where id_batch = ?";
    private static final String SQL_QUERY_SELECTALL_BY_IDS = SQL_QUERY_SELECT_ALL + " WHERE i.id_candidate_identity IN (  ";
    private static final String EXTERNAL_IDS = "{external_ids}";
    private static final String SQL_QUERY_EXISTS_BY_IDS = "SELECT EXISTS(SELECT 1 FROM identityimport_candidate_identity ci JOIN identityimport_batch b ON ci.id_batch = b.id_batch WHERE b.reference = ? AND ci.client_id IN ("
            + EXTERNAL_IDS + "))";
    private static final String SQL_QUERY_SELECTALL_BY_IDS_END = ")";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( CandidateIdentity candidateIdentity, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, candidateIdentity.getIdBatch( ) );
            daoUtil.setString( nIndex++, candidateIdentity.getConnectionId( ) );
            daoUtil.setString( nIndex++, candidateIdentity.getCustomerId( ) );
            daoUtil.setString( nIndex, candidateIdentity.getExternalCustomerId( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                candidateIdentity.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<CandidateIdentity> load( int nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );
            CandidateIdentity candidateIdentity = null;

            if ( daoUtil.next( ) )
            {
                candidateIdentity = this.getCandidateIdentity(daoUtil);
            }

            return Optional.ofNullable( candidateIdentity );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( CandidateIdentity candidateIdentity, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;

            daoUtil.setInt( nIndex++, candidateIdentity.getId( ) );
            daoUtil.setInt( nIndex++, candidateIdentity.getIdBatch( ) );
            daoUtil.setString( nIndex++, candidateIdentity.getConnectionId( ) );
            daoUtil.setString( nIndex++, candidateIdentity.getCustomerId( ) );
            daoUtil.setString( nIndex++, candidateIdentity.getExternalCustomerId( ) );
            daoUtil.setInt( nIndex, candidateIdentity.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<CandidateIdentity> selectCandidateIdentitiesList( Plugin plugin )
    {
        List<CandidateIdentity> candidateIdentityList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                candidateIdentityList.add( this.getCandidateIdentity(daoUtil) );
            }

            return candidateIdentityList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdCandidateIdentitiesList( int nBatchId, Plugin plugin )
    {
        List<Integer> candidateIdentityList = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin ) )
        {
            daoUtil.setInt( 1, nBatchId );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                candidateIdentityList.add( daoUtil.getInt( 1 ) );
            }

            return candidateIdentityList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<CandidateIdentity> selectCandidateIdentitiesListByIds( Plugin plugin, List<Integer> listIds )
    {
        List<CandidateIdentity> candidateIdentityList = new ArrayList<>( );

        StringBuilder builder = new StringBuilder( );

        if ( !listIds.isEmpty( ) )
        {
            for ( int i = 0; i < listIds.size( ); i++ )
            {
                builder.append( "?," );
            }

            String placeHolders = builder.deleteCharAt( builder.length( ) - 1 ).toString( );
            String stmt = SQL_QUERY_SELECTALL_BY_IDS + placeHolders + SQL_QUERY_SELECTALL_BY_IDS_END;

            try ( DAOUtil daoUtil = new DAOUtil( stmt, plugin ) )
            {
                int index = 1;
                for ( Integer n : listIds )
                {
                    daoUtil.setInt( index++, n );
                }

                daoUtil.executeQuery( );
                while ( daoUtil.next( ) )
                {
                    candidateIdentityList.add( this.getCandidateIdentity(daoUtil) );
                }

                daoUtil.free( );

            }
        }
        return candidateIdentityList;

    }

    private CandidateIdentity getCandidateIdentity(DAOUtil daoUtil) {
        CandidateIdentity candidateIdentity = new CandidateIdentity( );
        int nIndex = 1;

        candidateIdentity.setId( daoUtil.getInt( nIndex++ ) );
        candidateIdentity.setIdBatch( daoUtil.getInt( nIndex++ ) );
        candidateIdentity.setConnectionId( daoUtil.getString( nIndex++ ) );
        candidateIdentity.setCustomerId( daoUtil.getString( nIndex++ ) );
        candidateIdentity.setExternalCustomerId( daoUtil.getString( nIndex++ ) );
        candidateIdentity.setClientAppCode( daoUtil.getString( nIndex++ ) );
        candidateIdentity.setStatus(StringUtils.defaultString(daoUtil.getString(nIndex), ""));
        return candidateIdentity;
    }

    @Override
    public boolean checkIfOneExists( final Plugin plugin, final String batchReference, List<String> externalIds )
    {
        if ( !externalIds.isEmpty( ) )
        {
            final String query = SQL_QUERY_EXISTS_BY_IDS.replace( EXTERNAL_IDS,
                    externalIds.stream( ).map( s -> "'" + s + "'" ).collect( Collectors.joining( "," ) ) );
            try ( final DAOUtil daoUtil = new DAOUtil( query, plugin ) )
            {
                daoUtil.setString( 1, batchReference );
                daoUtil.executeQuery( );
                if ( daoUtil.next( ) )
                {
                    return daoUtil.getBoolean( 1 );
                }
            }
        }
        return false;
    }
}
