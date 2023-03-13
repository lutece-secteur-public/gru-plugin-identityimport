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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class provides Data Access methods for CandidateIdentityAttribute objects
 */
public final class CandidateIdentityAttributeDAO implements ICandidateIdentityAttributeDAO
{
    // Constants
	private static final String SQL_QUERY_SELECT = "SELECT id_candidate_identity_attribute, id_candidate_identity, code, value, cert_process, cert_date FROM identityimport_candidate_identity_attribute WHERE id_candidate_identity_attribute = ?";
	private static final String SQL_QUERY_SELECT_ALL = "SELECT id_candidate_identity_attribute, id_candidate_identity, code, value, cert_process, cert_date FROM identityimport_candidate_identity_attribute WHERE id_candidate_identity = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO identityimport_candidate_identity_attribute ( id_candidate_identity, code, value, cert_process, cert_date ) VALUES ( ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM identityimport_candidate_identity_attribute WHERE id_candidate_identity_attribute = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE identityimport_candidate_identity_attribute SET code = ?, value = ?, cert_process = ?, cert_date = ? WHERE id_candidate_identity_attribute = ?";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_candidate_identity_attribute FROM identityimport_candidate_identity_attribute where id_candidate_identity = ? ";
    private static final String SQL_QUERY_SELECTALL_BY_IDS = "SELECT id_candidate_identity_attribute, id_candidate_identity, code, value, cert_date, cert_process FROM identityimport_candidate_identity_attribute WHERE id_candidate_identity_attribute IN (  ";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( CandidateIdentityAttribute candidateIdentityAttribute, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, candidateIdentityAttribute.getIdIdentity( ) );
            daoUtil.setString( nIndex++, candidateIdentityAttribute.getCode( ) );
            daoUtil.setString( nIndex++, candidateIdentityAttribute.getValue( ) );
            daoUtil.setString( nIndex++, candidateIdentityAttribute.getCertProcess( ) );
            daoUtil.setDate( nIndex++, candidateIdentityAttribute.getCertDate( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                candidateIdentityAttribute.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<CandidateIdentityAttribute> load( int nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );
            CandidateIdentityAttribute candidateIdentityAttribute = null;

            if ( daoUtil.next( ) )
            {
                candidateIdentityAttribute = new CandidateIdentityAttribute( );
                int nIndex = 1;

                candidateIdentityAttribute.setId( daoUtil.getInt( nIndex++ ) );
                candidateIdentityAttribute.setIdIdentity( daoUtil.getInt( nIndex++ ) );
                candidateIdentityAttribute.setCode( daoUtil.getString( nIndex++ ) );
                candidateIdentityAttribute.setValue( daoUtil.getString( nIndex++ ) );
                candidateIdentityAttribute.setCertProcess( daoUtil.getString( nIndex++ ) );
                candidateIdentityAttribute.setCertDate( daoUtil.getDate( nIndex ) );
            }

            return Optional.ofNullable( candidateIdentityAttribute );
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
    public void store( CandidateIdentityAttribute candidateIdentityAttribute, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;

            daoUtil.setString( nIndex++, candidateIdentityAttribute.getCode( ) );
            daoUtil.setString( nIndex++, candidateIdentityAttribute.getValue( ) );
            daoUtil.setString( nIndex++, candidateIdentityAttribute.getCertProcess( ) );
            daoUtil.setDate( nIndex++, candidateIdentityAttribute.getCertDate( ) );
            daoUtil.setInt( nIndex, candidateIdentityAttribute.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<CandidateIdentityAttribute> selectCandidateIdentityAttributesList( int idIdentity, Plugin plugin )
    {
        List<CandidateIdentityAttribute> candidateIdentityAttributeList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL, plugin ) )
        {
        	daoUtil.setInt( 1, idIdentity);
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
            	CandidateIdentityAttribute candidateIdentityAttribute = new CandidateIdentityAttribute( );
                int nIndex = 1;

                candidateIdentityAttribute.setId( daoUtil.getInt( nIndex++ ) );
                candidateIdentityAttribute.setIdIdentity( daoUtil.getInt( nIndex++ ) );
                candidateIdentityAttribute.setCode( daoUtil.getString( nIndex++ ) );
                candidateIdentityAttribute.setValue( daoUtil.getString( nIndex++ ) );
                candidateIdentityAttribute.setCertProcess( daoUtil.getString( nIndex++ ) );
                candidateIdentityAttribute.setCertDate( daoUtil.getDate( nIndex ) );

                candidateIdentityAttributeList.add( candidateIdentityAttribute );
            }

            return candidateIdentityAttributeList;
        }
    }

    
    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdCandidateIdentityAttributesList( int idIdentity, Plugin plugin )
    {
        List<Integer> candidateIdentityAttributeList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin ) )
        {
        	daoUtil.setInt( 1, idIdentity);
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                candidateIdentityAttributeList.add( daoUtil.getInt( 1 ) );
            }

            return candidateIdentityAttributeList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<CandidateIdentityAttribute> selectCandidateIdentityAttributesListByIds( Plugin plugin, List<Integer> listIds )
    {
        List<CandidateIdentityAttribute> candidateIdentityAttributeList = new ArrayList<>( );

        StringBuilder builder = new StringBuilder( );

        if ( !listIds.isEmpty( ) )
        {
            for ( int i = 0; i < listIds.size( ); i++ )
            {
                builder.append( "?," );
            }

            String placeHolders = builder.deleteCharAt( builder.length( ) - 1 ).toString( );
            String stmt = SQL_QUERY_SELECTALL_BY_IDS + placeHolders + ")";

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
                    CandidateIdentityAttribute candidateIdentityAttribute = new CandidateIdentityAttribute( );
                    int nIndex = 1;

                    candidateIdentityAttribute.setId( daoUtil.getInt( nIndex++ ) );
                    candidateIdentityAttribute.setIdIdentity( daoUtil.getInt( nIndex++ ) );
                    candidateIdentityAttribute.setCode( daoUtil.getString( nIndex++ ) );
                    candidateIdentityAttribute.setValue( daoUtil.getString( nIndex++ ) );
                    candidateIdentityAttribute.setCertDate( daoUtil.getDate( nIndex++ ) );
                    candidateIdentityAttribute.setCertProcess( daoUtil.getString( nIndex ) );

                    candidateIdentityAttributeList.add( candidateIdentityAttribute );
                }

                daoUtil.free( );

            }
        }
        return candidateIdentityAttributeList;

    }
}
