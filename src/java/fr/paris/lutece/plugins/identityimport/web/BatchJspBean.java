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
package fr.paris.lutece.plugins.identityimport.web;

import fr.paris.lutece.plugins.identityimport.business.Batch;
import fr.paris.lutece.plugins.identityimport.business.BatchHome;
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentity;
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentityAttribute;
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentityAttributeHome;
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentityHome;
import fr.paris.lutece.plugins.identityimport.business.ResourceState;
import fr.paris.lutece.plugins.identityimport.service.BatchService;
import fr.paris.lutece.plugins.identityimport.service.CandidateIdentityService;
import fr.paris.lutece.plugins.identityimport.wf.WorkflowBean;
import fr.paris.lutece.plugins.identityimport.wf.WorkflowBeanService;
import fr.paris.lutece.plugins.identityquality.v3.web.service.IdentityQualityService;
import fr.paris.lutece.plugins.identitystore.v3.csv.CsvIdentityService;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.AttributeDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.AuthorType;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.BatchDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.IdentityDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.RequestAuthor;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.search.DuplicateSearchRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.search.DuplicateSearchResponse;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.progressmanager.ProgressManagerService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.url.UrlItem;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This class provides the user interface to manage Batch features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageBatchs.jsp", controllerPath = "jsp/admin/plugins/identityimport/", right = "IDENTITYIMPORT_BATCH_MANAGEMENT" )
public class BatchJspBean extends AbstractManageItemsJspBean<Integer, WorkflowBean<Batch>>
{
    // Templates
    private static final String TEMPLATE_IMPORT_BATCH = "/admin/plugins/identityimport/import_batch.html";
    private static final String TEMPLATE_MANAGE_BATCHS = "/admin/plugins/identityimport/manage_batchs.html";
    private static final String TEMPLATE_MANAGE_IDENTITIES = "/admin/plugins/identityimport/manage_candidateidentities.html";
    private static final String TEMPLATE_CREATE_BATCH = "/admin/plugins/identityimport/create_batch.html";
    private static final String TEMPLATE_MODIFY_BATCH = "/admin/plugins/identityimport/modify_batch.html";
    private static final String TEMPLATE_PROCESS_BATCH = "/admin/plugins/identityimport/process_batch.html";
    private static final String TEMPLATE_IMPORT_CANDIDATE_IDENTITY = "/admin/plugins/identityimport/import_candidateidentity.html";
    private static final String JSP_MANAGE_CANDIDATEIDENTITIES = "jsp/admin/plugins/identityimport/ManageBatchs.jsp";

    // Parameters
    private static final String PARAMETER_ID_BATCH = "id_batch";
    private static final String PARAMETER_ID_CANDIDATEIDENTITY = "id_identity";
    private static final String PARAMETER_RETURN_URL = "return_url";
    private static final String PARAMETER_ID_BATCH_STATE = "id_state";
    private static final String PARAMETER_ID_ACTION = "id_action";
    private static final String PARAMETER_CSV_FILE = "csvFile";
    private static final String PARAMETER_REFERENCE = "reference";
    private static final String PARAMETER_FILTER_APP_CODE = "application_code";
    private final static String PARAMETER_BATCH_PAGE = "batch_page";
    private final static String IDENTITIES_PARAMETER_PAGE = "identities_page";
    private static final String MARK_FEED_TOKEN = "feed_token";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_IMPORT_BATCH = "identityimport.import_batch.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MANAGE_BATCHS = "identityimport.manage_batchs.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_BATCH = "identityimport.modify_batch.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_BATCH = "identityimport.create_batch.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_PROCESS_BATCH = "identityimport.process_batch.pageTitle";
    private static final String PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE = "identityimport.listItems.itemsPerPage";
    private static final String PROPERTY_PAGE_TITLE_IMPORT_CANDIDATEIDENTITY = "identityimport.import_candidateidentity.pageTitle";
    private static final String PROPERTY_DUPLICATE_RULES = "identityimport.duplicate.search.rules";

    // Markers
    private static final String MARK_BATCH_LIST = "batch_list";
    private static final String MARK_CURRENT_BATCH_ID = "current_batch_id";
    private static final String MARK_IDENTITY_LIST = "identity_list";
    private static final String MARK_BATCH = "batch";
    private static final String MARK_NEW_IMPORT = "newImport";
    private static final String MARK_BATCH_STATE_LIST = "batch_state_list";
    private static final String MARK_CURRENT_BATCH_STATE = "current_batch_state";
    private static final String MARK_BATCH_TOTAL_PAGES = "batch_total_pages";
    private static final String MARK_CURRENT_BATCH_PAGE = "batch_current_page";
    private static final String MARK_IDENTITIES_TOTAL_PAGES = "identities_total_pages";
    private static final String MARK_CURRENT_IDENTITIES_PAGE = "identities_current_page";
    private static final String MARK_FILTER_APP_CODE = "application_code";
    private static final String MARK_CANDIDATE_IDENTITY_DUPLICATE_LIST = "duplicate_list";
    private static final String MARK_CANDIDATE_IDENTITY = "identity";
    private static final String MARK_CANDIDATE_IDENTITY_WORKFLOW = "identity_workflow";
    private static final String MARK_ATTRIBUTE_KEY_LIST = "key_list";
    private static final String MARK_RETURN_URL = "return_url";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_BATCH = "identityimport.message.confirmRemoveBatch";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "identityimport.model.entity.batch.attribute.";

    // Views
    private static final String VIEW_IMPORT_BATCH = "importBatch";
    private static final String VIEW_COMPLETE_BATCH = "completeBatch";
    private static final String VIEW_MANAGE_BATCHS = "manageBatchs";
    private static final String VIEW_MANAGE_IDENTITIES = "manageIdentities";
    private static final String VIEW_CREATE_BATCH = "createBatch";
    private static final String VIEW_MODIFY_BATCH = "modifyBatch";
    private static final String VIEW_PROCESS_BATCH = "processBatch";
    private static final String VIEW_IMPORT_CANDIDATEIDENTITY = "importCandidateIdentity";

    // Actions
    private static final String ACTION_IMPORT_BATCH = "importBatch";
    private static final String ACTION_CREATE_BATCH = "createBatch";
    private static final String ACTION_MODIFY_BATCH = "modifyBatch";
    private static final String ACTION_REMOVE_BATCH = "removeBatch";
    private static final String ACTION_CONFIRM_REMOVE_BATCH = "confirmRemoveBatch";
    private static final String ACTION_PROCESS_BATCH_WORKFLOW_ACTION = "processAction";
    private static final String ACTION_PROCESS_IDENTITY_WORKFLOW_ACTION = "processIdentityAction";

    // Infos
    private static final String INFO_BATCH_CREATED = "identityimport.info.batch.created";
    private static final String INFO_BATCH_UPDATED = "identityimport.info.batch.updated";
    private static final String INFO_BATCH_REMOVED = "identityimport.info.batch.removed";

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";

    // Workflow
    private static final String BATCH_WFBEANSERVICE = "identityimport.batch.wfbeanservice";
    private static final String CANDIDATEIDENTITY_WFBEANSERVICE = "identityimport.candidateidentity.wfbeanservice";
    private final WorkflowBeanService<Batch> _wfBatchBeanService = SpringContextService.getBean( BATCH_WFBEANSERVICE );
    private final WorkflowBeanService<CandidateIdentity> _wfIdentitiesBeanService = SpringContextService.getBean( CANDIDATEIDENTITY_WFBEANSERVICE );

    // Feed
    private static final String FEED_NAME = "importBatchFeed";
    private final ProgressManagerService progressManagerService = ProgressManagerService.getInstance( );

    // Session variable to store working values
    private Batch _batch;
    private String _filterAppCode;
    private ResourceState _current_batch_state;
    private Integer _batchCurrentPage;
    private Integer _batchTotalPages;
    private Integer _identitiesCurrentPage;
    private Integer _identitiesTotalPages;
    private WorkflowBean<Batch> _wfBatchBean;
    private List<Integer> _listIdBatchs = new ArrayList<>( );
    private List<ResourceState> _batchStates = new ArrayList<>( );
    private String _feedToken;
    private Integer _currentBatchId;
    private List<Integer> _listIdCandidateIdentities;
    private CandidateIdentity _candidateidentity;
    private WorkflowBean<CandidateIdentity> _wfCandidateIdentityBean;
    private Integer _currentIdentityId;

    // Services
    private final IdentityQualityService identityQualityService = SpringContextService.getBean( "qualityService.rest" );
    private final List<String> DUPLICATE_RULE_CODES = Arrays.asList( AppPropertiesService.getProperty( PROPERTY_DUPLICATE_RULES, "" ).split( "," ) );

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_BATCHS, defaultView = true )
    public String getManageBatchs( final HttpServletRequest request )
    {
        this.globalInit( request );
        this.unregisterFeed( );
        final Map<String, Object> model = this.globalModel( request );
        return getPage( PROPERTY_PAGE_TITLE_MANAGE_BATCHS, TEMPLATE_MANAGE_BATCHS, model );
    }

    /**
     * Build the Manage View
     *
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_IDENTITIES )
    public String getManageBatchIdentities( HttpServletRequest request )
    {
        this.globalInit( request );
        this.unregisterFeed( );

        final Optional<String> idBatchOpt = Optional.ofNullable( request.getParameter( PARAMETER_ID_BATCH ) );
        idBatchOpt.ifPresent( idBatch -> {
            _currentBatchId = Integer.parseInt( idBatch );
            _identitiesCurrentPage = Optional.ofNullable( request.getParameter( IDENTITIES_PARAMETER_PAGE ) ).map( Integer::parseInt ).orElse( 1 );
            _listIdCandidateIdentities = CandidateIdentityHome.getIdCandidateIdentitiesList( _currentBatchId );
            final int nbItemsPerPages = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 10 );
            final int totalRecords = _listIdCandidateIdentities.size( );
            _identitiesTotalPages = (int) Math.ceil( (double) totalRecords / nbItemsPerPages );
            if ( _identitiesTotalPages == 0 )
            {
                _identitiesTotalPages = 1;
            }

            final int start = ( _identitiesCurrentPage - 1 ) * nbItemsPerPages;
            final int end = Math.min( start + nbItemsPerPages, totalRecords );
            _listIdCandidateIdentities = _listIdCandidateIdentities.subList( start, end );

            if ( _batch == null || ( _batch.getId( ) != _currentBatchId ) )
            {
                Optional<Batch> optBatch = BatchHome.findByPrimaryKey( _currentBatchId );
                _batch = optBatch.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
            }

            if ( _wfBatchBean == null || _wfBatchBean.getResourceId( ) != _currentBatchId )
            {
                _wfBatchBean = _wfBatchBeanService.createWorkflowBean( _batch, _batch.getId( ), getUser( ) );
                _wfBatchBeanService.addHistory( _wfBatchBean, request, getLocale( ) );
                _wfBatchBeanService.countSubResources( _wfBatchBean );
                _wfBatchBeanService.addSubResourceStates( _wfBatchBean );
            }
        } );

        final Map<String, Object> model = this.globalModel( request );
        return getPage( PROPERTY_PAGE_TITLE_MANAGE_BATCHS, TEMPLATE_MANAGE_IDENTITIES, model );
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
        final Optional<String> idIdentityOpt = Optional.ofNullable( request.getParameter( PARAMETER_ID_CANDIDATEIDENTITY ) );
        idIdentityOpt.ifPresent( idIdentity -> {
            _currentIdentityId = Integer.parseInt( idIdentity );
            final Optional<CandidateIdentity> optCandidateIdentity = CandidateIdentityHome.findByPrimaryKey( _currentIdentityId );
            _candidateidentity = optCandidateIdentity.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
            _candidateidentity.setAttributes( CandidateIdentityAttributeHome.getCandidateIdentityAttributesList( _currentIdentityId ) );
            _wfCandidateIdentityBean = _wfIdentitiesBeanService.createWorkflowBean( _candidateidentity, _candidateidentity.getId( ),
                    _candidateidentity.getIdBatch( ), getUser( ) );
            _wfIdentitiesBeanService.addHistory( _wfCandidateIdentityBean, request, getLocale( ) );
        } );

        final Map<String, Object> model = getModel( );

        // Appel API Recherche
        final DuplicateSearchRequest searchRequest = new DuplicateSearchRequest( );
        searchRequest.getRuleCodes( ).addAll( DUPLICATE_RULE_CODES );
        final Map<String, String> searchAttributes = _candidateidentity.getAttributes( ).stream( )
                .collect( Collectors.toMap( CandidateIdentityAttribute::getCode, CandidateIdentityAttribute::getValue ) );
        searchRequest.getAttributes( ).putAll( searchAttributes );

        // init attributes key list
        final List<String> keyList = _candidateidentity.getAttributes( ).stream( ).map( CandidateIdentityAttribute::getCode ).collect( Collectors.toList( ) );
        try
        {
            final DuplicateSearchResponse response = identityQualityService.searchDuplicates( searchRequest, _candidateidentity.getClientAppCode( ),
                    this.buildAuthor( ) );
            keyList.addAll( response.getIdentities( ).stream( ).flatMap( duplicate -> duplicate.getAttributes( ).stream( ) ).map( AttributeDto::getKey )
                    .distinct( ).collect( Collectors.toList( ) ) );
            model.put( MARK_CANDIDATE_IDENTITY_DUPLICATE_LIST, response.getIdentities( ) );
        }
        catch( IdentityStoreException e )
        {
            this.addInfo( e.getLocalizedMessage( ), getLocale( ) );
        }

        final Optional<String> returnUrlOpt = Optional.ofNullable( request.getParameter( PARAMETER_RETURN_URL ) );
        returnUrlOpt.ifPresent( url -> {
            final String idState = request.getParameter( PARAMETER_ID_BATCH_STATE );
            final String idBatch = request.getParameter( PARAMETER_ID_BATCH );
            final String batchPage = request.getParameter( PARAMETER_BATCH_PAGE );
            final String applicationCode = request.getParameter( PARAMETER_FILTER_APP_CODE );
            final String returnUrl = String.format( "%s&%s=%s&%s=%s&%s=%s&%s=%s", url, PARAMETER_ID_BATCH_STATE, idState, PARAMETER_ID_BATCH, idBatch,
                    PARAMETER_BATCH_PAGE, batchPage, PARAMETER_FILTER_APP_CODE, applicationCode );
            model.put( MARK_RETURN_URL, returnUrl );
        } );

        model.put( MARK_CANDIDATE_IDENTITY_WORKFLOW, _wfCandidateIdentityBean );
        model.put( MARK_CANDIDATE_IDENTITY, CandidateIdentityService.instance( ).getIdentityDto( _wfCandidateIdentityBean.getResource( ) ) );
        model.put( MARK_ATTRIBUTE_KEY_LIST, keyList.stream( ).distinct( ).collect( Collectors.toList( ) ) );

        return getPage( PROPERTY_PAGE_TITLE_IMPORT_CANDIDATEIDENTITY, TEMPLATE_IMPORT_CANDIDATE_IDENTITY, model );
    }

    /**
     * Get Items from Ids list
     *
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
    @Override
    protected List<WorkflowBean<Batch>> getItemsFromIds( List<Integer> listIds )
    {
        final List<Batch> listBatch = BatchHome.getBatchsListByIds( listIds );

        // keep original order
        return listBatch.stream( ).sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getId( ) ) ) ).map( b -> {
            final WorkflowBean<Batch> workflowBean = _wfBatchBeanService.createWorkflowBean( b, b.getId( ), getUser( ) );
            _wfBatchBeanService.countSubResources( workflowBean );
            return workflowBean;
        } ).collect( Collectors.toList( ) );
    }

    protected List<WorkflowBean<CandidateIdentity>> getIdentitiesFromIds( final List<Integer> listIds, final HttpServletRequest request )
    {
        final List<CandidateIdentity> listCandidateIdentity = CandidateIdentityHome.getCandidateIdentitiesListByIds( listIds );

        // keep original order
        return listCandidateIdentity.stream( )
                .peek( candidateIdentity -> candidateIdentity
                        .setAttributes( CandidateIdentityAttributeHome.getCandidateIdentityAttributesList( candidateIdentity.getId( ) ) ) )
                .sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getId( ) ) ) ).map( b -> {
                    final WorkflowBean<CandidateIdentity> workflowBean = _wfIdentitiesBeanService.createWorkflowBean( b, b.getId( ), b.getIdBatch( ),
                            getUser( ) );
                    _wfIdentitiesBeanService.addHistory( workflowBean, request, getLocale( ) );
                    return workflowBean;
                } ).collect( Collectors.toList( ) );
    }

    /**
     * reset the _listIdBatchs list
     */
    public void resetListId( )
    {
        _listIdBatchs = new ArrayList<>( );
    }

    /**
     * Returns the form to create a batch
     *
     * @param request
     *            The Http request
     * @return the html code of the batch form
     */
    @View( VIEW_CREATE_BATCH )
    public String getCreateBatch( HttpServletRequest request )
    {
        _batch = ( _batch != null ) ? _batch : new Batch( );
        unregisterFeed( );

        Map<String, Object> model = getModel( );
        model.put( MARK_BATCH, _batch );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_BATCH ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_BATCH, TEMPLATE_CREATE_BATCH, model );
    }

    /**
     * Process the data capture form of a new batch
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_BATCH )
    public String doCreateBatch( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _batch, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_BATCH ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _batch, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_BATCH );
        }

        BatchHome.create( _batch );
        addInfo( INFO_BATCH_CREATED, getLocale( ) );
        resetListId( );

        _wfBatchBean = _wfBatchBeanService.createWorkflowBean( _batch, _batch.getId( ), getUser( ) );

        return redirectView( request, VIEW_MANAGE_BATCHS );
    }

    /**
     * Manages the removal form of a batch whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_BATCH )
    public String getConfirmRemoveBatch( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_BATCH ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_BATCH ) );
        url.addParameter( PARAMETER_ID_BATCH, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_BATCH, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a batch
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage batchs
     */
    @Action( ACTION_REMOVE_BATCH )
    public String doRemoveBatch( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_BATCH ) );

        BatchHome.remove( nId );
        addInfo( INFO_BATCH_REMOVED, getLocale( ) );
        resetListId( );

        _batch = null;
        _wfBatchBean = null;

        return redirectView( request, VIEW_MANAGE_BATCHS );
    }

    /**
     * Returns the form to update info about a batch
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_BATCH )
    public String getModifyBatch( HttpServletRequest request )
    {
        unregisterFeed( );
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_BATCH ) );

        if ( _batch == null || ( _batch.getId( ) != nId ) )
        {
            Optional<Batch> optBatch = BatchHome.findByPrimaryKey( nId );
            _batch = optBatch.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }

        if ( _wfBatchBean == null || _wfBatchBean.getResourceId( ) != nId )
        {
            _wfBatchBean = _wfBatchBeanService.createWorkflowBean( _batch, _batch.getId( ), getUser( ) );
        }

        _wfBatchBeanService.addHistory( _wfBatchBean, request, getLocale( ) );

        Map<String, Object> model = getModel( );
        model.put( MARK_BATCH, _wfBatchBean );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_BATCH ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_BATCH, TEMPLATE_MODIFY_BATCH, model );
    }

    /**
     * Process the change form of a batch
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_BATCH )
    public String doModifyBatch( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _batch, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_BATCH ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _batch, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_BATCH, PARAMETER_ID_BATCH, _batch.getId( ) );
        }

        BatchHome.update( _batch );
        addInfo( INFO_BATCH_UPDATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_BATCHS );
    }

    /**
     * Import a new batch
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @View( VIEW_IMPORT_BATCH )
    public String getViewImportBatch( HttpServletRequest request ) throws AccessDeniedException
    {
        registerFeed( );
        _batch = ( _batch != null ) ? _batch : new Batch( );

        final Map<String, Object> model = getModel( );
        model.put( MARK_NEW_IMPORT, true );
        model.put( MARK_BATCH, _batch );
        model.put( MARK_FEED_TOKEN, _feedToken );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_BATCH ) );

        return getPage( PROPERTY_PAGE_TITLE_IMPORT_BATCH, TEMPLATE_IMPORT_BATCH, model );
    }

    /**
     * Complete an existing batch
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @View( VIEW_COMPLETE_BATCH )
    public String getViewCompletetBatch( HttpServletRequest request ) throws AccessDeniedException
    {
        registerFeed( );
        final int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_BATCH ) );
        final Optional<Batch> batch = BatchHome.getBatch( nId );
        if ( !batch.isPresent( ) )
        {
            addError( "Aucun batch n'a pu être trouvé avec l'id " + nId );
            return redirectView( request, VIEW_MANAGE_BATCHS );
        }

        _batch = batch.get( );

        final Map<String, Object> model = getModel( );
        model.put( MARK_NEW_IMPORT, false );
        model.put( MARK_BATCH, _batch );
        model.put( MARK_FEED_TOKEN, _feedToken );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_BATCH ) );

        return getPage( PROPERTY_PAGE_TITLE_IMPORT_BATCH, TEMPLATE_IMPORT_BATCH, model );
    }

    /**
     * Process the data capture form of a new batch
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_IMPORT_BATCH )
    public String doImportBatch( HttpServletRequest request ) throws AccessDeniedException
    {
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_BATCH ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        int batchId;

        try
        {
            populate( _batch, request, getLocale( ) );
            final MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            final FileItem fileItem = multipartRequest.getFile( PARAMETER_CSV_FILE );

            final BatchDto batchDto = BatchService.instance( ).getDto( _batch );
            if ( StringUtils.isBlank( request.getParameter( PARAMETER_REFERENCE ) ) )
            {
                batchDto.setReference( UUID.randomUUID( ).toString( ) );
            }
            final List<IdentityDto> importedIdentities = CsvIdentityService.instance( ).read( fileItem.getInputStream( ) );
            batchDto.getIdentities( ).addAll( importedIdentities );
            // Check constraints
            if ( !validateBean( _batch, VALIDATION_ATTRIBUTES_PREFIX ) )
            {
                return redirectView( request, VIEW_CREATE_BATCH );
            }

            batchId = BatchService.instance( ).importBatch( batchDto, getUser( ), _feedToken );

            addInfo( INFO_BATCH_CREATED, getLocale( ) );
            resetListId( );
        }
        catch( IdentityStoreException | IOException e )
        {
            addError( e.getMessage( ) );
            return redirectView( request, VIEW_IMPORT_BATCH );
        }

        return redirect( request, VIEW_MODIFY_BATCH, PARAMETER_ID_BATCH, batchId );
    }

    /**
     * process a workflow action
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage batchs
     */
    @Action( ACTION_PROCESS_BATCH_WORKFLOW_ACTION )
    public String doProcessBatchAction( HttpServletRequest request )
    {
        final int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_BATCH ) );
        final int nAction = Integer.parseInt( request.getParameter( PARAMETER_ID_ACTION ) );
        final String appCode = request.getParameter( PARAMETER_FILTER_APP_CODE );

        if ( _batch == null || ( _batch.getId( ) != nId ) )
        {
            Optional<Batch> optBatch = BatchHome.findByPrimaryKey( nId );
            _batch = optBatch.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );

            _wfBatchBean = _wfBatchBeanService.createWorkflowBean( _batch, _batch.getId( ), getUser( ) );
        }

        _wfBatchBeanService.processAction( _wfBatchBean, nAction, request, getLocale( ) );
        _wfBatchBean = _wfBatchBeanService.createWorkflowBean( _batch, _batch.getId( ), getUser( ) );

        final Map<String, String> params = new HashMap<>( );
        params.put( PARAMETER_ID_BATCH_STATE, String.valueOf( _wfBatchBean.getState( ).getId( ) ) );
        params.put( PARAMETER_ID_BATCH, String.valueOf( nId ) );
        params.put( PARAMETER_FILTER_APP_CODE, appCode );

        return redirect( request, VIEW_MANAGE_IDENTITIES, params );
    }

    /**
     * process a workflow action
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage batchs
     */
    @Action( ACTION_PROCESS_IDENTITY_WORKFLOW_ACTION )
    public String doProcessIdentityAction( HttpServletRequest request )
    {
        int nResourceId = Integer.parseInt( request.getParameter( WorkflowBeanService.PARAMETER_RESOURCE_ID ) );
        int nActionId = Integer.parseInt( request.getParameter( WorkflowBeanService.PARAMETER_ACTION_ID ) );

        // check and refresh resource if necessary
        if ( _candidateidentity == null || ( _candidateidentity.getId( ) != nResourceId ) )
        {
            Optional<CandidateIdentity> optIdentity = CandidateIdentityHome.findByPrimaryKey( nResourceId );
            _candidateidentity = optIdentity.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );

            _wfCandidateIdentityBean = _wfIdentitiesBeanService.createWorkflowBean( _candidateidentity, _candidateidentity.getId( ),
                    _candidateidentity.getIdBatch( ), getUser( ) );
        }

        // Process the action, and redirect,
        // or redirect to the task form if exists

        if ( !_wfIdentitiesBeanService.existsTaskForm( nActionId, getLocale( ) ) )
        {
            // The task does not need a form
            _wfIdentitiesBeanService.processAction( _wfCandidateIdentityBean, nActionId, request, getLocale( ) );
            final int batchId = _wfCandidateIdentityBean.getExternalParentId( );
            Optional<Batch> optBatch = BatchHome.findByPrimaryKey( batchId );
            _batch = optBatch.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
            _wfBatchBean = _wfBatchBeanService.createWorkflowBean( _batch, _batch.getId( ), getUser( ) );

            final Map<String, String> params = new HashMap<>( );
            params.put( PARAMETER_ID_BATCH_STATE, String.valueOf( _wfBatchBean.getState( ).getId( ) ) );
            params.put( PARAMETER_ID_BATCH, String.valueOf( batchId ) );

            return redirect( request, VIEW_MANAGE_IDENTITIES, params );
        }
        else
            if ( request.getParameter( WorkflowBeanService.PARAMETER_SUBMITTED_TASK_FORM ) == null )
            {
                // A task form should be displayed
                return _wfIdentitiesBeanService.getTaskForm( _wfCandidateIdentityBean, nActionId, request, getLocale( ), JSP_MANAGE_CANDIDATEIDENTITIES,
                        ACTION_PROCESS_IDENTITY_WORKFLOW_ACTION );
            }
            else
            {
                // The task form was submitted
                final String errMsg = _wfIdentitiesBeanService.validateTaskForm( _wfCandidateIdentityBean, nActionId, request, getLocale( ) );
                if ( errMsg == null )
                {
                    return redirect( request, VIEW_IMPORT_CANDIDATEIDENTITY, PARAMETER_ID_CANDIDATEIDENTITY, _wfCandidateIdentityBean.getResourceId( ) );
                }
                else
                {
                    // error message
                    return redirect( request, errMsg );
                }
            }
    }

    private void globalInit( final HttpServletRequest request )
    {
        _wfBatchBean = null;
        _batch = null;
        _current_batch_state = null;
        _listIdBatchs = new ArrayList<>( );
        _currentBatchId = null;
        _listIdCandidateIdentities = new ArrayList<>( );
        _filterAppCode = request.getParameter( PARAMETER_FILTER_APP_CODE );
        _batchStates = BatchHome.getBatchStates( _filterAppCode );

        final Optional<String> idStateOpt = Optional.ofNullable( request.getParameter( PARAMETER_ID_BATCH_STATE ) );
        idStateOpt.ifPresent( idState -> {
            final Integer nCurrentBatchId = Integer.parseInt( idState );
            _current_batch_state = _batchStates.stream( ).filter( resourceState -> nCurrentBatchId.equals( resourceState.getId( ) ) ).findAny( ).orElse( null );
            _batchCurrentPage = Optional.ofNullable( request.getParameter( PARAMETER_BATCH_PAGE ) ).map( Integer::parseInt ).orElse( 1 );

            if ( _current_batch_state != null && _current_batch_state.getResourceCount( ) > 0 )
            {
                _listIdBatchs = BatchHome.getIdBatchsList( _current_batch_state, _filterAppCode );
                final int nbItemsPerPages = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 10 );
                final int totalRecords = _listIdBatchs.size( );
                _batchTotalPages = (int) Math.ceil( (double) totalRecords / nbItemsPerPages );
                if ( _batchTotalPages == 0 )
                {
                    _batchTotalPages = 1;
                }

                final int start = ( _batchCurrentPage - 1 ) * nbItemsPerPages;
                final int end = Math.min( start + nbItemsPerPages, totalRecords );
                _listIdBatchs = _listIdBatchs.subList( start, end );
            }
        } );
    }

    private Map<String, Object> globalModel( final HttpServletRequest request )
    {
        final Map<String, Object> model = new HashMap<>( );
        model.put( MARK_BATCH, _wfBatchBean );
        model.put( MARK_BATCH_LIST, this.getItemsFromIds( _listIdBatchs ) );
        model.put( MARK_CURRENT_BATCH_ID, _currentBatchId );
        model.put( MARK_BATCH_STATE_LIST, _batchStates );
        model.put( MARK_CURRENT_BATCH_STATE, _current_batch_state );
        model.put( MARK_CURRENT_BATCH_PAGE, _batchCurrentPage );
        model.put( MARK_BATCH_TOTAL_PAGES, _batchTotalPages );

        model.put( MARK_IDENTITY_LIST, this.getIdentitiesFromIds( _listIdCandidateIdentities, request ) );
        model.put( MARK_CURRENT_IDENTITIES_PAGE, _identitiesCurrentPage );
        model.put( MARK_IDENTITIES_TOTAL_PAGES, _identitiesTotalPages );

        model.put( MARK_FILTER_APP_CODE, _filterAppCode );
        return model;
    }

    private void unregisterFeed( )
    {
        _feedToken = null;
        progressManagerService.unRegisterFeed( FEED_NAME );
    }

    private void registerFeed( )
    {
        _feedToken = progressManagerService.registerFeed( FEED_NAME, 0 );
    }

    private RequestAuthor buildAuthor( )
    {
        final RequestAuthor author = new RequestAuthor( );
        author.setName( getUser( ).getEmail( ) );
        author.setType( AuthorType.application );
        return author;
    }
}
