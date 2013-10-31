<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sage" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sgf" tagdir="/WEB-INF/tags/form" %>
<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layouts" %>
<layout:minimal title="Reset Password" boxSize="30rem">
    <c:url var="resetPasswordUrl" value="/resetPassword.html"/>
    <form:form role="form" modelAttribute="signUpForm" method="post" action="${resetPasswordUrl}">
        <spring:hasBindErrors name="signUpForm">
            <div class="alert alert-danger">
                <form:errors></form:errors>
            </div>
        </spring:hasBindErrors>
        <spring:bind path="email">
            <div class="form-group ${status.error ? 'has-error' : ''}">
                <label class="control-label" for="email">Your email address</label>
                <form:input cssClass="form-control input-sm" id="email" path="email"/>
                <span class="help-block">Enter your email address, and we&#8217;ll mail you a link to reset your password.</span>
                <form:errors path="email" />
            </div>
        </spring:bind>
        <!-- <input type="hidden" name="origin" value="${requestScope['origin']}"/> -->
        <button type="submit" class="btn btn-default">Reset Password</button>
    </form:form>
</layout:minimal>