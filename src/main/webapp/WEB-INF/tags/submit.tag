<%@ include file="../jsp/directives.jsp" %>
<%@ attribute name="code" required="true" %>
<button type="submit" class="btn btn-sm btn-primary">
    <spring:message code="${code}"/>
</button>
