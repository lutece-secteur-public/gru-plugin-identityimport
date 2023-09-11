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
    private static final String BATCH_SELECT_FIELDS = "id_batch, reference, date, user, app_code, comment";

    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT " + BATCH_SELECT_FIELDS + " FROM identityimport_batch WHERE id_batch = ?";
    private static final String SQL_QUERY_SELECT_BY_REFERENCE = "SELECT " + BATCH_SELECT_FIELDS + " FROM identityimport_batch WHERE reference = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO identityimport_batch ( reference, date, user, app_code, comment ) VALUES ( ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM identityimport_batch WHERE id_batch = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE identityimport_batch SET reference = ?, date = ?, user = ?, app_code = ?, comment = ? WHERE id_batch = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT " + BATCH_SELECT_FIELDS + " FROM identityimport_batch";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_batch FROM identityimport_batch";
    private static final String SQL_QUERY_SELECTALL_BY_IDS = "SELECT " + BATCH_SELECT_FIELDS + " FROM identityimport_batch WHERE id_batch IN (  ";

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
    public void store( Batch batch, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
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
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
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
        Batch batch = new Batch( );
        try ( final DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_REFERENCE, plugin ) )
        {
            daoUtil.setString( 1, reference );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                batch = getBatch( daoUtil );
            }

            return batch;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdBatchsList( Plugin plugin )
    {
        List<Integer> batchList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin ) )
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
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
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
