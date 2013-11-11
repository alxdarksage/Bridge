<%@ include file="directives.jsp" %>
<sage:minimal code="">
    <h3 id="error-pane">${requestScope.title}</h3>
    
    <p><spring:message code="ErrorWelcome"/></p>

    <p><spring:message code="Error.help"/></p>
    
    <script>console.error("${requestScope.message}");</script>        
</sage:minimal>
