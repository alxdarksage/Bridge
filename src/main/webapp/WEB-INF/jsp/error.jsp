<%@ include file="directives.jsp" %>
<sage:minimal code="">
    <h3 id="error-pane"><spring:message code="${requestScope.errorCode}"/></h3>
    
    <p><spring:message code="ErrorWelcome"/></p>

    <p><spring:message code="Error.help"/></p>
    
    <c:if test="${requestScope.message}">
        <p><em>(The server said: "${requestScope.message}.")</em></p>
    </c:if>
    
    <div style="display: none">
        <c:out value="${requestScope.exception.message}"/>
        <c:forEach var="stackTraceElem" items="${requestScope.exception.stackTrace}">
            <c:out value="${stackTraceElem}"/><br/>
        </c:forEach>
    </div> 

</sage:minimal>
