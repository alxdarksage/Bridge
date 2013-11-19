<%@ include file="directives.jsp" %>
<sage:minimal code="SetPassword" boxSize="30rem">
    <spring:bind path="resetPasswordForm">
        <c:if test="${not empty status.errorMessages}">
            <div class="alert alert-danger">
                <form:errors id="resetPasswordForm_errors" path="resetPasswordForm" htmlEscape="false"></form:errors>
            </div>
        </c:if>
    </spring:bind>
    <spring:bind path="resetPasswordForm.token">
        <c:if test="${not empty status.errorMessages}">
            <div class="alert alert-danger">
                <form:errors id="token_errors" path="resetPasswordForm.token" htmlEscape="false"></form:errors>
            </div>
        </c:if>
    </spring:bind>
    <c:url var="resetPasswordUrl" value="/resetPassword.html"/>
    <form:form role="form" id="resetPasswordForm" modelAttribute="resetPasswordForm" method="post" action="${resetPasswordUrl}">
        <p><spring:message code="EnterPassword"/></p> 
        <sage:password field="password"/>
        <sage:password field="passwordConfirm"/>
        <input type="hidden" name="token" value="${param.token}"/> 
        <button id="submitAct" type="submit" class="btn btn-default">
            <spring:message code="SetPassword"/>
        </button>
    </form:form>
</sage:minimal>