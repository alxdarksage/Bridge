<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="element" required="true" type="org.sagebionetworks.bridge.webapp.specs.FormElement" %>
<%--If anyone knows of a way to dynamically reference a tag... --%>
<c:choose>
    <c:when test="${element.UIType == 'LIST'}">
        <sage:list element="${element}"/>
    </c:when>
    <c:when test="${element.UIType == 'RANGE_NORM_BAR'}">
        <sage:range-norm-bar element="${element}"/>
    </c:when>
    <c:when test="${element.UIType == 'GROUP'}">
        <sage:group element="${element}"/>
    </c:when>
    <c:when test="${element.UIType == 'RANGE'}">
        <sage:range element="${element}"/>
    </c:when>
    <c:when test="${element.UIType == 'GRID'}">
        <sage:grid element="${element}"/>
    </c:when>
    <c:when test="${element.UIType == 'INLINE_EDITOR'}">
        <sage:inline-editor element="${element}"/>
    </c:when>
    <c:when test="${element.UIType == 'TABULAR'}">
        <sage:tabular element="${element}"/>
    </c:when>
    <c:when test="${element.UIType == 'VALUE'}">
        <sage:value valuesMapHolder="${dynamicForm}" field="${element}"/>
    </c:when>
    <c:otherwise>
        <sage:field valuesMapHolder="${dynamicForm}" field="${element}" defaultedFields="${defaultedFields}"/>
    </c:otherwise>
</c:choose>
