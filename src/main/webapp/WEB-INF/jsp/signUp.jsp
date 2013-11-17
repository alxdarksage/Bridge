<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sage" tagdir="/WEB-INF/tags" %>
<sage:minimal code="SignUpForBridge" boxSize="40rem">
    <p><spring:message code="SignUpWelcome"/></p>
    
    <c:url var="signUpUrl" value="/signUp.html"/>
    <spring:bind path="signUpForm">
        <c:if test="${not empty status.errorMessages}">
            <div class="alert alert-danger">
                <form:errors path="signUpForm" htmlEscape="false"></form:errors>
            </div>
        </c:if>
    </spring:bind>
    <form:form role="form" modelAttribute="signUpForm" method="post" action="${signUpUrl}">
        <spring:hasBindErrors name="*">
            <div class="alert alert-danger">
                <form:errors htmlEscape="false"></form:errors>
            </div>
        </spring:hasBindErrors>
        <sage:text field="displayName">
            <span class="help-block"><spring:message code="displayName.help"/></span>
        </sage:text>
        <sage:text field="email"/>
        <button type="submit" class="btn btn-sm btn-default">
            <spring:message code="SignUp"/>
        </button>
        <a class="btn" href='<c:url value="${pageContext.request.origin}"/>'>
            <spring:message code="Continue"/>
        </a>
    </form:form>
</sage:minimal>
