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
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentityAttribute;
import fr.paris.lutece.plugins.identityimport.business.CandidateIdentityAttributeHome;
import java.sql.Date;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.portal.web.l10n.LocaleService;
/**
 * This is the business class test for the object CandidateIdentityAttribute
 */
public class CandidateIdentityAttributeJspBeanTest extends LuteceTestCase
{
    private static final String KEY1 = "Key1";
    private static final String KEY2 = "Key2";
    private static final String VALUE1 = "Value1";
    private static final String VALUE2 = "Value2";
    private static final String CERTPROCESS1 = "CertProcess1";
    private static final String CERTPROCESS2 = "CertProcess2";
	private static final Date CERTDATE1 = new Date( 1000000l );
    private static final Date CERTDATE2 = new Date( 2000000l );

public void testJspBeans(  ) throws AccessDeniedException, IOException
	{	
     	MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockServletConfig config = new MockServletConfig();

		//display admin CandidateIdentityAttribute management JSP
		CandidateIdentityAttributeJspBean jspbean = new CandidateIdentityAttributeJspBean();
		String html = jspbean.getManageCandidateIdentityAttributes( request );
		assertNotNull(html);

		//display admin CandidateIdentityAttribute creation JSP
		html = jspbean.getCreateCandidateIdentityAttribute( request );
		assertNotNull(html);

		//action create CandidateIdentityAttribute
		request = new MockHttpServletRequest();

		response = new MockHttpServletResponse( );
		AdminUser adminUser = new AdminUser( );
		adminUser.setAccessCode( "admin" );
		
        
        request.addParameter( "key" , KEY1 );
        request.addParameter( "value" , VALUE1 );
        request.addParameter( "cert_process" , CERTPROCESS1 );
        request.addParameter( "cert_date" , DateUtil.getDateString( CERTDATE1, LocaleService.getDefault( ) ) );
		request.addParameter("action","createCandidateIdentityAttribute");
        request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "createCandidateIdentityAttribute" ));
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

		//display modify CandidateIdentityAttribute JSP
		request = new MockHttpServletRequest();
        request.addParameter( "key" , KEY1 );
        request.addParameter( "value" , VALUE1 );
        request.addParameter( "cert_process" , CERTPROCESS1 );
        request.addParameter( "cert_date" , DateUtil.getDateString( CERTDATE1, LocaleService.getDefault( ) ) );
		List<Integer> listIds = CandidateIdentityAttributeHome.getIdCandidateIdentityAttributesList();
        assertTrue( !listIds.isEmpty( ) );
        request.addParameter( "id", String.valueOf( listIds.get( 0 ) ) );
		jspbean = new CandidateIdentityAttributeJspBean();
		
		assertNotNull( jspbean.getModifyCandidateIdentityAttribute( request ) );	

		//action modify CandidateIdentityAttribute
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		
		adminUser = new AdminUser();
		adminUser.setAccessCode("admin");
		
        request.addParameter( "key" , KEY2 );
        request.addParameter( "value" , VALUE2 );
        request.addParameter( "cert_process" , CERTPROCESS2 );
        request.addParameter( "cert_date" , DateUtil.getDateString( CERTDATE2, LocaleService.getDefault( ) ) );
		request.setRequestURI("jsp/admin/plugins/example/ManageCandidateIdentityAttributes.jsp");
		//important pour que MVCController sache quelle action effectuer, sinon, il redirigera vers createCandidateIdentityAttribute, qui est l'action par défaut
		request.addParameter("action","modifyCandidateIdentityAttribute");
		request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "modifyCandidateIdentityAttribute" ));

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
		
		//get remove CandidateIdentityAttribute
		request = new MockHttpServletRequest();
        //request.setRequestURI("jsp/admin/plugins/example/ManageCandidateIdentityAttributes.jsp");
        request.addParameter( "id", String.valueOf( listIds.get( 0 ) ) );
		jspbean = new CandidateIdentityAttributeJspBean();
		request.addParameter("action","confirmRemoveCandidateIdentityAttribute");
		assertNotNull( jspbean.getModifyCandidateIdentityAttribute( request ) );
				
		//do remove CandidateIdentityAttribute
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		request.setRequestURI("jsp/admin/plugins/example/ManageCandidateIdentityAttributets.jsp");
		//important pour que MVCController sache quelle action effectuer, sinon, il redirigera vers createCandidateIdentityAttribute, qui est l'action par défaut
		request.addParameter("action","removeCandidateIdentityAttribute");
		request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "removeCandidateIdentityAttribute" ));
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
