<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="element" required="true" type="org.sagebionetworks.bridge.webapp.specs.FormElement" %>
<c:forEach var="child" items="${element.children}">
    <sage:router element="${child}"/>
</c:forEach>