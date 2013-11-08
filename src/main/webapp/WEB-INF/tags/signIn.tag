<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sage" tagdir="/WEB-INF/tags" %>
<fmt:setBundle basename="messages"/>

<h3>Sign In</h3>
<c:url var="signInUrl" value="/signIn.html"/>
<form:form role="form" modelAttribute="signInForm" method="post" action="${signInUrl}">
    <div class="form-group">
        <form:input cssClass="form-control input-sm" id="email" path="email" placeholder="Email"/>
    </div>
    <div class="form-group">
        <form:password cssClass="form-control input-sm" id="password" path="password" placeholder="Password"/>
    </div>
    <c:if test="${not empty param.login}">
        <div class="alert alert-danger">
            <fmt:message key="IncorrectLogin"/>
        </div>
    </c:if>
    <button type="submit" class="btn btn-sm btn-default">Sign In</button>
    <a id="signUpLink" href='<c:url value="/signUp.html"/>' class="btn">Sign Up</a>
</form:form>
<p class="forgotLinkWrapper">
    <a id="forgotPasswordLink" href='<c:url value="/resetPassword.html"/>'>I forgot my password</a>
</p>
<sage:oauth/>
