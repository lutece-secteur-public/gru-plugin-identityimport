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
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentityAttribute;
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentityAttributeHome;

/**
 * This class provides the user interface to manage CandidateIdentityAttribute features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageCandidateIdentityAttributes.jsp", controllerPath = "jsp/admin/plugins/identityimport/", right = "IDENTITYIMPORT_BATCH_MANAGEMENT" )
public class CandidateIdentityAttributeJspBean extends AbstractManageItemsJspBean<Integer, CandidateIdentityAttribute>
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 9007725551001629387L;
	// Templates
    private static final String TEMPLATE_MANAGE_CANDIDATEIDENTITYATTRIBUTES = "/admin/plugins/identityimport/manage_candidateidentityattributes.html";
    private static final String TEMPLATE_CREATE_CANDIDATEIDENTITYATTRIBUTE = "/admin/plugins/identityimport/create_candidateidentityattribute.html";
    private static final String TEMPLATE_MODIFY_CANDIDATEIDENTITYATTRIBUTE = "/admin/plugins/identityimport/modify_candidateidentityattribute.html";

    // Parameters
    private static final String PARAMETER_ID_CANDIDATEIDENTITYATTRIBUTE = "id";
    private static final String PARAMETER_IDENTITY_ID = "identity_id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_CANDIDATEIDENTITYATTRIBUTES = "identityimport.manage_candidateidentityattributes.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_CANDIDATEIDENTITYATTRIBUTE = "identityimport.modify_candidateidentityattribute.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_CANDIDATEIDENTITYATTRIBUTE = "identityimport.create_candidateidentityattribute.pageTitle";

    // Markers
    private static final String MARK_CANDIDATEIDENTITYATTRIBUTE_LIST = "candidateidentityattribute_list";
    private static final String MARK_CANDIDATEIDENTITYATTRIBUTE = "candidateidentityattribute";

    private static final String JSP_MANAGE_CANDIDATEIDENTITYATTRIBUTES = "jsp/admin/plugins/identityimport/ManageCandidateIdentityAttributes.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_CANDIDATEIDENTITYATTRIBUTE = "identityimport.message.confirmRemoveCandidateIdentityAttribute";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "identityimport.model.entity.candidateidentityattribute.attribute.";

    // Views
    private static final String VIEW_MANAGE_CANDIDATEIDENTITYATTRIBUTES = "manageCandidateIdentityAttributes";
    private static final String VIEW_CREATE_CANDIDATEIDENTITYATTRIBUTE = "createCandidateIdentityAttribute";
    private static final String VIEW_MODIFY_CANDIDATEIDENTITYATTRIBUTE = "modifyCandidateIdentityAttribute";

    // Actions
    private static final String ACTION_CREATE_CANDIDATEIDENTITYATTRIBUTE = "createCandidateIdentityAttribute";
    private static final String ACTION_MODIFY_CANDIDATEIDENTITYATTRIBUTE = "modifyCandidateIdentityAttribute";
    private static final String ACTION_REMOVE_CANDIDATEIDENTITYATTRIBUTE = "removeCandidateIdentityAttribute";
    private static final String ACTION_CONFIRM_REMOVE_CANDIDATEIDENTITYATTRIBUTE = "confirmRemoveCandidateIdentityAttribute";

    // Infos
    private static final String INFO_CANDIDATEIDENTITYATTRIBUTE_CREATED = "identityimport.info.candidateidentityattribute.created";
    private static final String INFO_CANDIDATEIDENTITYATTRIBUTE_UPDATED = "identityimport.info.candidateidentityattribute.updated";
    private static final String INFO_CANDIDATEIDENTITYATTRIBUTE_REMOVED = "identityimport.info.candidateidentityattribute.removed";

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";

    // Session variable to store working values
    private CandidateIdentityAttribute _candidateidentityattribute;
    private List<Integer> _listIdCandidateIdentityAttributes;
    private int _currentIdentityId;
    
    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_CANDIDATEIDENTITYATTRIBUTES, defaultView = true )
    public String getManageCandidateIdentityAttributes( HttpServletRequest request )
    {
        _candidateidentityattribute = null;

        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null || _listIdCandidateIdentityAttributes.isEmpty( ) )
        {
        	_currentIdentityId = Integer.parseInt( request.getParameter( PARAMETER_IDENTITY_ID ) );
            _listIdCandidateIdentityAttributes = CandidateIdentityAttributeHome.getIdCandidateIdentityAttributesList( _currentIdentityId );
        }

        Map<String, Object> model = getPaginatedListModel( request, MARK_CANDIDATEIDENTITYATTRIBUTE_LIST, _listIdCandidateIdentityAttributes,
                JSP_MANAGE_CANDIDATEIDENTITYATTRIBUTES );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_CANDIDATEIDENTITYATTRIBUTES, TEMPLATE_MANAGE_CANDIDATEIDENTITYATTRIBUTES, model );
    }

    /**
     * Get Items from Ids list
     * 
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
    @Override
    List<CandidateIdentityAttribute> getItemsFromIds( List<Integer> listIds )
    {
        List<CandidateIdentityAttribute> listCandidateIdentityAttribute = CandidateIdentityAttributeHome.getCandidateIdentityAttributesListByIds( listIds );

        // keep original order
        return listCandidateIdentityAttribute.stream( ).sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getId( ) ) ) )
                .collect( Collectors.toList( ) );
    }

    /**
     * reset the _listIdCandidateIdentityAttributes list
     */
    public void resetListId( )
    {
        _listIdCandidateIdentityAttributes = new ArrayList<>( );
    }

    /**
     * Returns the form to create a candidateidentityattribute
     *
     * @param request
     *            The Http request
     * @return the html code of the candidateidentityattribute form
     */
    @View( VIEW_CREATE_CANDIDATEIDENTITYATTRIBUTE )
    public String getCreateCandidateIdentityAttribute( HttpServletRequest request )
    {
        _candidateidentityattribute = ( _candidateidentityattribute != null ) ? _candidateidentityattribute : new CandidateIdentityAttribute( );

        Map<String, Object> model = getModel( );
        model.put( MARK_CANDIDATEIDENTITYATTRIBUTE, _candidateidentityattribute );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_CANDIDATEIDENTITYATTRIBUTE ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_CANDIDATEIDENTITYATTRIBUTE, TEMPLATE_CREATE_CANDIDATEIDENTITYATTRIBUTE, model );
    }

    /**
     * Process the data capture form of a new candidateidentityattribute
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_CANDIDATEIDENTITYATTRIBUTE )
    public String doCreateCandidateIdentityAttribute( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _candidateidentityattribute, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_CANDIDATEIDENTITYATTRIBUTE ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _candidateidentityattribute, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_CANDIDATEIDENTITYATTRIBUTE );
        }

        CandidateIdentityAttributeHome.create( _candidateidentityattribute );
        addInfo( INFO_CANDIDATEIDENTITYATTRIBUTE_CREATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_CANDIDATEIDENTITYATTRIBUTES );
    }

    /**
     * Manages the removal form of a candidateidentityattribute whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_CANDIDATEIDENTITYATTRIBUTE )
    public String getConfirmRemoveCandidateIdentityAttribute( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CANDIDATEIDENTITYATTRIBUTE ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_CANDIDATEIDENTITYATTRIBUTE ) );
        url.addParameter( PARAMETER_ID_CANDIDATEIDENTITYATTRIBUTE, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_CANDIDATEIDENTITYATTRIBUTE, url.getUrl( ),
                AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a candidateidentityattribute
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage candidateidentityattributes
     */
    @Action( ACTION_REMOVE_CANDIDATEIDENTITYATTRIBUTE )
    public String doRemoveCandidateIdentityAttribute( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CANDIDATEIDENTITYATTRIBUTE ) );

        CandidateIdentityAttributeHome.remove( nId );
        addInfo( INFO_CANDIDATEIDENTITYATTRIBUTE_REMOVED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_CANDIDATEIDENTITYATTRIBUTES );
    }

    /**
     * Returns the form to update info about a candidateidentityattribute
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_CANDIDATEIDENTITYATTRIBUTE )
    public String getModifyCandidateIdentityAttribute( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CANDIDATEIDENTITYATTRIBUTE ) );

        if ( _candidateidentityattribute == null || ( _candidateidentityattribute.getId( ) != nId ) )
        {
            Optional<CandidateIdentityAttribute> optCandidateIdentityAttribute = CandidateIdentityAttributeHome.findByPrimaryKey( nId );
            _candidateidentityattribute = optCandidateIdentityAttribute.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_CANDIDATEIDENTITYATTRIBUTE, _candidateidentityattribute );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_CANDIDATEIDENTITYATTRIBUTE ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_CANDIDATEIDENTITYATTRIBUTE, TEMPLATE_MODIFY_CANDIDATEIDENTITYATTRIBUTE, model );
    }

    /**
     * Process the change form of a candidateidentityattribute
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_CANDIDATEIDENTITYATTRIBUTE )
    public String doModifyCandidateIdentityAttribute( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _candidateidentityattribute, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_CANDIDATEIDENTITYATTRIBUTE ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _candidateidentityattribute, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_CANDIDATEIDENTITYATTRIBUTE, PARAMETER_ID_CANDIDATEIDENTITYATTRIBUTE, _candidateidentityattribute.getId( ) );
        }

        CandidateIdentityAttributeHome.update( _candidateidentityattribute );
        addInfo( INFO_CANDIDATEIDENTITYATTRIBUTE_UPDATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_CANDIDATEIDENTITYATTRIBUTES );
    }
}
