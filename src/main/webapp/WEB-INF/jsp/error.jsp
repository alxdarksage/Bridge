<%@ include file="directives.jsp" %>
<sage:minimal code="">
    <h3 id="error-pane"><spring:message code="${requestScope.title}"/></h3>
    
    <p><spring:message code="ErrorWelcome"/></p>

    <p><spring:message code="Error.help"/></p>
    
    <c:if test="${requestScope.message}">
        <p><em>(The server said: "${requestScope.message}.")</em></p>
    </c:if>
    
</sage:minimal>
