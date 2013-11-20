<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sage" tagdir="/WEB-INF/tags" %>
<sage:minimal code="SignUpForBridge" boxSize="40rem">
    <p><spring:message code="SignUpWelcome"/></p>
    
    <sage:formErrors formName="signUpForm"/>
    <c:url var="signUpUrl" value="/signUp.html"/>
    <form:form role="form" modelAttribute="signUpForm" method="post" action="${signUpUrl}">
        <sage:text field="displayName">
            <span class="help-block"><spring:message code="displayName.help"/></span>
        </sage:text>
        <sage:text field="email"/>
        <sage:submit code="SignUp"/>
        <sage:cancel url="${pageContext.request.origin}"/>
    </form:form>
</sage:minimal>
