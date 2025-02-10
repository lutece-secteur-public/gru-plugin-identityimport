package fr.paris.lutece.plugins.identityimport.service;

import fr.paris.lutece.plugins.identityimport.business.Client;
import fr.paris.lutece.plugins.identityimport.business.ClientHome;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ImportClientService {

    private static ImportClientService _instance;

    public static ImportClientService instance() {
        if (_instance == null) {
            _instance = new ImportClientService();
        }
        return _instance;
    }

    private ImportClientService() {
    }

    /**
     * Lors de l'appel des API du site import, au moins un des deux headers "code appli" ou "code client" est obligatoire.
     * <ul>
     *     <li>si aucun code n'est envoyé, renvoyer une erreur</li>
     *     <li>si le code appli existe dans les entêtes, utiliser ce code appli pour retrouver l'importateur ("le client d'import")</li>
     *     <ul><li>si le code client est également envoyé, vérifier qu'il correspond bien au code client défini pour l'importateur</li></ul>
     *     <li>si le code appli n'est pas dans les headers, utiliser le code_client envoyé dans les entêtes pour retrouver l'importateur</li>
     * </ul>
     * @param appCode le code application
     * @param clientCode le code client
     * @return l'importateur ("client d'import")
     */
    public Client getClient(final String appCode, final String clientCode) throws IdentityStoreException {
        if (StringUtils.isAllBlank(appCode, clientCode)) {
            throw new IdentityStoreException("You must provide a client_code or an application_code.");
        }

        if (StringUtils.isNotBlank(appCode)) {
            final List<Client> clients = ClientHome.getClientsListByAppCode(appCode);
            if (clients.isEmpty()) {
                throw new IdentityStoreException("No client found.");
            }

            if (StringUtils.isNotBlank(clientCode)) {
                final Client client = clients.stream().filter(c -> c.getClientCode().equals(clientCode)).findFirst().orElse(null);
                if (client != null) {
                    return client;
                } else {
                    throw new IdentityStoreException("Provided application_code and client_code are not correlating.");
                }
            } else {
                if (clients.size() == 1) {
                    return clients.get(0);
                } else {
                    throw new IdentityStoreException("Multiple client found for provided application_code. Please specify a client_code.");
                }
            }
        } else {
            final Optional<Client> client = ClientHome.findByClientCode(clientCode);
            if (client.isPresent()) {
                return client.get();
            } else {
                throw new IdentityStoreException("No client found for provided client_code.");
            }
        }
    }

    public List<Client> getClients() {
        return ClientHome.getClientsList();
    }

    public Client getClient( final int clientId ) throws IdentityStoreException {
        final List<Client> clientsListByIds = ClientHome.getClientsListByIds( Collections.singletonList( clientId ) );
        if ( clientsListByIds == null || clientsListByIds.size( ) != 1 )
        {
            throw new IdentityStoreException("No client found for provided client id.");
        } else {
            return clientsListByIds.get( 0 );
        }
    }
}
