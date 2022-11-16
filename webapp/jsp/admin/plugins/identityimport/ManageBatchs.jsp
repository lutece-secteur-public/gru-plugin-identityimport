<jsp:useBean id="managebatchsBatch" scope="session" class="fr.paris.lutece.plugins.identityimport.web.BatchJspBean" />
<% String strContent = managebatchsBatch.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
