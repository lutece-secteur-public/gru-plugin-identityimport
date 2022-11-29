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
package fr.paris.lutece.plugins.identityimport.wf;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.util.html.HtmlTemplate;

public class WorkflowBeanService<T> implements Serializable
{

	private static final long serialVersionUID = 4229558883756833136L;

	private static final String MARK_TASK_FORM = "task_form";
	private static final String MARK_TARGET_JSP = "target_jsp";
	private static final String MARK_RESOURCE_ID = "resource_id";
	private static final String MARK_RESOURCE_TYPE_ID = "resource_type_id";
	private static final String MARK_WORKFLOW_ACTION_ID = "action_id";
	private static final String TEMPLATE_TASKS_FORM_WORKFLOW = "admin/plugins/workflow/default_task_form_wrapper.html";

	public static final String PARAMETER_SUBMITTED_TASK_FORM = "submitted_task_form";
	public static final String PARAMETER_RESOURCE_ID = "id";
	public static final String PARAMETER_RESOURCE_TYPE_ID = "resource_type_id";
	public static final String PARAMETER_ACTION_ID = "action_id";
	public static final String PARAMETER_ID_PARAMETER = "parameter_ID";

	public static final String CONSTANT_PROCESS_WORKFLOW_ACTION = "processWorkflowAction";

	// instance variables
	private int _nWorkflowKey;
    private String _strResourceType;
    
    /**
     * constructor
     * 
     * @param strResourceType
     * @param nWorkflowKey
     */
    public WorkflowBeanService( String strResourceType, int nWorkflowKey )
    {
        this._strResourceType = strResourceType;
        this._nWorkflowKey = nWorkflowKey;
    }

    /**
     * Create a WorkflowBean and initialize it
     * 
     * @param resource
     * @param nResourceId
     * @param user
     * @return the WorkflowBean
     */
    public WorkflowBean<T> createWorkflowBean( T resource, int nResourceId, User user )
    {
    	return createWorkflowBean( resource, nResourceId, -1, user );
    }
    
    /**
     * Create a WorkflowBean and initialize it
     * 
     * @param resource
     * @param nResourceId
     * @param nExternalParentId
     * @param user
     * @return the workflow bean
     */
    public WorkflowBean<T> createWorkflowBean( T resource, int nResourceId, int nExternalParentId, User user )
    {
    	WorkflowBean<T> wfBean = new WorkflowBean<>( resource, nResourceId, user, 
    			_strResourceType, _nWorkflowKey, nExternalParentId);
    	
    	refresh( wfBean );
    	
    	return wfBean;
    }
    
    /**
     * get workflow resource type
     * 
     * @return the type
     */
    public String getResourceType( )
    {
        return _strResourceType;
    }

    /**
     * get workflow key
     * 
     * @return the key
     */
    public int getWorkflowKey( )
    {
        return _nWorkflowKey;
    }

    /**
     * Refresh (or initialize) the state of the resource and the available actions
     * 
     * @return the workflow resource
     */
    public WorkflowBean<T> refresh( WorkflowBean<T> wfBean )
    {
    	// set (or initialize) state
    	wfBean.setState( WorkflowService.getInstance( ).getState( wfBean.getResourceId( ), _strResourceType, _nWorkflowKey, -1 ) );
    	
    	// set available actions
    	wfBean.setActions( WorkflowService.getInstance( ).getActions( wfBean.getResourceId( ), _strResourceType, _nWorkflowKey, wfBean.getUser( ) ) );

        return wfBean;
    }

    /**
     * process action
     * 
     * @param workflowBean
     * @param nAction
     * @param request
     * @param locale
     * 
     */
    public void processAction( WorkflowBean<T> wfBean, int nAction, HttpServletRequest request, Locale locale )
    {
    	WorkflowService.getInstance( ).doProcessAction( wfBean.getResourceId( ), _strResourceType, nAction, 
    			wfBean.getExternalParentId( ), request, locale, false, wfBean.getUser( ) );

        refresh( wfBean );
    }

    
    /**
     * get task form HTML
     * 
     * @param wfBean
     * @param nAction
     * @param request
     * @param locale
     * @param strTargetJsp
     * @return the HTML
     */
    public String getTaskForm(  WorkflowBean<T> wfBean, int nAction, HttpServletRequest request, 
    		Locale locale, String strTargetJsp  )
    {
    	if ( WorkflowService.getInstance().isDisplayTasksForm(nAction, locale ) )
        {
    		// A task Form exists and should be displayed
        	String strHtmlTasksForm = WorkflowService.getInstance().getDisplayTasksForm( 
        			wfBean.getResourceId(), wfBean.getResourceType( ),  nAction, 
        			request, locale, wfBean.getUser( ) );
        	
        	Map<String, Object> model = new HashMap<>( );
            model.put( MARK_TASK_FORM, strHtmlTasksForm );
            model.put( MARK_TARGET_JSP, strTargetJsp );
            model.put( MARK_RESOURCE_ID, wfBean.getResourceId( ) );
            model.put( MARK_RESOURCE_TYPE_ID, wfBean.getResourceType( ) );
            model.put( MARK_WORKFLOW_ACTION_ID, nAction );
            
            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASKS_FORM_WORKFLOW, locale, model );
            
            return template.getHtml( );
        }
    	else 
    	{
    		throw new AppException( "The task doesn't require a form");
    	}
    }
    
    /**
     * validate Task form 
     * (and process the action if there is no error)
     * 
     * @param wfBean
     * @param nAction
     * @param request
     * @param locale
     * @return the error message url, null orherwise
     */
    public String validateTaskForm( WorkflowBean<T> wfBean, int nAction, HttpServletRequest request, 
    		Locale locale)
    {
    	if ( request.getParameter( PARAMETER_SUBMITTED_TASK_FORM ) != null )
    	{
    		// Submit the task Form and process the action
    		String errorMsgUrl = WorkflowService.getInstance().doSaveTasksForm( wfBean.getResourceId( ), wfBean.getResourceType( ), 
    				nAction, wfBean.getExternalParentId( ), request, locale, wfBean.getUser( ) );
    		refresh( wfBean );
    		
    		return errorMsgUrl;
    	}
    	else 
    	{
    		throw new AppException( "The task form wasn't submitted");
    	}
    }
    
    /**
     * Check  if the action contains a task that needs to display and submit a form
     * 
     * @param nAction
     * @param locale
     * @return true if a task require a form
     */
    public boolean existsTaskForm( int nAction, Locale locale)
    {
    	return WorkflowService.getInstance().isDisplayTasksForm( nAction, locale );
    }
    
    /**
     * process automatic action
     * 
     * @param nAction
     * @param request
     * @param locale
     * @param nExternalParentId
     * 
     */
    public void processAutomaticAction(WorkflowBean<T> wfBean, int nAction, HttpServletRequest request, Locale locale )
    {
    	WorkflowService.getInstance( ).doProcessAction( wfBean.getResourceId( ), _strResourceType, 
        		nAction, wfBean.getExternalParentId( ), request, locale, true, null );

        refresh( wfBean );
    }

    /**
     * refresh workflow history
     * 
     * @param request
     * @param locale
     */
    public void addHistory( WorkflowBean<T> wfBean, HttpServletRequest request, Locale locale )
    {
        wfBean.setHistory( WorkflowService.getInstance( ).getDisplayDocumentHistory( 
        		wfBean.getResourceId( ), _strResourceType, _nWorkflowKey, request,
                locale, wfBean.getUser( ) ) );
    }
}
