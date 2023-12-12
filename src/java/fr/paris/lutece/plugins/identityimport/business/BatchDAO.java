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

import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class provides Data Access methods for Batch objects
 */
public final class BatchDAO implements IBatchDAO
{
    private static final String BATCH_SELECT_FIELDS = "batch.id_batch, batch.reference, batch.date, batch.user, batch.app_code, batch.comment";

    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT " + BATCH_SELECT_FIELDS + " FROM identityimport_batch batch WHERE id_batch = ?";
    private static final String SQL_QUERY_SELECT_EXPIRED_BATCHES = "SELECT " + BATCH_SELECT_FIELDS + " FROM identityimport_batch batch"
            + " JOIN identityimport_client client ON client.app_code = batch.app_code JOIN workflow_resource_workflow r ON r.id_resource = batch.id_batch AND r.resource_type = 'IDENTITYIMPORT_BATCH_RESOURCE'"
            + " WHERE DATE_ADD(batch.date, INTERVAL client.data_retention_period_in_months MONTH ) < CURRENT_DATE AND r.id_state = 3";
    private static final String SQL_QUERY_SELECT_BY_REFERENCE = "SELECT " + BATCH_SELECT_FIELDS + " FROM identityimport_batch batch WHERE reference = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO identityimport_batch ( reference, date, user, app_code, comment ) VALUES ( ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM identityimport_batch WHERE id_batch = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE identityimport_batch SET reference = ?, date = ?, user = ?, app_code = ?, comment = ? WHERE id_batch = ?";
    private static final String SQL_QUERY_PURGE = "";
    private static final String SQL_QUERY_SELECTSTATES = "SELECT ws.id_state, ws.name, ws.description, COUNT(wr.id_resource), ws.display_order as batch_count FROM workflow_state ws LEFT JOIN workflow_resource_workflow wr ON wr.id_state = ws.id_state AND wr.resource_type = 'IDENTITYIMPORT_BATCH_RESOURCE' WHERE ws.id_workflow = 1 GROUP BY ws.id_state, ws.name, ws.description";
    private static final String SQL_QUERY_SELECTSTATES_BY_APP_CODE = "SELECT ws.id_state, ws.name, ws.description, COUNT(b.app_code) as batch_count FROM workflow_state ws LEFT JOIN workflow_resource_workflow wr ON wr.id_state = ws.id_state AND wr.resource_type = 'IDENTITYIMPORT_BATCH_RESOURCE' LEFT JOIN identityimport_batch b ON b.id_batch = wr.id_resource AND b.app_code = '${app_code}' WHERE ws.id_workflow = 1 GROUP BY ws.id_state, ws.name, ws.description";
    private static final String SQL_QUERY_SELECTSTATE_BY_BATCH_ID = "SELECT ws.id_state, ws.name, ws.description, 1 as batch_count FROM workflow_resource_workflow wr LEFT JOIN workflow_state ws ON ws.id_state = wr.id_state LEFT JOIN identityimport_batch b ON b.id_batch = wr.id_resource WHERE ws.id_workflow = 1 AND wr.resource_type = 'IDENTITYIMPORT_BATCH_RESOURCE' AND b.id_batch = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT " + BATCH_SELECT_FIELDS + " FROM identityimport_batch batch";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT b.id_batch FROM identityimport_batch b ";
    private static final String SQL_QUERY_SELECTALL_ID_BY_STATE = "JOIN workflow_resource_workflow wr ON wr.id_resource = b.id_batch AND wr.resource_type = 'IDENTITYIMPORT_BATCH_RESOURCE' WHERE wr.id_state = ${id_state} ";
    private static final String SQL_QUERY_SELECTALL_ID_BY_APP_CODE = "b.app_code = ${app_code}";
    private static final String SQL_QUERY_SELECTALL_BY_IDS = "SELECT " + BATCH_SELECT_FIELDS + " FROM identityimport_batch batch WHERE id_batch IN (  ";
    private static final String SQL_QUERY_COUNT_IDENTITIES = "SELECT count(identity.id_resource) FROM workflow_resource_workflow identity WHERE identity.resource_type = 'IDENTITYIMPORT_CANDIDATE_RESOURCE' and identity.id_external_parent = ?";

    private static final String SQL_QUERY_SELECT_BATCH_HISTORY = "SELECT a.name, a.description, h.creation_date, h.user_access_code FROM workflow_resource_history h JOIN workflow_action a ON a.id_action = h.id_action WHERE h.resource_type = 'IDENTITYIMPORT_BATCH_RESOURCE' AND h.id_resource = ?";

    private static final String SQL_QUERY_SELECT_INITIAL_STATE_BATCHES = "SELECT " + BATCH_SELECT_FIELDS
            + " FROM identityimport_batch batch JOIN workflow_resource_workflow r ON batch.id_batch = r.id_resource AND r.resource_type = 'IDENTITYIMPORT_BATCH_RESOURCE' JOIN workflow_state s ON s.id_state = r.id_state WHERE s.is_initial_state = 1 ";
    private static final String SQL_QUERY_SELECT_CLOSABLE_BATCHES = "SELECT " + BATCH_SELECT_FIELDS
            + " FROM identityimport_batch batch JOIN workflow_resource_workflow r ON batch.id_batch = r.id_resource AND r.resource_type = 'IDENTITYIMPORT_BATCH_RESOURCE' "
            + " WHERE r.id_state = 2 AND NOT EXISTS( SELECT 1 FROM workflow_resource_workflow candidate WHERE candidate.id_external_parent = batch.id_batch AND (candidate.id_state = 4 OR candidate.id_state = 7)) ";
    private static final String SQL_QUERY_SELECT_BATCHES_INITIAL_ACTION_ID = "SELECT sb.id_action FROM workflow_action_state_before sb JOIN workflow_state s ON s.id_state = sb.id_state_before AND s.is_initial_state = 1 JOIN workflow_resource_workflow r ON r.id_workflow = s.id_workflow AND r.resource_type = 'IDENTITYIMPORT_BATCH_RESOURCE'";
    private static final String SQL_QUERY_SELECT_BATCHES_IN_TREATMENT_ACTION_ID = "SELECT sb.id_action FROM workflow_action_state_before sb WHERE sb.id_state_before = 2";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Batch batch, Plugin plugin )
    {
        try ( final DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, batch.getReference( ) );
            daoUtil.setDate( nIndex++, batch.getDate( ) );
            daoUtil.setString( nIndex++, batch.getUser( ) );
            daoUtil.setString( nIndex++, batch.getAppCode( ) );
            daoUtil.setString( nIndex++, batch.getComment( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                batch.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<Batch> load( int nKey, Plugin plugin )
    {
        try ( final DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                return Optional.of( getBatch( daoUtil ) );
            }

            return Optional.empty( );
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
    public void store( Batch batch, Plugin plugin )
    {
        try ( final DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;

            daoUtil.setString( nIndex++, batch.getReference( ) );
            daoUtil.setDate( nIndex++, batch.getDate( ) );
            daoUtil.setString( nIndex++, batch.getUser( ) );
            daoUtil.setString( nIndex++, batch.getAppCode( ) );
            daoUtil.setString( nIndex++, batch.getComment( ) );
            daoUtil.setInt( nIndex, batch.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Batch> selectBatchsList( Plugin plugin )
    {
        List<Batch> batchList = new ArrayList<>( );
        try ( final DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                batchList.add( getBatch( daoUtil ) );
            }

            return batchList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<ResourceState> selectBatchStates( final String filterAppCode, final Plugin plugin )
    {
        final List<ResourceState> states = new ArrayList<>( );

        String query = SQL_QUERY_SELECTSTATES;
        if ( filterAppCode != null && !filterAppCode.isEmpty( ) )
        {
            query = SQL_QUERY_SELECTSTATES_BY_APP_CODE.replace( "${app_code}", filterAppCode );
        }

        try ( final DAOUtil daoUtil = new DAOUtil( query, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                final ResourceState state = new ResourceState( );
                state.setId( daoUtil.getInt( 1 ) );
                state.setName( daoUtil.getString( 2 ) );
                state.setDescription( daoUtil.getString( 3 ) );
                state.setResourceCount( daoUtil.getInt( 4 ) );
                state.setOrder( daoUtil.getInt( 5 ) );
                states.add( state );
            }

            return states;
        }
    }

    @Override
    public ResourceState selectBatchState( final int batchId, final Plugin plugin )
    {
        try ( final DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTSTATE_BY_BATCH_ID, plugin ) )
        {
            daoUtil.setInt( 1, batchId );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                final ResourceState state = new ResourceState( );
                state.setId( daoUtil.getInt( 1 ) );
                state.setName( daoUtil.getString( 2 ) );
                state.setDescription( daoUtil.getString( 3 ) );
                state.setResourceCount( daoUtil.getInt( 4 ) );
                return state;
            }
        }
        return null;
    }

    @Override
    public int countIdentities( int resourceId, Plugin plugin )
    {
        try ( final DAOUtil daoUtil = new DAOUtil( SQL_QUERY_COUNT_IDENTITIES, plugin ) )
        {
            daoUtil.setInt( 1, resourceId );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                return daoUtil.getInt( 1 );
            }

            return 0;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Batch> selectExpiredBatchsList( final int batchLimit, final Plugin plugin )
    {
        final List<Batch> batchList = new ArrayList<>( );
        String query = SQL_QUERY_SELECT_EXPIRED_BATCHES;
        if ( batchLimit > 0 )
        {
            query += " LIMIT " + batchLimit;
        }

        try ( DAOUtil daoUtil = new DAOUtil( query, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                batchList.add( getBatch( daoUtil ) );
            }

            return batchList;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Batch> selectInitialStateBatches( final int batchLimit, final Plugin plugin )
    {
        final List<Batch> batchList = new ArrayList<>( );
        final StringBuilder query = new StringBuilder( SQL_QUERY_SELECT_INITIAL_STATE_BATCHES );
        if ( batchLimit > 0 )
        {
            query.append( " LIMIT " ).append( batchLimit );
        }
        try ( DAOUtil daoUtil = new DAOUtil( query.toString( ), plugin ) )
        {
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                batchList.add( getBatch( daoUtil ) );
            }
            return batchList;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Batch> selectClosableBatches( final int batchLimit, final Plugin plugin )
    {
        final List<Batch> batchList = new ArrayList<>( );
        final StringBuilder query = new StringBuilder( SQL_QUERY_SELECT_CLOSABLE_BATCHES );
        if ( batchLimit > 0 )
        {
            query.append( " LIMIT " ).append( batchLimit );
        }
        try ( DAOUtil daoUtil = new DAOUtil( query.toString( ), plugin ) )
        {
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                batchList.add( getBatch( daoUtil ) );
            }
            return batchList;
        }
    }

    @Override
    public Batch selectBatchByReference( final Plugin plugin, final String reference )
    {
        try ( final DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_REFERENCE, plugin ) )
        {
            daoUtil.setString( 1, reference );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                return getBatch( daoUtil );
            }

            return null;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdBatchsList( final Integer batchStateId, final String filterAppCode, Plugin plugin )
    {
        final List<Integer> batchList = new ArrayList<>( );
        String query = SQL_QUERY_SELECTALL_ID;

        if ( batchStateId != null && batchStateId > 0 )
        {
            query += SQL_QUERY_SELECTALL_ID_BY_STATE.replace( "${id_state}", String.valueOf( batchStateId ) );
            if ( filterAppCode != null && !filterAppCode.isEmpty( ) )
            {
                query += " AND " + SQL_QUERY_SELECTALL_ID_BY_APP_CODE.replace( "${app_code}", "'" + filterAppCode + "'" );
            }
        }
        else
            if ( filterAppCode != null && !filterAppCode.isEmpty( ) )
            {
                query += " WHERE " + SQL_QUERY_SELECTALL_ID_BY_APP_CODE.replace( "${app_code}", "'" + filterAppCode + "'" );
            }

        try ( final DAOUtil daoUtil = new DAOUtil( query, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                batchList.add( daoUtil.getInt( 1 ) );
            }

            return batchList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectBatchsReferenceList( Plugin plugin )
    {
        ReferenceList batchList = new ReferenceList( );
        try ( final DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                batchList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
            }

            return batchList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Batch> selectBatchsListByIds( Plugin plugin, List<Integer> listIds )
    {
        List<Batch> batchList = new ArrayList<>( );

        StringBuilder builder = new StringBuilder( );

        if ( !listIds.isEmpty( ) )
        {
            for ( int i = 0; i < listIds.size( ); i++ )
            {
                builder.append( "?," );
            }

            final String placeHolders = builder.deleteCharAt( builder.length( ) - 1 ).toString( );
            final String stmt = SQL_QUERY_SELECTALL_BY_IDS + placeHolders + ")";

            try ( final DAOUtil daoUtil = new DAOUtil( stmt, plugin ) )
            {
                int index = 1;
                for ( Integer n : listIds )
                {
                    daoUtil.setInt( index++, n );
                }

                daoUtil.executeQuery( );
                while ( daoUtil.next( ) )
                {
                    batchList.add( getBatch( daoUtil ) );
                }

                daoUtil.free( );

            }
        }
        return batchList;

    }

    @Override
    public List<ResourceHistory> getBatchHistory( final int batchId, final Plugin plugin )
    {
        final List<ResourceHistory> list = new ArrayList<>( );
        try ( final DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BATCH_HISTORY, plugin ) )
        {
            daoUtil.setInt( 1, batchId );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                final ResourceHistory history = new ResourceHistory( );
                final Action action = new Action( );
                action.setName( daoUtil.getString( 1 ) );
                action.setDescription( daoUtil.getString( 2 ) );
                history.setAction( action );
                history.setCreationDate( daoUtil.getTimestamp( 3 ) );
                history.setUserAccessCode( daoUtil.getString( 4 ) );
                list.add( history );
            }
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    public int getBatchInitialActionId( final Plugin plugin ) throws IdentityStoreException
    {
        try ( final DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BATCHES_INITIAL_ACTION_ID, plugin ) )
        {
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                return daoUtil.getInt( 1 );
            }
        }
        throw new IdentityStoreException( "The initial state for batches doesn't have any linked action." );
    }

    /**
     * {@inheritDoc}
     */
    public int getBatchInTreatmentActionId( final Plugin plugin ) throws IdentityStoreException
    {
        try ( final DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BATCHES_IN_TREATMENT_ACTION_ID, plugin ) )
        {
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                return daoUtil.getInt( 1 );
            }
        }
        throw new IdentityStoreException( "The in treatment state for batches doesn't have any linked action." );
    }

    private Batch getBatch( final DAOUtil daoUtil )
    {
        final Batch batch = new Batch( );
        int nIndex = 1;
        batch.setId( daoUtil.getInt( nIndex++ ) );
        batch.setReference( daoUtil.getString( nIndex++ ) );
        batch.setDate( daoUtil.getDate( nIndex++ ) );
        batch.setUser( daoUtil.getString( nIndex++ ) );
        batch.setAppCode( daoUtil.getString( nIndex++ ) );
        batch.setComment( daoUtil.getString( nIndex ) );
        return batch;
    }
}
