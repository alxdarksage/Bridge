<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="id" required="true" %>
<%@ attribute name="href" required="true" %>

<c:url var="finalUrl" value="${href}"/>
<a href="${finalUrl}" id="${id}" class="btn btn-sm btn-default"><jsp:doBody/></a>
