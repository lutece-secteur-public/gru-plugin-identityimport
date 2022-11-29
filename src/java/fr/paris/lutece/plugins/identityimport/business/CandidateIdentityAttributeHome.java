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
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;
import java.util.Optional;

/**
 * This class provides instances management methods (create, find, ...) for CandidateIdentityAttribute objects
 */
public final class CandidateIdentityAttributeHome
{
    // Static variable pointed at the DAO instance
    private static ICandidateIdentityAttributeDAO _dao = SpringContextService.getBean( "identityimport.candidateIdentityAttributeDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "identityimport" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private CandidateIdentityAttributeHome( )
    {
    }

    /**
     * Create an instance of the candidateIdentityAttribute class
     * 
     * @param candidateIdentityAttribute
     *            The instance of the CandidateIdentityAttribute which contains the informations to store
     * @return The instance of candidateIdentityAttribute which has been created with its primary key.
     */
    public static CandidateIdentityAttribute create( CandidateIdentityAttribute candidateIdentityAttribute )
    {
        _dao.insert( candidateIdentityAttribute, _plugin );

        return candidateIdentityAttribute;
    }

    /**
     * Update of the candidateIdentityAttribute which is specified in parameter
     * 
     * @param candidateIdentityAttribute
     *            The instance of the CandidateIdentityAttribute which contains the data to store
     * @return The instance of the candidateIdentityAttribute which has been updated
     */
    public static CandidateIdentityAttribute update( CandidateIdentityAttribute candidateIdentityAttribute )
    {
        _dao.store( candidateIdentityAttribute, _plugin );

        return candidateIdentityAttribute;
    }

    /**
     * Remove the candidateIdentityAttribute whose identifier is specified in parameter
     * 
     * @param nKey
     *            The candidateIdentityAttribute Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a candidateIdentityAttribute whose identifier is specified in parameter
     * 
     * @param nKey
     *            The candidateIdentityAttribute primary key
     * @return an instance of CandidateIdentityAttribute
     */
    public static Optional<CandidateIdentityAttribute> findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the id of all the candidateIdentityAttribute objects and returns them as a list
     * 
     * @return the list which contains the id of all the candidateIdentityAttribute objects
     */
    public static List<Integer> getIdCandidateIdentityAttributesList( int idIdentity )
    {
        return _dao.selectIdCandidateIdentityAttributesList( idIdentity, _plugin );
    }

    /**
     * Load the id of all the candidateIdentityAttribute objects and returns them as a list
     * 
     * @return the list which contains the id of all the candidateIdentityAttribute objects
     */
    public static List<CandidateIdentityAttribute> getCandidateIdentityAttributesList( int idIdentity )
    {
        return _dao.selectCandidateIdentityAttributesList( idIdentity, _plugin );
    }
    
    /**
     * Load the data of all the avant objects and returns them as a list
     * 
     * @param listIds
     *            liste of ids
     * @return the list which contains the data of all the avant objects
     */
    public static List<CandidateIdentityAttribute> getCandidateIdentityAttributesListByIds( List<Integer> listIds )
    {
        return _dao.selectCandidateIdentityAttributesListByIds( _plugin, listIds );
    }

}
