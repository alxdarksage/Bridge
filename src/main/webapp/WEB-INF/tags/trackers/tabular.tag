<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="element" required="true" type="org.sagebionetworks.bridge.webapp.specs.FormElement" %>

<h3>${element.label}</h3>
<c:choose>
    <c:when test="${not empty records}">
	    <table width="100%" class="smBottomSpaced">
	        <tr>
	            <c:forEach var="el" items="${element.children}">
	                <th>
	                    <sage:form-label field="${el}"/>
	                </th>
	            </c:forEach>
	        </tr>
            <c:forEach var="row" items="${records}">
	            <tr>
	                <c:forEach var="el" items="${element.children}">
                        <td style="width:${100 / fn:length(element.children)}%">
                            <c:set var="dynamicForm" value="${sage:valuesMapHolder(form.showStructure, row)}" scope="request" />
                            <sage:router element="${el}"/>
                        </td>
	                </c:forEach>
	            </tr>
            </c:forEach>
	    </table>
    </c:when>
    <c:otherwise>
        <p>There are no ${fn:toLowerCase(element.label)}.</p>    
    </c:otherwise>
</c:choose>
