<%@ include file="directives.jsp" %>
<sage:minimal code="ResetPassword" boxSize="30rem">
    <spring:bind path="requestResetPasswordForm">
        <c:if test="${not empty status.errorMessages}">
            <div class="alert alert-danger">
                <form:errors path="requestResetPasswordForm" htmlEscape="false"></form:errors>
            </div>
        </c:if>
    </spring:bind>
    <c:url var="requestResetPasswordUrl" value="/requestResetPassword.html"/>
    <form:form role="form" modelAttribute="requestResetPasswordForm" method="post" action="${requestResetPasswordUrl}">
        <spring:bind path="email">
            <div class="form-group ${status.error ? 'has-error' : ''}">
                <label class="control-label" for="email"><spring:message code="ResetPassword.email"/></label>
                <form:input cssClass="form-control input-sm" id="email" path="email"/>
                <span class="help-block">
                    <spring:message code="ResetPassword.email.help"/>
                </span>
                <form:errors id="email_errors" path="email" htmlEscape="false"/>
            </div>
        </spring:bind>
        <button type="submit" class="btn btn-default">
            <spring:message code="ResetPassword"/>
        </button>
        <a class="btn btn-sm" href='<c:url value="/profile.html"/>'>
            <spring:message code="Cancel"/>
        </a>
    </form:form>
</sage:minimal>