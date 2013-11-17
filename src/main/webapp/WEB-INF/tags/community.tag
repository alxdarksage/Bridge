<%@ include file="../jsp/directives.jsp" %>
<%@ attribute name="code" required="true" %>
<sage:main code="${code}">
    <jsp:attribute name="navigation">
        <ul class="list-group">
            <li class="active list-group-item">${community.name}</li>
            <c:if test="${sessionScope.BridgeUser.isAuthenticated()}">
                <li class="list-group-item"><a><spring:message code="MyJournal"/></a></li>
            </c:if>
            <li class="list-group-item"><a><spring:message code="Forums"/></a></li>
        </ul>
    </jsp:attribute>
    <jsp:attribute name="content"><jsp:doBody/></jsp:attribute>
</sage:main>
