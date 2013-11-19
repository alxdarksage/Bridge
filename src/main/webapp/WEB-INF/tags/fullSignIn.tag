<%@ include file="../jsp/directives.jsp" %>

<sage:formErrors formName="signInForm"/>
<c:url var="signInUrl" value="/signIn.html"/> <!-- Required for Spring form -->
<form:form role="form" modelAttribute="signInForm" method="post" action="${signInUrl}">
    <sage:text field="email"/>
    <sage:password field="password"/>
    <input type="hidden" name="origin" value="${requestScope.origin}"/>
    <sage:submit code="SignIn"/>
    <a id="signUpLink" href='<c:url value="/signUp.html"/>' class="btn"><spring:message code="SignUp"/></a> 
</form:form>
<p class="forgotLinkWrapper">
    <a id="forgotPasswordLink" href='<c:url value="/requestResetPassword.html"/>'><spring:message code="ForgotPassword"/></a>
</p>
<sage:oauth/>