<%@ include file="directives.jsp" %>
<sage:minimal code="ResetPassword" boxSize="30rem">
    <sage:formErrors formName="requestResetPasswordForm"/>
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
        <sage:submit code="ResetPassword"/>
        <sage:cancel url="${pageContext.request.origin}"/>
    </form:form>
</sage:minimal>