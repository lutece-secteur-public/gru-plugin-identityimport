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
package fr.paris.lutece.plugins.identityimport.wf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.identityimport.business.ResourceState;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.state.State;

public class WorkflowBean<T>
{

    private T _resource;
    private String _strResourceType;
    private int _nResourceId;
    private User _user;
    private int _nWorkflowKey;
    private int _nExternalParentId = -1;
    private State _state;
    private Collection<Action> _listActions;
    private String _strHistory;
    private int _nNbSubResource;
    private List<ResourceState> _listSubResourceStates = new ArrayList<>( );

    /**
     * constructor
     * 
     * @param resource
     * @param user
     * @param nWorkflowKey
     */
    public WorkflowBean( T resource, int nResourceId, User user, String strResourceType, int nWorkflowKey )
    {
        this._resource = resource;
        this._user = user;
        this._nResourceId = nResourceId;
        this._strResourceType = strResourceType;
        this._nWorkflowKey = nWorkflowKey;
    }

    /**
     * constructor with external parent id
     * 
     * @param resource
     * @param user
     * @param nWorkflowKey
     */
    public WorkflowBean( T resource, int nResourceId, User user, String strResourceType, int nWorkflowKey, int nExternalParentId )
    {
        this._resource = resource;
        this._user = user;
        this._nResourceId = nResourceId;
        this._strResourceType = strResourceType;
        this._nWorkflowKey = nWorkflowKey;
        this._nExternalParentId = nExternalParentId;
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
     * get the state of the resource
     * 
     * @return the current state
     */
    public State getState( )
    {
        return _state;
    }

    /**
     * get the available actions for the resource
     * 
     * @return the actions
     */
    public Collection<Action> getActions( )
    {
        return _listActions;
    }

    /**
     * get the workflow resource
     * 
     * @return the resource
     */
    public T getResource( )
    {
        return _resource;
    }

    /**
     * get workflow history of the resource
     * 
     * @param request
     * @param locale
     * 
     * @return the history
     */
    public String getHistory( )
    {
        return _strHistory;
    }

    /**
     * set state
     * 
     * @param _state
     */
    public void setState( State _state )
    {
        this._state = _state;
    }

    /**
     * set actions
     * 
     * @param _listActions
     */
    public void setActions( Collection<Action> _listActions )
    {
        this._listActions = _listActions;
    }

    /**
     * set history
     * 
     * @param _strHistory
     */
    public void setHistory( String _strHistory )
    {
        this._strHistory = _strHistory;
    }

    /**
     * get resource id
     * 
     * @return the id
     */
    public int getResourceId( )
    {
        return _nResourceId;
    }

    /**
     * get external parent id
     * 
     * @return the parent id
     */
    public int getExternalParentId( )
    {
        return _nExternalParentId;
    }

    /**
     * get user
     * 
     * @return the user
     */
    public User getUser( )
    {
        return _user;
    }

    public int getNbSubResource( )
    {
        return _nNbSubResource;
    }

    public void setNbSubResource( int _nNbSubResource )
    {
        this._nNbSubResource = _nNbSubResource;
    }

    public List<ResourceState> getSubResourceStates( )
    {
        return _listSubResourceStates;
    }

    public void setSubResourceStates( List<ResourceState> _listSubResourceStates )
    {
        this._listSubResourceStates = _listSubResourceStates;
    }
}
