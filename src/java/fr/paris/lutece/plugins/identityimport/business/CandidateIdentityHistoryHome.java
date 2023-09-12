package fr.paris.lutece.plugins.identityimport.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;
import java.util.Optional;

public class CandidateIdentityHistoryHome {
    // Static variable pointed at the DAO instance
    private static final ICandidateIdentityHistoryDAO _dao = SpringContextService.getBean( "identityimport.candidateIdentityHistoryDao" );
    private static final Plugin _plugin = PluginService.getPlugin( "identityimport" );

    /**
     * Insert a new record in the table.
     *
     * @param candidateIdentityHistory
     *            instance of the candidateIdentityHistory object to insert
     */
    public static void insert( CandidateIdentityHistory candidateIdentityHistory) {
        _dao.insert(candidateIdentityHistory, _plugin);
    }

    /**
     * Update the record in the table
     *
     * @param candidateIdentityHistory
     *            the reference of the candidateIdentityHistory to update
     */
    public static void update( CandidateIdentityHistory candidateIdentityHistory ){
        _dao.update(candidateIdentityHistory, _plugin);
    }

    /**
     * Select a record from the table
     *
     * @param nWfResourceHistoryId
     *            The identifier to select
     */
    public static Optional<CandidateIdentityHistory> selectByWfHistory(int nWfResourceHistoryId){
        return _dao.selectByWfHistory(nWfResourceHistoryId, _plugin);
    }

    /**
     * Select a record from the table
     *
     * @param nId
     *            The identifier to select
     */
    public static Optional<CandidateIdentityHistory> select( int nId ){
        return _dao.select(nId, _plugin);
    }

    /**
     * Select a record from the table
     *
     * @param nCandidateIdentityId
     *            The identifier to select
     */
    public static List<CandidateIdentityHistory> selectAll(int nCandidateIdentityId ){
        return _dao.selectAll(nCandidateIdentityId, _plugin);
    }

    /**
     * Delete a record from the table
     *
     * @param nKey
     *            The identifier of the CandidateIdentity to delete
     */
    public static void delete( int nKey ){
        _dao.delete(nKey, _plugin);
    }
}
