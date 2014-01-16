<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="element" required="true" type="org.sagebionetworks.bridge.webapp.specs.FormElement" %>
<div class="form well">
    <table class="group">
        <thead>
            <tr>
                <th colspan="2"><h4><sage:form-label field="${element}"/></h4></th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="child" items="${element.children}">
                <tr>
                    <td>
                        <sage:form-label field="${child}"/>
                        <sage:form-errors field="${child}"/>
                    </td>
                    <td>
                        <sage:router element="${child}"/>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>
