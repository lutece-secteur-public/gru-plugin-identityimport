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

import fr.paris.lutece.plugins.identityimport.business.CandidateIdentity;
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentityAttribute;
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentityAttributeHome;
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentityHome;
import fr.paris.lutece.plugins.identityimport.wf.WorkflowBean;
import fr.paris.lutece.plugins.identityimport.wf.WorkflowBeanService;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.AttributeTreatmentType;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.AuthorType;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.RequestAuthor;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.search.*;
import fr.paris.lutece.plugins.identitystore.v3.web.service.IdentityService;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.html.AbstractPaginator;
import fr.paris.lutece.util.url.UrlItem;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class provides the user interface to manage CandidateIdentity features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageCandidateIdentities.jsp", controllerPath = "jsp/admin/plugins/identityimport/", right = "IDENTITYIMPORT_BATCH_MANAGEMENT" )
public class CandidateIdentityJspBean extends AbstractManageItemsJspBean<Integer, WorkflowBean<CandidateIdentity>>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8102266671918797492L;
	// Templates
	private static final String TEMPLATE_MANAGE_CANDIDATEIDENTITIES = "/admin/plugins/identityimport/manage_candidateidentities.html";
	private static final String TEMPLATE_CREATE_CANDIDATEIDENTITY = "/admin/plugins/identityimport/create_candidateidentity.html";
	private static final String TEMPLATE_MODIFY_CANDIDATEIDENTITY = "/admin/plugins/identityimport/modify_candidateidentity.html";
	private static final String TEMPLATE_IMPORT_CANDIDATEIDENTITY = "/admin/plugins/identityimport/import_candidateidentity.html";

	// Parameters
	private static final String PARAMETER_ID_CANDIDATEIDENTITY = "id";
	private static final String PARAMETER_BATCH_ID = "id_batch";

	// Properties for page titles
	private static final String PROPERTY_PAGE_TITLE_MANAGE_CANDIDATEIDENTITIES = "identityimport.manage_candidateidentities.pageTitle";
	private static final String PROPERTY_PAGE_TITLE_MODIFY_CANDIDATEIDENTITY = "identityimport.modify_candidateidentity.pageTitle";
	private static final String PROPERTY_PAGE_TITLE_CREATE_CANDIDATEIDENTITY = "identityimport.create_candidateidentity.pageTitle";
	private static final String PROPERTY_PAGE_TITLE_IMPORT_CANDIDATEIDENTITY = "identityimport.import_candidateidentity.pageTitle";

	// Markers
	private static final String MARK_CANDIDATEIDENTITY_LIST = "candidateidentity_list";
	private static final String MARK_CANDIDATEIDENTITY_DUPLICATE_LIST = "candidateidentity_duplicate_list";
	private static final String MARK_CANDIDATEIDENTITY = "candidateidentity";
	private static final String MARK_ATTRIBUTE_KEY_LIST = "key_list";

	private static final String JSP_MANAGE_CANDIDATEIDENTITIES = "jsp/admin/plugins/identityimport/ManageCandidateIdentities.jsp";

	// Properties
	private static final String MESSAGE_CONFIRM_REMOVE_CANDIDATEIDENTITY = "identityimport.message.confirmRemoveCandidateIdentity";

	// Validations
	private static final String VALIDATION_ATTRIBUTES_PREFIX = "identityimport.model.entity.candidateidentity.attribute.";

	// Views
	private static final String VIEW_MANAGE_CANDIDATEIDENTITIES = "manageCandidateIdentities";
	private static final String VIEW_CREATE_CANDIDATEIDENTITY = "createCandidateIdentity";
	private static final String VIEW_MODIFY_CANDIDATEIDENTITY = "modifyCandidateIdentity";
	private static final String VIEW_IMPORT_CANDIDATEIDENTITY = "importCandidateIdentity";

	// Actions
	private static final String ACTION_CREATE_CANDIDATEIDENTITY = "createCandidateIdentity";
	private static final String ACTION_MODIFY_CANDIDATEIDENTITY = "modifyCandidateIdentity";
	private static final String ACTION_REMOVE_CANDIDATEIDENTITY = "removeCandidateIdentity";
	private static final String ACTION_IMPORT_CANDIDATEIDENTITY = "importCandidateIdentity";
	private static final String ACTION_CONFIRM_REMOVE_CANDIDATEIDENTITY = "confirmRemoveCandidateIdentity";
	private static final String ACTION_PROCESS_WORKFLOW_ACTION = WorkflowBeanService.CONSTANT_PROCESS_WORKFLOW_ACTION;

	// Infos
	private static final String INFO_CANDIDATEIDENTITY_CREATED = "identityimport.info.candidateidentity.created";
	private static final String INFO_CANDIDATEIDENTITY_UPDATED = "identityimport.info.candidateidentity.updated";
	private static final String INFO_CANDIDATEIDENTITY_REMOVED = "identityimport.info.candidateidentity.removed";
	private static final String INFO_CANDIDATEIDENTITY_IMPORTED = "identityimport.info.candidateidentity.imported";

	private static final String INFO_CANDIDATEIDENTITY_RETRIEVE_DUPLICATE = "identityimport.info.candidateidentity.retrieve.duplicate";

	// Errors
	private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";

	// Constants
	private static final int WORKFLOW_KEY = 2; // todo: search in properties

	// Workflow
	private static final String CANDIDATEIDENTITY_WFBEANSERVICE = "identityimport.candidateidentity.wfbeanservice";

	private WorkflowBeanService<CandidateIdentity> _wfBeanService = SpringContextService.getBean( CANDIDATEIDENTITY_WFBEANSERVICE );

	private IdentityService identityService = SpringContextService.getBean( "identityService.rest.httpAccess.v3" );

	// Session variable to store working values
	private CandidateIdentity _candidateidentity;
	WorkflowBean<CandidateIdentity> _wfBean;

	private int _currentBatchId;
	private List<Integer> _listIdCandidateIdentities;

	/**
	 * Build the Manage View
	 * 
	 * @param request
	 *            The HTTP request
	 * @return The page
	 */
	@View( value = VIEW_MANAGE_CANDIDATEIDENTITIES, defaultView = true )
	public String getManageCandidateIdentities( HttpServletRequest request )
	{
		_candidateidentity = null;

		if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null || _listIdCandidateIdentities.isEmpty( ) )
		{
			_currentBatchId = Integer.parseInt( request.getParameter( PARAMETER_BATCH_ID ) );
			_listIdCandidateIdentities = CandidateIdentityHome.getIdCandidateIdentitiesList( _currentBatchId );
		}

		Map<String, Object> model = getPaginatedListModel( request, MARK_CANDIDATEIDENTITY_LIST, _listIdCandidateIdentities, JSP_MANAGE_CANDIDATEIDENTITIES );
		model.put(PARAMETER_BATCH_ID, _currentBatchId);

		return getPage( PROPERTY_PAGE_TITLE_MANAGE_CANDIDATEIDENTITIES, TEMPLATE_MANAGE_CANDIDATEIDENTITIES, model );
	}

	/**
	 * Get Items from Ids list
	 * 
	 * @param listIds
	 * @return the populated list of items corresponding to the id List
	 */
	@Override
	List<WorkflowBean<CandidateIdentity>> getItemsFromIds( List<Integer> listIds )
	{
		List<CandidateIdentity> listCandidateIdentity = CandidateIdentityHome.getCandidateIdentitiesListByIds( listIds );

		// keep original order
		return listCandidateIdentity.stream( )
				.sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getId( ) ) ) )
				.map( b -> _wfBeanService.createWorkflowBean( b, b.getId( ), b.getIdBatch( ), getUser( ) ) )
				.collect( Collectors.toList( ) );
	}

	/**
	 * reset the _listIdCandidateIdentities list
	 */
	public void resetListId( )
	{
		_listIdCandidateIdentities = new ArrayList<>( );
	}

	/**
	 * Returns the form to create a candidateidentity
	 *
	 * @param request
	 *            The Http request
	 * @return the html code of the candidateidentity form
	 */
	@View( VIEW_CREATE_CANDIDATEIDENTITY )
	public String getCreateCandidateIdentity( HttpServletRequest request )
	{
		_candidateidentity = ( _candidateidentity != null ) ? _candidateidentity : new CandidateIdentity( );

		Map<String, Object> model = getModel( );
		model.put( MARK_CANDIDATEIDENTITY, _candidateidentity );
		model.put( PARAMETER_BATCH_ID, _currentBatchId );
		model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_CANDIDATEIDENTITY ) );

		return getPage( PROPERTY_PAGE_TITLE_CREATE_CANDIDATEIDENTITY, TEMPLATE_CREATE_CANDIDATEIDENTITY, model );
	}

	/**
	 * Process the data capture form of a new candidateidentity
	 *
	 * @param request
	 *            The Http Request
	 * @return The Jsp URL of the process result
	 * @throws AccessDeniedException
	 */
	@Action( ACTION_CREATE_CANDIDATEIDENTITY )
	public String doCreateCandidateIdentity( HttpServletRequest request ) throws AccessDeniedException
	{
		populate( _candidateidentity, request, getLocale( ) );

		if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_CANDIDATEIDENTITY ) )
		{
			throw new AccessDeniedException( "Invalid security token" );
		}

		// Check constraints
		if ( !validateBean( _candidateidentity, VALIDATION_ATTRIBUTES_PREFIX ) )
		{
			return redirectView( request, VIEW_CREATE_CANDIDATEIDENTITY );
		}

		CandidateIdentityHome.create( _candidateidentity );
		addInfo( INFO_CANDIDATEIDENTITY_CREATED, getLocale( ) );
		resetListId( );

		_wfBean = _wfBeanService.createWorkflowBean( _candidateidentity,
				_candidateidentity.getId( ), _candidateidentity.getIdBatch( ), getUser( ) );


		return redirect( request, VIEW_MANAGE_CANDIDATEIDENTITIES, PARAMETER_BATCH_ID, _candidateidentity.getIdBatch( ) );
	}

	/**
	 * Manages the removal form of a candidateidentity whose identifier is in the http request
	 *
	 * @param request
	 *            The Http request
	 * @return the html code to confirm
	 */
	@Action( ACTION_CONFIRM_REMOVE_CANDIDATEIDENTITY )
	public String getConfirmRemoveCandidateIdentity( HttpServletRequest request )
	{
		int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CANDIDATEIDENTITY ) );
		UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_CANDIDATEIDENTITY ) );
		url.addParameter( PARAMETER_ID_CANDIDATEIDENTITY, nId );
		url.addParameter( PARAMETER_BATCH_ID, _currentBatchId );

		String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_CANDIDATEIDENTITY, url.getUrl( ),
				AdminMessage.TYPE_CONFIRMATION );

		return redirect( request, strMessageUrl );
	}

	/**
	 * Handles the removal form of a candidateidentity
	 *
	 * @param request
	 *            The Http request
	 * @return the jsp URL to display the form to manage candidateidentities
	 */
	@Action( ACTION_REMOVE_CANDIDATEIDENTITY )
	public String doRemoveCandidateIdentity( HttpServletRequest request )
	{
		int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CANDIDATEIDENTITY ) );

		CandidateIdentityHome.remove( nId );
		addInfo( INFO_CANDIDATEIDENTITY_REMOVED, getLocale( ) );
		resetListId( );

		_candidateidentity = null;
		_wfBean = null;

		return redirect( request, VIEW_MANAGE_CANDIDATEIDENTITIES, PARAMETER_ID_CANDIDATEIDENTITY, nId, PARAMETER_BATCH_ID, _currentBatchId );
	}

	/**
	 * Returns the form to update info about a candidateidentity
	 *
	 * @param request
	 *            The Http request
	 * @return The HTML form to update info
	 */
	@View( VIEW_MODIFY_CANDIDATEIDENTITY )
	public String getModifyCandidateIdentity( HttpServletRequest request )
	{
		int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CANDIDATEIDENTITY ) );

		if ( _candidateidentity == null || ( _candidateidentity.getId( ) != nId ) )
		{
			Optional<CandidateIdentity> optCandidateIdentity = CandidateIdentityHome.findByPrimaryKey( nId );
			_candidateidentity = optCandidateIdentity.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
			_currentBatchId = Integer.parseInt( request.getParameter( PARAMETER_BATCH_ID ) );
			_wfBean = _wfBeanService.createWorkflowBean( _candidateidentity, 
					_candidateidentity.getId( ), _candidateidentity.getIdBatch( ), getUser( ) );
		}

		_wfBeanService.addHistory(_wfBean, request, getLocale( ) );

		Map<String, Object> model = getModel( );
		model.put( MARK_CANDIDATEIDENTITY, _wfBean );
		model.put(PARAMETER_BATCH_ID, _currentBatchId);
		model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_CANDIDATEIDENTITY ) );

		return getPage( PROPERTY_PAGE_TITLE_MODIFY_CANDIDATEIDENTITY, TEMPLATE_MODIFY_CANDIDATEIDENTITY, model );
	}

	/**
	 * Process the change form of a candidateidentity
	 *
	 * @param request
	 *            The Http request
	 * @return The Jsp URL of the process result
	 * @throws AccessDeniedException
	 */
	@Action( ACTION_MODIFY_CANDIDATEIDENTITY )
	public String doModifyCandidateIdentity( HttpServletRequest request ) throws AccessDeniedException
	{
		populate( _candidateidentity, request, getLocale( ) );

		if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_CANDIDATEIDENTITY ) )
		{
			throw new AccessDeniedException( "Invalid security token" );
		}

		// Check constraints
		if ( !validateBean( _candidateidentity, VALIDATION_ATTRIBUTES_PREFIX ) )
		{
			return redirect( request, VIEW_MODIFY_CANDIDATEIDENTITY, PARAMETER_ID_CANDIDATEIDENTITY, _candidateidentity.getId( ) );
		}

		CandidateIdentityHome.update( _candidateidentity );
		addInfo( INFO_CANDIDATEIDENTITY_UPDATED, getLocale( ) );
		resetListId( );

		return redirect( request, VIEW_MANAGE_CANDIDATEIDENTITIES, PARAMETER_BATCH_ID, _candidateidentity.getIdBatch( ) );
	}

	/**
	 * process a workflow action
	 *
	 * @param request
	 *            The Http request
	 * @return the jsp URL to display the form to manage batchs
	 */
	@Action( ACTION_PROCESS_WORKFLOW_ACTION )
	public String doProcessAction( HttpServletRequest request )
	{
		int nResourceId = Integer.parseInt( request.getParameter( WorkflowBeanService.PARAMETER_RESOURCE_ID ) );
		int nActionId = Integer.parseInt( request.getParameter( WorkflowBeanService.PARAMETER_ACTION_ID ) );

		// check and refresh resource if necessary
		if ( _candidateidentity == null || ( _candidateidentity.getId( ) != nResourceId ) )
		{
			Optional<CandidateIdentity> optIdentity = CandidateIdentityHome.findByPrimaryKey( nResourceId );
			_candidateidentity = optIdentity.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );

			_wfBean = _wfBeanService.createWorkflowBean( _candidateidentity, 
					_candidateidentity.getId( ), _candidateidentity.getIdBatch( ), getUser( ) );
		}

		// Process the action, and redirect,
		// or redirect to the task form if exists

		if ( !_wfBeanService.existsTaskForm( nActionId, getLocale( ) ) ) 
		{
			// The task does not need a form
			_wfBeanService.processAction( _wfBean, nActionId, request, getLocale( ) );

			return redirect( request, VIEW_IMPORT_CANDIDATEIDENTITY, PARAMETER_ID_CANDIDATEIDENTITY, _wfBean.getResourceId( ) );
		}
		else if ( request.getParameter( WorkflowBeanService.PARAMETER_SUBMITTED_TASK_FORM ) == null )
		{
			// A task form should be displayed 
			return _wfBeanService.getTaskForm( _wfBean, nActionId, request, getLocale( ), JSP_MANAGE_CANDIDATEIDENTITIES );
		}
		else 
		{
			// The task form was submitted    		
			String errMsg = _wfBeanService.validateTaskForm( _wfBean, nActionId, request, getLocale( ) );
			if ( errMsg == null )
			{
				return redirect( request, VIEW_IMPORT_CANDIDATEIDENTITY, PARAMETER_ID_CANDIDATEIDENTITY, _wfBean.getResourceId( ) );
			}
			else
			{
				// error message
				return redirect ( request, errMsg );
			}	
		}
	}

	/**
	 * Returns the form to update info about a candidateidentity
	 *
	 * @param request
	 *            The Http request
	 * @return The HTML form to update info
	 */
	@View( VIEW_IMPORT_CANDIDATEIDENTITY )
	public String getImportCandidateIdentity( HttpServletRequest request )
	{
		int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CANDIDATEIDENTITY ) );

		if ( _candidateidentity == null || ( _candidateidentity.getId( ) != nId ) )
		{
			Optional<CandidateIdentity> optCandidateIdentity = CandidateIdentityHome.findByPrimaryKey( nId );
			_candidateidentity = optCandidateIdentity.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );

			_candidateidentity.setAttributes( CandidateIdentityAttributeHome.getCandidateIdentityAttributesList( nId ) );

			_wfBean =  _wfBeanService.createWorkflowBean( _candidateidentity,
					_candidateidentity.getId( ), _candidateidentity.getIdBatch( ), getUser( ) );
		}
		Map<String, Object> model = getModel( );

		// Appel API Recherche
		final IdentitySearchRequest searchRequest = new IdentitySearchRequest();
		searchRequest.setOrigin(buildAuthor());
		final SearchDto searchDto = new SearchDto();
		final List<SearchAttributeDto> searchAttributes = _candidateidentity.getAttributes().stream()
				.filter(attr -> attr.getCode().equals("family_name") ||
						attr.getCode().equals("first_name") ||
						attr.getCode().equals("birthdate"))
				.map(attr -> {
					final SearchAttributeDto searchAttribute = new SearchAttributeDto();
					searchAttribute.setKey(attr.getCode());
					searchAttribute.setValue(attr.getValue());
					if (attr.getCode().equals("birthdate")) {
						searchAttribute.setTreatmentType(AttributeTreatmentType.STRICT);
					} else {
						searchAttribute.setTreatmentType(AttributeTreatmentType.APPROXIMATED);
					}
					return searchAttribute;
				}).collect(Collectors.toList());
		searchDto.setAttributes(searchAttributes);
		searchRequest.setSearch(searchDto);

		// init attributes key list
		List<String> keyList = _candidateidentity.getAttributes().stream().map(CandidateIdentityAttribute::getCode).collect(Collectors.toList());

		try {
			IdentitySearchResponse response = identityService.searchIdentities(searchRequest, _candidateidentity.getClientAppCode());

			keyList.addAll(  response.getIdentities().stream()
					.flatMap(duplicate -> duplicate.getAttributes().stream())
					.map(CertifiedAttribute::getKey)
					.distinct().collect( Collectors.toList() ) );

			model.put( MARK_CANDIDATEIDENTITY_DUPLICATE_LIST, response.getIdentities() );
		} catch (IdentityStoreException e) {
			addInfo( e.getLocalizedMessage() , getLocale( ) );
		}


		_wfBeanService.addHistory(_wfBean, request, getLocale( ) );

		model.put( MARK_CANDIDATEIDENTITY, _wfBean );
		model.put( MARK_ATTRIBUTE_KEY_LIST, keyList.stream().distinct().collect(Collectors.toList( ) ) );
		model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_CANDIDATEIDENTITY ) );

		return getPage( PROPERTY_PAGE_TITLE_IMPORT_CANDIDATEIDENTITY, TEMPLATE_IMPORT_CANDIDATEIDENTITY, model );
	}

	private RequestAuthor buildAuthor( )
	{
		final RequestAuthor author = new RequestAuthor( );
		author.setName( getUser( ).getEmail( ) );
		author.setType( AuthorType.application );
		return author;
	}

	/**
	 * Identify the candidateidentity to import
	 *
	 * @param request
	 *            The Http request
	 * @return The HTML form to update info
	 */
	@Action( ACTION_IMPORT_CANDIDATEIDENTITY )
	public String doImportCandidateIdentity( HttpServletRequest request )
	{
		int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CANDIDATEIDENTITY ) );

		if ( _candidateidentity == null || ( _candidateidentity.getId( ) != nId ) )
		{
			Optional<CandidateIdentity> optCandidateIdentity = CandidateIdentityHome.findByPrimaryKey( nId );
			_candidateidentity = optCandidateIdentity.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );

			_candidateidentity.setAttributes( CandidateIdentityAttributeHome.getCandidateIdentityAttributesList( nId ) );

			_wfBean =  _wfBeanService.createWorkflowBean( _candidateidentity,
					_candidateidentity.getId( ), _candidateidentity.getIdBatch( ), getUser( ) );
		}
		Map<String, Object> model = getModel( );

		_wfBeanService.addHistory(_wfBean, request, getLocale( ) );

		model.put( MARK_CANDIDATEIDENTITY, _wfBean );
		model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_CANDIDATEIDENTITY ) );

		return getPage( PROPERTY_PAGE_TITLE_IMPORT_CANDIDATEIDENTITY, TEMPLATE_IMPORT_CANDIDATEIDENTITY, model );
	}
}
