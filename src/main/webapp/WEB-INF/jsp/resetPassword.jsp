<%@ include file="directives.jsp" %>
<sage:minimal code="ResetPassword" boxSize="30rem">
    <spring:bind path="resetPasswordForm">
        <c:if test="${not empty status.errorMessages}">
            <div class="alert alert-danger">
                <form:errors path="resetPasswordForm" htmlEscape="false"></form:errors>
            </div>
        </c:if>
    </spring:bind>
    <c:url var="resetPasswordUrl" value="/resetPassword.html"/>
    <form:form role="form" modelAttribute="resetPasswordForm" method="post" action="${resetPasswordUrl}">
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
    </form:form>
</sage:minimal>