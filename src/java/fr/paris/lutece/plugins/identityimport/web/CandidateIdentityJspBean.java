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
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentity;
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentityHome;

/**
 * This class provides the user interface to manage CandidateIdentity features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageCandidateIdentitys.jsp", controllerPath = "jsp/admin/plugins/identityimport/", right = "IDENTITYIMPORT_CANDIDATE_IDENTITY_MANAGEMENT" )
public class CandidateIdentityJspBean extends AbstractManageCandidateIdentitiesJspBean <Integer, CandidateIdentity>
{
    // Templates
    private static final String TEMPLATE_MANAGE_CANDIDATEIDENTITYS = "/admin/plugins/identityimport/manage_candidateidentitys.html";
    private static final String TEMPLATE_CREATE_CANDIDATEIDENTITY = "/admin/plugins/identityimport/create_candidateidentity.html";
    private static final String TEMPLATE_MODIFY_CANDIDATEIDENTITY = "/admin/plugins/identityimport/modify_candidateidentity.html";

    // Parameters
    private static final String PARAMETER_ID_CANDIDATEIDENTITY = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_CANDIDATEIDENTITYS = "identityimport.manage_candidateidentitys.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_CANDIDATEIDENTITY = "identityimport.modify_candidateidentity.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_CANDIDATEIDENTITY = "identityimport.create_candidateidentity.pageTitle";

    // Markers
    private static final String MARK_CANDIDATEIDENTITY_LIST = "candidateidentity_list";
    private static final String MARK_CANDIDATEIDENTITY = "candidateidentity";

    private static final String JSP_MANAGE_CANDIDATEIDENTITYS = "jsp/admin/plugins/identityimport/ManageCandidateIdentitys.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_CANDIDATEIDENTITY = "identityimport.message.confirmRemoveCandidateIdentity";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "identityimport.model.entity.candidateidentity.attribute.";

    // Views
    private static final String VIEW_MANAGE_CANDIDATEIDENTITYS = "manageCandidateIdentitys";
    private static final String VIEW_CREATE_CANDIDATEIDENTITY = "createCandidateIdentity";
    private static final String VIEW_MODIFY_CANDIDATEIDENTITY = "modifyCandidateIdentity";

    // Actions
    private static final String ACTION_CREATE_CANDIDATEIDENTITY = "createCandidateIdentity";
    private static final String ACTION_MODIFY_CANDIDATEIDENTITY = "modifyCandidateIdentity";
    private static final String ACTION_REMOVE_CANDIDATEIDENTITY = "removeCandidateIdentity";
    private static final String ACTION_CONFIRM_REMOVE_CANDIDATEIDENTITY = "confirmRemoveCandidateIdentity";

    // Infos
    private static final String INFO_CANDIDATEIDENTITY_CREATED = "identityimport.info.candidateidentity.created";
    private static final String INFO_CANDIDATEIDENTITY_UPDATED = "identityimport.info.candidateidentity.updated";
    private static final String INFO_CANDIDATEIDENTITY_REMOVED = "identityimport.info.candidateidentity.removed";
    
    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";
    
    // Session variable to store working values
    private CandidateIdentity _candidateidentity;
    private List<Integer> _listIdCandidateIdentitys;
    
    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_CANDIDATEIDENTITYS, defaultView = true )
    public String getManageCandidateIdentitys( HttpServletRequest request )
    {
        _candidateidentity = null;
        
        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX) == null || _listIdCandidateIdentitys.isEmpty( ) )
        {
        	_listIdCandidateIdentitys = CandidateIdentityHome.getIdCandidateIdentitysList(  );
        }
        
        Map<String, Object> model = getPaginatedListModel( request, MARK_CANDIDATEIDENTITY_LIST, _listIdCandidateIdentitys, JSP_MANAGE_CANDIDATEIDENTITYS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_CANDIDATEIDENTITYS, TEMPLATE_MANAGE_CANDIDATEIDENTITYS, model );
    }

	/**
     * Get Items from Ids list
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
	@Override
	List<CandidateIdentity> getItemsFromIds( List<Integer> listIds ) 
	{
		List<CandidateIdentity> listCandidateIdentity = CandidateIdentityHome.getCandidateIdentitysListByIds( listIds );
		
		// keep original order
        return listCandidateIdentity.stream()
                 .sorted(Comparator.comparingInt( notif -> listIds.indexOf( notif.getId())))
                 .collect(Collectors.toList());
	}
    
    /**
    * reset the _listIdCandidateIdentitys list
    */
    public void resetListId( )
    {
    	_listIdCandidateIdentitys = new ArrayList<>( );
    }

    /**
     * Returns the form to create a candidateidentity
     *
     * @param request The Http request
     * @return the html code of the candidateidentity form
     */
    @View( VIEW_CREATE_CANDIDATEIDENTITY )
    public String getCreateCandidateIdentity( HttpServletRequest request )
    {
        _candidateidentity = ( _candidateidentity != null ) ? _candidateidentity : new CandidateIdentity(  );

        Map<String, Object> model = getModel(  );
        model.put( MARK_CANDIDATEIDENTITY, _candidateidentity );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_CANDIDATEIDENTITY ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_CANDIDATEIDENTITY, TEMPLATE_CREATE_CANDIDATEIDENTITY, model );
    }

    /**
     * Process the data capture form of a new candidateidentity
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_CANDIDATEIDENTITY )
    public String doCreateCandidateIdentity( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _candidateidentity, request, getLocale( ) );
        

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_CANDIDATEIDENTITY ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _candidateidentity, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_CANDIDATEIDENTITY );
        }

        CandidateIdentityHome.create( _candidateidentity );
        addInfo( INFO_CANDIDATEIDENTITY_CREATED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_CANDIDATEIDENTITYS );
    }

    /**
     * Manages the removal form of a candidateidentity whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_CANDIDATEIDENTITY )
    public String getConfirmRemoveCandidateIdentity( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CANDIDATEIDENTITY ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_CANDIDATEIDENTITY ) );
        url.addParameter( PARAMETER_ID_CANDIDATEIDENTITY, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_CANDIDATEIDENTITY, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a candidateidentity
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage candidateidentitys
     */
    @Action( ACTION_REMOVE_CANDIDATEIDENTITY )
    public String doRemoveCandidateIdentity( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CANDIDATEIDENTITY ) );
        
        
        CandidateIdentityHome.remove( nId );
        addInfo( INFO_CANDIDATEIDENTITY_REMOVED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_CANDIDATEIDENTITYS );
    }

    /**
     * Returns the form to update info about a candidateidentity
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_CANDIDATEIDENTITY )
    public String getModifyCandidateIdentity( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CANDIDATEIDENTITY ) );

        if ( _candidateidentity == null || ( _candidateidentity.getId(  ) != nId ) )
        {
            Optional<CandidateIdentity> optCandidateIdentity = CandidateIdentityHome.findByPrimaryKey( nId );
            _candidateidentity = optCandidateIdentity.orElseThrow( ( ) -> new AppException(ERROR_RESOURCE_NOT_FOUND ) );
        }


        Map<String, Object> model = getModel(  );
        model.put( MARK_CANDIDATEIDENTITY, _candidateidentity );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_CANDIDATEIDENTITY ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_CANDIDATEIDENTITY, TEMPLATE_MODIFY_CANDIDATEIDENTITY, model );
    }

    /**
     * Process the change form of a candidateidentity
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_CANDIDATEIDENTITY )
    public String doModifyCandidateIdentity( HttpServletRequest request ) throws AccessDeniedException
    {   
        populate( _candidateidentity, request, getLocale( ) );
		
		
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_CANDIDATEIDENTITY ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _candidateidentity, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_CANDIDATEIDENTITY, PARAMETER_ID_CANDIDATEIDENTITY, _candidateidentity.getId( ) );
        }

        CandidateIdentityHome.update( _candidateidentity );
        addInfo( INFO_CANDIDATEIDENTITY_UPDATED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_CANDIDATEIDENTITYS );
    }
}
