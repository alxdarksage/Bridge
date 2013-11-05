<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sage" tagdir="/WEB-INF/tags" %>
<%@ attribute name="errorView" required="true" %>
<spring:bind path="signInForm">
    <c:if test="${not empty status.errorMessages}">
        <div class="alert alert-danger">
            <form:errors path="signInForm"></form:errors>
        </div>
    </c:if>
</spring:bind>
<c:url var="signInUrl" value="/signIn.html"/> <!-- Required for Spring form -->
<form:form role="form" modelAttribute="signInForm" method="post" action="${signInUrl}">
    <sage:text field="email" label="Email"/>
    <sage:password field="password" label="Password"/>
    <c:if test="${not empty param.login}">
        <div class="alert alert-danger">Unable to sign you in.</div>
    </c:if>
    <input type="hidden" name="errorView" value="${errorView}"/>
    <button type="submit" class="btn btn-default">Sign In</button> 
    <a class="btn" href='<c:url value="/resetPassword.html"/>'>I forgot my password</a>
</form:form>
<sage:oauth/>