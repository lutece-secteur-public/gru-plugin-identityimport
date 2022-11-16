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
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentity;
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentityHome;
/**
 * This is the business class test for the object CandidateIdentity
 */
public class CandidateIdentityJspBeanTest extends LuteceTestCase
{
    private static final int IDBATCH1 = 1;
    private static final int IDBATCH2 = 2;
    private static final String CONNECTIONID1 = "ConnectionId1";
    private static final String CONNECTIONID2 = "ConnectionId2";
    private static final String CUSTOMERID1 = "CustomerId1";
    private static final String CUSTOMERID2 = "CustomerId2";

public void testJspBeans(  ) throws AccessDeniedException, IOException
	{	
     	MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockServletConfig config = new MockServletConfig();

		//display admin CandidateIdentity management JSP
		CandidateIdentityJspBean jspbean = new CandidateIdentityJspBean();
		String html = jspbean.getManageCandidateIdentitys( request );
		assertNotNull(html);

		//display admin CandidateIdentity creation JSP
		html = jspbean.getCreateCandidateIdentity( request );
		assertNotNull(html);

		//action create CandidateIdentity
		request = new MockHttpServletRequest();

		response = new MockHttpServletResponse( );
		AdminUser adminUser = new AdminUser( );
		adminUser.setAccessCode( "admin" );
		
        
        request.addParameter( "id_batch" , String.valueOf( IDBATCH1) );
        request.addParameter( "connection_id" , CONNECTIONID1 );
        request.addParameter( "customer_id" , CUSTOMERID1 );
		request.addParameter("action","createCandidateIdentity");
        request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "createCandidateIdentity" ));
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

		//display modify CandidateIdentity JSP
		request = new MockHttpServletRequest();
        request.addParameter( "id_batch" , String.valueOf( IDBATCH1) );
        request.addParameter( "connection_id" , CONNECTIONID1 );
        request.addParameter( "customer_id" , CUSTOMERID1 );
		List<Integer> listIds = CandidateIdentityHome.getIdCandidateIdentitysList();
        assertTrue( !listIds.isEmpty( ) );
        request.addParameter( "id", String.valueOf( listIds.get( 0 ) ) );
		jspbean = new CandidateIdentityJspBean();
		
		assertNotNull( jspbean.getModifyCandidateIdentity( request ) );	

		//action modify CandidateIdentity
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		
		adminUser = new AdminUser();
		adminUser.setAccessCode("admin");
		
        request.addParameter( "id_batch" , String.valueOf( IDBATCH2) );
        request.addParameter( "connection_id" , CONNECTIONID2 );
        request.addParameter( "customer_id" , CUSTOMERID2 );
		request.setRequestURI("jsp/admin/plugins/example/ManageCandidateIdentitys.jsp");
		//important pour que MVCController sache quelle action effectuer, sinon, il redirigera vers createCandidateIdentity, qui est l'action par défaut
		request.addParameter("action","modifyCandidateIdentity");
		request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "modifyCandidateIdentity" ));

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
		
		//get remove CandidateIdentity
		request = new MockHttpServletRequest();
        //request.setRequestURI("jsp/admin/plugins/example/ManageCandidateIdentitys.jsp");
        request.addParameter( "id", String.valueOf( listIds.get( 0 ) ) );
		jspbean = new CandidateIdentityJspBean();
		request.addParameter("action","confirmRemoveCandidateIdentity");
		assertNotNull( jspbean.getModifyCandidateIdentity( request ) );
				
		//do remove CandidateIdentity
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		request.setRequestURI("jsp/admin/plugins/example/ManageCandidateIdentityts.jsp");
		//important pour que MVCController sache quelle action effectuer, sinon, il redirigera vers createCandidateIdentity, qui est l'action par défaut
		request.addParameter("action","removeCandidateIdentity");
		request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "removeCandidateIdentity" ));
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
