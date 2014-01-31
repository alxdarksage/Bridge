<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="code" required="true" %>
<%@ attribute name="id" required="false" %>
<%@ attribute name="action" required="false" %>
<button type="submit" id="${id}" class="btn btn-sm btn-primary" name="${action}" value="${action}">
    <spring:message code="${code}"/>
</button>
