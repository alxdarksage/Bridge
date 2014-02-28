<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="element" required="true" type="org.sagebionetworks.bridge.webapp.specs.FormElement" %>
<div class="form well">
    <table class="grid">
        <thead>
            <tr>
                <th><h4><sage:form-label field="${element}"/></h4></th>
                <c:forEach var="h" items="${element.headers}">
                    <th>${h}</th>
                </c:forEach>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="row" items="${element.children}">
                 <sage:router element="${row}"/>
            </c:forEach>
        </tbody>
    </table>
</div>   
