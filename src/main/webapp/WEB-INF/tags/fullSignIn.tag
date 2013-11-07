<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sage" tagdir="/WEB-INF/tags" %>
<fmt:setBundle basename="messages"/>
<%@ attribute name="errorView" required="true" %>

<spring:bind path="signInForm">
    <c:if test="${not empty status.errorMessages}">
        <div class="alert alert-danger">
            <form:errors id="signInForm_errors" path="signInForm"></form:errors>
        </div>
    </c:if>
</spring:bind>
    <c:if test="${not empty param.login}">
        <div class="alert alert-danger">
            <fmt:message key="IncorrectLogin"/>
        </div>
    </c:if>
<c:url var="signInUrl" value="/signIn.html"/> <!-- Required for Spring form -->
<form:form role="form" modelAttribute="signInForm" method="post" action="${signInUrl}">
    <sage:text field="email" label="Email"/>
    <sage:password field="password" label="Password"/>
    <input type="hidden" name="errorView" value="${errorView}"/>
    <button type="submit" class="btn btn-default">Sign In</button>
    <a id="signUpLink" href='<c:url value="/signUp.html"/>' class="btn">Sign Up</a> 
</form:form>
<p class="forgotLinkWrapper">
    <a id="forgotPasswordLink" href='<c:url value="/resetPassword.html"/>'>I forgot my password</a>
</p>
<sage:oauth/>