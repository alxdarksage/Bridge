<%@ include file="../directives.jsp" %>
<sage:minimal code="SetPassword" boxSize="30rem">
    <%--This is a field in the form but it isn't present on the screen, so, all this. --%>
    <spring:bind path="resetPasswordForm.token">
        <c:if test="${not empty status.errorMessages}">
            <div class="alert alert-danger">
                <form:errors id="token_errors" path="resetPasswordForm.token" htmlEscape="false"></form:errors>
            </div>
        </c:if>
    </spring:bind>
    <sage:formErrors formName="resetPasswordForm"/>
    <c:url var="resetPasswordUrl" value="/resetPassword.html"/>
    <form:form role="form" id="resetPasswordForm" modelAttribute="resetPasswordForm" method="post" action="${resetPasswordUrl}">
        <p><spring:message code="EnterPassword"/></p> 
        <sage:password field="password"/>
        <sage:password field="passwordConfirm"/>
        <input type="hidden" name="token" value="${param.token}"/>
        <sage:submit code="SetPassword"/> 
    </form:form>
</sage:minimal>