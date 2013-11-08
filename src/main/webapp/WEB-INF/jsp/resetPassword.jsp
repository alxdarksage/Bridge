<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sage" tagdir="/WEB-INF/tags" %>
<sage:minimal title="Reset Password" boxSize="30rem">
    <spring:bind path="resetPasswordForm">
        <c:if test="${not empty status.errorMessages}">
            <div class="alert alert-danger">
                <form:errors path="resetPasswordForm"></form:errors>
            </div>
        </c:if>
    </spring:bind>
    <c:url var="resetPasswordUrl" value="/resetPassword.html"/>
    <form:form role="form" modelAttribute="resetPasswordForm" method="post" action="${resetPasswordUrl}">
        <spring:bind path="email">
            <div class="form-group ${status.error ? 'has-error' : ''}">
                <label class="control-label" for="email">Your email address</label>
                <form:input cssClass="form-control input-sm" id="email" path="email"/>
                <span class="help-block">Enter your email address, and we&#8217;ll mail you a link to reset your password.</span>
                <form:errors id="email_errors" path="email" />
            </div>
        </spring:bind>
        <button type="submit" class="btn btn-default">Reset Password</button>
    </form:form>
</sage:minimal>