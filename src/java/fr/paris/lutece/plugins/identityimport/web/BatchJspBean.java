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
import fr.paris.lutece.plugins.identityimport.service.BatchService;
import fr.paris.lutece.plugins.identityimport.wf.WorkflowBean;
import fr.paris.lutece.plugins.identityimport.wf.WorkflowBeanService;
import fr.paris.lutece.plugins.identitystore.v3.csv.CsvIdentityService;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.BatchDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.IdentityDto;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import fr.paris.lutece.plugins.progressmanager.service.ProgressManagerService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.html.AbstractPaginator;
import fr.paris.lutece.util.url.UrlItem;
import org.apache.commons.fileupload.FileItem;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private static final String TEMPLATE_CREATE_BATCH = "/admin/plugins/identityimport/create_batch.html";
    private static final String TEMPLATE_MODIFY_BATCH = "/admin/plugins/identityimport/modify_batch.html";
    private static final String TEMPLATE_PROCESS_BATCH = "/admin/plugins/identityimport/process_batch.html";

    // Parameters
    private static final String PARAMETER_ID_BATCH = "id";
    private static final String PARAMETER_ID_ACTION = "id_action";
    private static final String PARAMETER_CSV_FILE = "csvFile";
    private static final String MARK_FEED_TOKEN = "feed_token";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_IMPORT_BATCH = "identityimport.import_batch.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MANAGE_BATCHS = "identityimport.manage_batchs.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_BATCH = "identityimport.modify_batch.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_BATCH = "identityimport.create_batch.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_PROCESS_BATCH = "identityimport.process_batch.pageTitle";

    // Markers
    private static final String MARK_BATCH_LIST = "batch_list";
    private static final String MARK_BATCH = "batch";
    private static final String MARK_NEW_IMPORT = "newImport";

    private static final String JSP_MANAGE_BATCHS = "jsp/admin/plugins/identityimport/ManageBatchs.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_BATCH = "identityimport.message.confirmRemoveBatch";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "identityimport.model.entity.batch.attribute.";

    // Views
    private static final String VIEW_IMPORT_BATCH = "importBatch";
    private static final String VIEW_COMPLETE_BATCH = "completeBatch";
    private static final String VIEW_MANAGE_BATCHS = "manageBatchs";
    private static final String VIEW_CREATE_BATCH = "createBatch";
    private static final String VIEW_MODIFY_BATCH = "modifyBatch";
    private static final String VIEW_PROCESS_BATCH = "processBatch";

    // Actions
    private static final String ACTION_IMPORT_BATCH = "importBatch";
    private static final String ACTION_CREATE_BATCH = "createBatch";
    private static final String ACTION_MODIFY_BATCH = "modifyBatch";
    private static final String ACTION_REMOVE_BATCH = "removeBatch";
    private static final String ACTION_CONFIRM_REMOVE_BATCH = "confirmRemoveBatch";
    private static final String ACTION_PROCESS_WORKFLOW_ACTION = "processAction";

    // Infos
    private static final String INFO_BATCH_CREATED = "identityimport.info.batch.created";
    private static final String INFO_BATCH_UPDATED = "identityimport.info.batch.updated";
    private static final String INFO_BATCH_REMOVED = "identityimport.info.batch.removed";

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";

    // Workflow
    private static final String BATCH_WFBEANSERVICE = "identityimport.batch.wfbeanservice";
    private final WorkflowBeanService<Batch> _wfBeanService = SpringContextService.getBean( BATCH_WFBEANSERVICE );

    // Feed

    private static final String FEED_NAME = "importBatchFeed";
    private final ProgressManagerService progressManagerService = ProgressManagerService.getInstance( );

    // Session variable to store working values
    private Batch _batch;
    WorkflowBean<Batch> _wfBean;
    private List<Integer> _listIdBatchs;
    private String _feedToken;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_BATCHS, defaultView = true )
    public String getManageBatchs( HttpServletRequest request )
    {
        _batch = null;
        unregisterFeed( );

        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null || _listIdBatchs.isEmpty( ) )
        {
            _listIdBatchs = BatchHome.getIdBatchsList( );
        }

        Map<String, Object> model = getPaginatedListModel( request, MARK_BATCH_LIST, _listIdBatchs, JSP_MANAGE_BATCHS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_BATCHS, TEMPLATE_MANAGE_BATCHS, model );
    }

    /**
     * Get Items from Ids list
     * 
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
    @Override
    List<WorkflowBean<Batch>> getItemsFromIds( List<Integer> listIds )
    {
        List<Batch> listBatch = BatchHome.getBatchsListByIds( listIds );

        // keep original order
        return listBatch.stream( ).sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getId( ) ) ) )
                .map( b -> _wfBeanService.createWorkflowBean( b, b.getId( ), getUser( ) ) ).collect( Collectors.toList( ) );
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

        _wfBean = _wfBeanService.createWorkflowBean( _batch, _batch.getId( ), getUser( ) );

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
        _wfBean = null;

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

            _wfBean = _wfBeanService.createWorkflowBean( _batch, _batch.getId( ), getUser( ) );
        }

        _wfBeanService.addHistory( _wfBean, request, getLocale( ) );

        Map<String, Object> model = getModel( );
        model.put( MARK_BATCH, _wfBean );
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
        model.put( MARK_NEW_IMPORT, "true" );
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
        model.put( MARK_NEW_IMPORT, "false" );
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

        try
        {
            populate( _batch, request, getLocale( ) );
            final MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            final FileItem fileItem = multipartRequest.getFile( PARAMETER_CSV_FILE );

            final BatchDto batchDto = BatchService.instance( ).getDto( _batch );
            final List<IdentityDto> importedIdentities = CsvIdentityService.instance( ).read( fileItem.getInputStream( ) );
            batchDto.getIdentities( ).addAll( importedIdentities );
            // Check constraints
            if ( !validateBean( _batch, VALIDATION_ATTRIBUTES_PREFIX ) )
            {
                return redirectView( request, VIEW_CREATE_BATCH );
            }

            BatchService.instance( ).importBatch( batchDto, getUser( ), _feedToken );

            addInfo( INFO_BATCH_CREATED, getLocale( ) );
            resetListId( );
        }
        catch( IdentityStoreException | IOException e )
        {
            addError( e.getMessage( ) );
            return redirectView( request, VIEW_IMPORT_BATCH );
        }

        return redirect( request, VIEW_MODIFY_BATCH, PARAMETER_ID_BATCH, _batch.getId( ) );
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
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_BATCH ) );
        int nAction = Integer.parseInt( request.getParameter( PARAMETER_ID_ACTION ) );

        if ( _batch == null || ( _batch.getId( ) != nId ) )
        {
            Optional<Batch> optBatch = BatchHome.findByPrimaryKey( nId );
            _batch = optBatch.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );

            _wfBean = _wfBeanService.createWorkflowBean( _batch, _batch.getId( ), getUser( ) );
        }

        _wfBeanService.processAction( _wfBean, nAction, request, getLocale( ) );

        return redirect( request, VIEW_MODIFY_BATCH, PARAMETER_ID_BATCH, nId );
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
}
