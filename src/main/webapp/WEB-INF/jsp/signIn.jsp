<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sage" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sgf" tagdir="/WEB-INF/tags/form" %>
<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layouts" %>
<layout:minimal title="Sign In" boxSize="30rem">
    <c:url var="signInUrl" value="/signIn.html"/>
    <form:form role="form" modelAttribute="signInForm" method="post" action="${signInUrl}">
        <sgf:text field="userName" label="User Name"/>
        <sgf:password field="password" label="Password"/>
        <input type="hidden" name="origin" value="${requestScope['origin']}"/>
        <c:if test="${not empty param.login}">
            <div class="alert alert-danger">Unable to sign you in.</div>
        </c:if>
        <button type="submit" class="btn btn-default">Sign In</button> 
        <a class="btn" href='<c:url value="/resetPassword.html"/>'>I forgot my password</a>
    </form:form>
</layout:minimal>