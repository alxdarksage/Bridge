<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="element" required="true" type="org.sagebionetworks.bridge.webapp.specs.FormElement" %>

<c:url var="trackerUrl" value="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.id}/new.html"/>
<form:form role="form" modelAttribute="dynamicForm" method="post" action="${trackerUrl}">
    <table width="100%" class="smBottomSpaced">
        <tr>
            <c:forEach var="el" items="${element.children}">
                <td>
                    <sage:form-label field="${el}"/>
                    <sage:form-errors field="${el}"/>
                </td>
            </c:forEach>
        </tr>
        <tr>
            <c:forEach var="el" items="${element.children}">
                <td><sage:router element="${el}"/></td>
            </c:forEach>
        </tr>
    </table>
    <sage:submit code="Save"/>
</form:form>
