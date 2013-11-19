<%@ include file="../jsp/directives.jsp" %>
<%@ attribute name="code" required="true" %>
<button type="submit" class="btn btn-sm btn-default">
    <spring:message code="${code}"/>
</button>
