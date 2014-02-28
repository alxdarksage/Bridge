<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="element" required="true" type="org.sagebionetworks.bridge.webapp.specs.FormElement" %>
<tr>
    <td class="grid">
        <sage:form-label field="${element}"/>
        <sage:form-errors fields="${element.children}"/>
    </td>
    <c:forEach var="col" items="${element.children}">
        <td class="grid" data-title="${col.label}: ">
            <sage:router element="${col}"/>
        </td>
    </c:forEach>
</tr>