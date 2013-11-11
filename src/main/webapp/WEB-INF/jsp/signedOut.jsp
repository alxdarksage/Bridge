<%@ taglib prefix="sage" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<sage:minimal code="SignedOut" boxSize="30rem">
    <p><spring:message code="SignedOutWelcome"/></p>
    <sage:fullSignIn errorView="signedOut"/>
</sage:minimal>
