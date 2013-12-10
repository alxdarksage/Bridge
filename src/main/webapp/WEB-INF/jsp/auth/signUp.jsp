<%@ include file="../directives.jsp" %>
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
