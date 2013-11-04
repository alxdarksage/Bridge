<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<h3>Sign In</h3>
<c:url var="signInUrl" value="/signIn.html"/>
<form:form role="form" modelAttribute="signInForm" method="post" action="${signInUrl}">
    <div class="form-group">
        <form:input cssClass="form-control input-sm" id="email" path="email" placeholder="Email"/>
    </div>
    <div class="form-group">
        <form:password cssClass="form-control input-sm" id="password" path="password" placeholder="Password"/>
    </div>
    <input type="hidden" name="origin" value="${requestScope['origin']}"/>
    <c:if test="${not empty param.login}">
        <div class="alert alert-danger">Unable to sign you in.</div>
    </c:if>
    <button type="submit" class="btn btn-sm btn-default">Sign In</button>
    <a href='<c:url value="/signUp.html"/>' class="btn">Sign Up</a>
</form:form>
<p style="margin-top:1rem; margin-bottom: -.25rem">
    <a href='<c:url value="/resetPassword.html"/>'>I forgot my password</a>
</p>
