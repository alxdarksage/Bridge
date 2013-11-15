<%@ include file="../directives.jsp" %>
<sage:community code="Admin Dashboard">
    <h3><spring:message code="${communityForm.formName}"/></h3>
    <spring:bind path="communityForm">
        <c:if test="${not empty status.errorMessages}">
            <div class="alert alert-danger">
                <form:errors path="communityForm" htmlEscape="false"></form:errors>
            </div>
        </c:if>
    </spring:bind>
    <c:url var="communityUrl" value="/admin/communities/${communityForm.formId}.html"/>
    <form:form role="form" modelAttribute="communityForm" method="post" action="${communityUrl}">
        <spring:hasBindErrors name="*">
            <div class="alert alert-danger">
                <form:errors htmlEscape="false"></form:errors>
            </div>
        </spring:hasBindErrors>
        
        <sage:text field="name"/>
        <sage:textarea field="description"/>
        <sage:hidden field="id"/>
        <button type="submit" class="btn btn-sm btn-primary">
            <spring:message code="Save"/>
        </button>
        <a href='<c:url value="/admin/communities.html"/>' class="btn btn-sm">
            Cancel
        </a>
    </form:form>
</sage:community>
