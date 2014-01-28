<%@ include file="../directives.jsp" %>
<sage:minimal code="ResetPassword" boxSize="30rem">
    <sage:formErrors formName="requestResetPasswordForm"/>
    <c:url var="requestResetPasswordUrl" value="/requestResetPassword.html"/>
    <form:form role="form" modelAttribute="requestResetPasswordForm" method="post" action="${requestResetPasswordUrl}">
        <sage:text field="email" required="true">
            <span class="help-block">
                <spring:message code="ResetPassword.email.help"/>
            </span>
        </sage:text>
        <sage:submit code="ResetPassword"/>
        <sage:cancel url="${pageContext.request.origin}"/>
    </form:form>
</sage:minimal>