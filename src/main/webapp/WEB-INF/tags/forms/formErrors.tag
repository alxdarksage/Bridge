<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="formName" required="true" %>
<spring:bind path="${formName}">
    <c:if test="${not empty status.errorMessages}">
        <div class="alert alert-danger">
            <form:errors id="${formName}_errors" path="${formName}" htmlEscape="false"></form:errors>
        </div>
    </c:if>
</spring:bind>
