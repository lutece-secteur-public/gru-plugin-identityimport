<jsp:useBean id="manageclientsClient" scope="session" class="fr.paris.lutece.plugins.identityimport.web.ClientJspBean" />
<% String strContent = manageclientsClient.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
