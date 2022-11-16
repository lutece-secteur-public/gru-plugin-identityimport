<jsp:useBean id="managecandidateidentitiesattributesCandidateIdentityAttribute" scope="session" class="fr.paris.lutece.plugins.identityimport.web.CandidateIdentityAttributeJspBean" />
<% String strContent = managecandidateidentitiesattributesCandidateIdentityAttribute.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
