<%@ include file="../jsp/directives.jsp" %>
<h3><spring:message code="SignIn"/></h3>
<c:url var="signInUrl" value="/signIn.html"/>
<form:form role="form" modelAttribute="signInForm" method="post" action="${signInUrl}">
    <div class="form-group">
        <spring:message var="emailLabel" code="Email"/>
        <form:input cssClass="form-control input-sm" id="email" path="email" placeholder="${emailLabel}"/>
    </div>
    <div class="form-group">
        <spring:message var="passwordLabel" code="Password"/>
        <form:password cssClass="form-control input-sm" id="password" path="password" placeholder="${passwordLabel}"/>
    </div>
    <c:if test="${not empty param.login}">
        <div class="alert alert-danger has-error">
            <spring:message code="IncorrectLogin"/>
        </div>
    </c:if>
    <button type="submit" class="btn btn-sm btn-default"><spring:message code="SignIn"/></button>
    <a id="signUpLink" href='<c:url value="/signUp.html"/>' class="btn"><spring:message code="SignUp"/></a>
</form:form>
<p class="forgotLinkWrapper">
    <a id="forgotPasswordLink" href='<c:url value="/resetPassword.html"/>'><spring:message code="ForgotPassword"/></a>
</p>
<sage:oauth/>
