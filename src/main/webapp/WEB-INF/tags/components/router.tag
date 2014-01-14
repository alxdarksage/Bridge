<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="element" required="true" type="org.sagebionetworks.bridge.webapp.specs.FormElement" %>
<%--If anyone knows of a way to dynamically reference a tag... --%>
<c:choose>
    <c:when test="${element.type == 'LIST'}">
        <sage:list element="${element}"/>
    </c:when>
    <c:when test="${element.type == 'RANGE_NORM_BAR'}">
        <sage:range-norm-bar element="${element}"/>
    </c:when>
    <c:when test="${element.type == 'GROUP'}">
        <sage:group element="${element}"/>
    </c:when>
    <c:when test="${element.type == 'RANGE'}">
        <sage:range element="${element}"/>
    </c:when>
    <c:when test="${element.type == 'GRID'}">
        <sage:grid element="${element}"/>
    </c:when>
    <c:when test="${element.type == 'VALUE'}">
        <%-- TODO: Formatting --%>
        ${dynamicForm.values[element.name]}
    </c:when>
    <c:otherwise>
        <sage:field dynamicForm="${dynamicForm}" field="${element}" defaultedFields="${defaultedFields}"/>
    </c:otherwise>
</c:choose>
