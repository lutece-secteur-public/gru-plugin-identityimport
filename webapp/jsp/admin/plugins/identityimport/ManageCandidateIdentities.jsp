<jsp:useBean id="managecandidateidentitiesCandidateIdentity" scope="session" class="fr.paris.lutece.plugins.identityimport.web.CandidateIdentityJspBean" />
<% String strContent = managecandidateidentitiesCandidateIdentity.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
