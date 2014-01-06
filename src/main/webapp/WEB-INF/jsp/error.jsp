<%@ include file="directives.jsp" %>
<sage:minimal code="">
    <h3 id="error-pane"><spring:message code="${errorCode}"/></h3>

    <p><spring:message code="ErrorWelcome"/></p>

    <p><spring:message code="Error.help"/></p>

    <c:if test="${not empty message}">
        <p><em>(The server said: "${message}.")</em></p>
    </c:if>

    <c:if test="${isDevelop}">
        <div style="font-family: monospace; font-size: smaller; width: 100%; overflow: scroll; 
                border: 1px solid #555; padding: 1rem; height: 400px; margin-top: 4rem">
            <c:out value="${exception.message}"/>
            <c:forEach var="stackTraceElem" items="${exception.stackTrace}">
                <c:out value="${stackTraceElem}"/><br/>
            </c:forEach>
        </div> 
    </c:if>
</sage:minimal>
