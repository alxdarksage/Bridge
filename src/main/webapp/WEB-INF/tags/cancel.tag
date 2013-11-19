<%@ include file="../jsp/directives.jsp" %>
<%@ attribute name="url" required="true" %>
<c:url var="cancelUrl" value="${url}"/>
<a id="cancelAct" class="btn btn-sm" href="${cancelUrl}">
    <spring:message code="Cancel"/>
</a>
