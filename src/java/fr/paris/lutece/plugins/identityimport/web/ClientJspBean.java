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
package fr.paris.lutece.plugins.identityimport.web;

import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.util.html.AbstractPaginator;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import fr.paris.lutece.plugins.identityimport.business.Client;
import fr.paris.lutece.plugins.identityimport.business.ClientHome;

/**
 * This class provides the user interface to manage Client features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageClients.jsp", controllerPath = "jsp/admin/plugins/identityimport/", right = "IDENTITYIMPORT_BATCH_MANAGEMENT" )
public class ClientJspBean extends AbstractManageItemsJspBean<Integer, Client>
{
    // Templates
    private static final String TEMPLATE_MANAGE_CLIENTS = "/admin/plugins/identityimport/manage_clients.html";
    private static final String TEMPLATE_CREATE_CLIENT = "/admin/plugins/identityimport/create_client.html";
    private static final String TEMPLATE_MODIFY_CLIENT = "/admin/plugins/identityimport/modify_client.html";

    // Parameters
    private static final String PARAMETER_ID_CLIENT = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_CLIENTS = "identityimport.manage_clients.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_CLIENT = "identityimport.modify_client.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_CLIENT = "identityimport.create_client.pageTitle";

    // Markers
    private static final String MARK_CLIENT_LIST = "client_list";
    private static final String MARK_CLIENT = "client";

    private static final String JSP_MANAGE_CLIENTS = "jsp/admin/plugins/identityimport/ManageClients.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_CLIENT = "identityimport.message.confirmRemoveClient";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "identityimport.model.entity.client.attribute.";

    // Views
    private static final String VIEW_MANAGE_CLIENTS = "manageClients";
    private static final String VIEW_CREATE_CLIENT = "createClient";
    private static final String VIEW_MODIFY_CLIENT = "modifyClient";

    // Actions
    private static final String ACTION_CREATE_CLIENT = "createClient";
    private static final String ACTION_MODIFY_CLIENT = "modifyClient";
    private static final String ACTION_REMOVE_CLIENT = "removeClient";
    private static final String ACTION_CONFIRM_REMOVE_CLIENT = "confirmRemoveClient";

    // Infos
    private static final String INFO_CLIENT_CREATED = "identityimport.info.client.created";
    private static final String INFO_CLIENT_UPDATED = "identityimport.info.client.updated";
    private static final String INFO_CLIENT_REMOVED = "identityimport.info.client.removed";

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";

    // Session variable to store working values
    private Client _client;
    private List<Integer> _listIdClients;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_CLIENTS, defaultView = true )
    public String getManageClients( HttpServletRequest request )
    {
        _client = null;

        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null || _listIdClients.isEmpty( ) )
        {
            _listIdClients = ClientHome.getIdClientsList( );
        }

        Map<String, Object> model = getPaginatedListModel( request, MARK_CLIENT_LIST, _listIdClients, JSP_MANAGE_CLIENTS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_CLIENTS, TEMPLATE_MANAGE_CLIENTS, model );
    }

    /**
     * Get Items from Ids list
     * 
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
    @Override
    List<Client> getItemsFromIds( List<Integer> listIds )
    {
        final List<Client> listClient = ClientHome.getClientsListByIds( listIds );

        // keep original order
        return listClient.stream( ).sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getId( ) ) ) ).collect( Collectors.toList( ) );
    }

    /**
     * reset the _listIdClients list
     */
    public void resetListId( )
    {
        _listIdClients = new ArrayList<>( );
    }

    /**
     * Returns the form to create a client
     *
     * @param request
     *            The Http request
     * @return the html code of the client form
     */
    @View( VIEW_CREATE_CLIENT )
    public String getCreateClient( HttpServletRequest request )
    {
        _client = ( _client != null ) ? _client : new Client( );

        Map<String, Object> model = getModel( );
        model.put( MARK_CLIENT, _client );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_CLIENT ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_CLIENT, TEMPLATE_CREATE_CLIENT, model );
    }

    /**
     * Process the data capture form of a new client
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_CLIENT )
    public String doCreateClient( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _client, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_CLIENT ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _client, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_CLIENT );
        }

        ClientHome.create( _client );
        addInfo( INFO_CLIENT_CREATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_CLIENTS );
    }

    /**
     * Manages the removal form of a client whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_CLIENT )
    public String getConfirmRemoveClient( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CLIENT ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_CLIENT ) );
        url.addParameter( PARAMETER_ID_CLIENT, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_CLIENT, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a client
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage clients
     */
    @Action( ACTION_REMOVE_CLIENT )
    public String doRemoveClient( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CLIENT ) );

        ClientHome.remove( nId );
        addInfo( INFO_CLIENT_REMOVED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_CLIENTS );
    }

    /**
     * Returns the form to update info about a client
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_CLIENT )
    public String getModifyClient( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CLIENT ) );

        if ( _client == null || ( _client.getId( ) != nId ) )
        {
            Optional<Client> optClient = ClientHome.findByPrimaryKey( nId );
            _client = optClient.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_CLIENT, _client );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_CLIENT ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_CLIENT, TEMPLATE_MODIFY_CLIENT, model );
    }

    /**
     * Process the change form of a client
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_CLIENT )
    public String doModifyClient( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _client, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_CLIENT ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _client, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_CLIENT, PARAMETER_ID_CLIENT, _client.getId( ) );
        }

        ClientHome.update( _client );
        addInfo( INFO_CLIENT_UPDATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_CLIENTS );
    }
}
