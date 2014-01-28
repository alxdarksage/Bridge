<%@ include file="../directives.jsp" %>
<sage:minimal code="SignUpForBridge" boxSize="40rem">
    <p><spring:message code="SignUpWelcome"/></p>
    
    <sage:formErrors formName="signUpForm"/>
    <c:url var="signUpUrl" value="/signUp.html"/>
    <form:form role="form" modelAttribute="signUpForm" method="post" action="${signUpUrl}">
        <sage:text field="userName" required="true">
            <span class="help-block"><spring:message code="userName.help"/></span>
        </sage:text>
        <sage:text field="email" required="true"/>
        <sage:submit code="SignUp"/>
        <sage:cancel url="${pageContext.request.origin}"/>
    </form:form>
</sage:minimal>
