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
 * SUBSTITUTE GOODS OR SERVICES LOSS OF USE, DATA, OR PROFITS OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */

package fr.paris.lutece.plugins.identityimport.web;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminAuthenticationService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import java.util.List;
import java.io.IOException;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.plugins.identityimport.business.Client;
import fr.paris.lutece.plugins.identityimport.business.ClientHome;
/**
 * This is the business class test for the object Client
 */
public class ClientJspBeanTest extends LuteceTestCase
{
    private static final String NAME1 = "Name1";
    private static final String NAME2 = "Name2";
    private static final String APPCODE1 = "AppCode1";
    private static final String APPCODE2 = "AppCode2";
    private static final String TOKEN1 = "Token1";
    private static final String TOKEN2 = "Token2";

public void testJspBeans(  ) throws AccessDeniedException, IOException
	{	
     	MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockServletConfig config = new MockServletConfig();

		//display admin Client management JSP
		ClientJspBean jspbean = new ClientJspBean();
		String html = jspbean.getManageClients( request );
		assertNotNull(html);

		//display admin Client creation JSP
		html = jspbean.getCreateClient( request );
		assertNotNull(html);

		//action create Client
		request = new MockHttpServletRequest();

		response = new MockHttpServletResponse( );
		AdminUser adminUser = new AdminUser( );
		adminUser.setAccessCode( "admin" );
		
        
        request.addParameter( "name" , NAME1 );
        request.addParameter( "app_code" , APPCODE1 );
        request.addParameter( "token" , TOKEN1 );
		request.addParameter("action","createClient");
        request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "createClient" ));
		request.setMethod( "POST" );
        
		
		try 
		{
			AdminAuthenticationService.getInstance( ).registerUser(request, adminUser);
			html = jspbean.processController( request, response ); 
			
			
			// MockResponse object does not redirect, result is always null
			assertNull( html );
		}
		catch (AccessDeniedException e)
		{
			fail("access denied");
		}
		catch (UserNotSignedException e) 
		{
			fail("user not signed in");
		}

		//display modify Client JSP
		request = new MockHttpServletRequest();
        request.addParameter( "name" , NAME1 );
        request.addParameter( "app_code" , APPCODE1 );
        request.addParameter( "token" , TOKEN1 );
		List<Integer> listIds = ClientHome.getIdClientsList();
        assertTrue( !listIds.isEmpty( ) );
        request.addParameter( "id", String.valueOf( listIds.get( 0 ) ) );
		jspbean = new ClientJspBean();
		
		assertNotNull( jspbean.getModifyClient( request ) );	

		//action modify Client
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		
		adminUser = new AdminUser();
		adminUser.setAccessCode("admin");
		
        request.addParameter( "name" , NAME2 );
        request.addParameter( "app_code" , APPCODE2 );
        request.addParameter( "token" , TOKEN2 );
		request.setRequestURI("jsp/admin/plugins/example/ManageClients.jsp");
		//important pour que MVCController sache quelle action effectuer, sinon, il redirigera vers createClient, qui est l'action par défaut
		request.addParameter("action","modifyClient");
		request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "modifyClient" ));

		try 
		{
			AdminAuthenticationService.getInstance( ).registerUser(request, adminUser);
			html = jspbean.processController( request, response );

			// MockResponse object does not redirect, result is always null
			assertNull( html );
		}
		catch (AccessDeniedException e)
		{
			fail("access denied");
		}
		catch (UserNotSignedException e) 
		{
			fail("user not signed in");
		}
		
		//get remove Client
		request = new MockHttpServletRequest();
        //request.setRequestURI("jsp/admin/plugins/example/ManageClients.jsp");
        request.addParameter( "id", String.valueOf( listIds.get( 0 ) ) );
		jspbean = new ClientJspBean();
		request.addParameter("action","confirmRemoveClient");
		assertNotNull( jspbean.getModifyClient( request ) );
				
		//do remove Client
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		request.setRequestURI("jsp/admin/plugins/example/ManageClientts.jsp");
		//important pour que MVCController sache quelle action effectuer, sinon, il redirigera vers createClient, qui est l'action par défaut
		request.addParameter("action","removeClient");
		request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "removeClient" ));
		request.addParameter( "id", String.valueOf( listIds.get( 0 ) ) );
		request.setMethod("POST");
		adminUser = new AdminUser();
		adminUser.setAccessCode("admin");

		try 
		{
			AdminAuthenticationService.getInstance( ).registerUser(request, adminUser);
			html = jspbean.processController( request, response ); 

			// MockResponse object does not redirect, result is always null
			assertNull( html );
		}
		catch (AccessDeniedException e)
		{
			fail("access denied");
		}
		catch (UserNotSignedException e) 
		{
			fail("user not signed in");
		}	
     
     }
}
