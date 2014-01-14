<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="element" required="true" type="org.sagebionetworks.bridge.webapp.specs.FormElement" %>

<c:choose>
    <c:when test="${element.type == 'LIST'}">
        <c:forEach var="child" items="${element.children}">
            <sage:router element="${child}"/>
        </c:forEach>
    </c:when>
    <c:when test="${element.type == 'GROUP'}">
        <div class="form well">
            <table class="inline">
                <thead>
                    <tr>
                        <th colspan="2"><h4><sage:form-label field="${element}"/></h4></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="child" items="${element.children}">
                        <tr>
                            <td style="width: 60%">
                                <sage:form-label field="${child}"/>
                                <sage:form-errors field="${child}"/>
                            </td>
                            <td>
                                <sage:field field="${child}" dynamicForm="${dynamicForm}" defaultedFields="${defaultedFields}"/>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:when>
    <c:when test="${element.type == 'RANGE'}">
        <div class="multi">
            <sage:router element="${element.children[0]}"/>&mdash;<sage:router element="${element.children[1]}"/>
        </div> 
    </c:when>
    <c:when test="${element.type == 'GRID'}">
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
		                <tr>
                            <td>
                                <sage:form-label field="${row}"/>
                                <sage:form-errors fields="${row.children}"/>
                            </td>
                            <c:forEach var="col" items="${row.children}">
                                <td data-title="${col.label}: ">
                                    <sage:router element="${col}"/>
                                </td>
                            </c:forEach>
		                </tr>
		            </c:forEach>
		        </tbody>
		    </table>
        </div>   
    </c:when>
    <c:otherwise>
        <sage:field dynamicForm="${dynamicForm}" field="${element}" defaultedFields="${defaultedFields}"/>
    </c:otherwise>
</c:choose>
