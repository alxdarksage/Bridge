<%@ include file="../jsp/directives.jsp" %>
<spring:bind path="signInForm">
    <c:if test="${not empty status.errorMessages}">
        <div class="alert alert-danger">
            <form:errors id="signInForm_errors" path="signInForm" htmlEscape="false"></form:errors>
        </div>
    </c:if>
</spring:bind>
<c:if test="${not empty param.login}">
    <div class="alert alert-danger">
        <spring:message code="IncorrectLogin"/>
    </div>
</c:if>
<c:url var="signInUrl" value="/signIn.html"/> <!-- Required for Spring form -->
<form:form role="form" modelAttribute="signInForm" method="post" action="${signInUrl}">
    <sage:text field="email"/>
    <sage:password field="password"/>
    <button type="submit" class="btn btn-default"><spring:message code="SignIn"/></button>
    <a id="signUpLink" href='<c:url value="/signUp.html"/>' class="btn"><spring:message code="SignUp"/></a> 
</form:form>
<p class="forgotLinkWrapper">
    <a id="forgotPasswordLink" href='<c:url value="/requestResetPassword.html"/>'><spring:message code="ForgotPassword"/></a>
</p>
<sage:oauth/>