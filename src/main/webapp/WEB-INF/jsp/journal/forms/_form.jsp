<c:forEach var="group" items="${form.formStructure.children}">
    <c:choose>
        <c:when test="${group.children[0].children.size() == 0}">
            <div class="form well">
			    <table class="inline">
			        <thead>
			            <tr>
			                <th colspan="2"><h4><sage:form-label field="${group}"/></h4></th>
			            </tr>
			        </thead>
			        <tbody>
	                    <c:forEach var="field" items="${group.children}">
			                <tr>
			                    <td style="width: 60%">
                                    <sage:form-label field="${field}"/>
                                    <sage:form-errors field="${field}"/>
			                    </td>
			                    <td>
                                    <sage:field field="${field}" dynamicForm="${dynamicForm}" defaultedFields="${defaultedFields}"/>
  			                    </td>
			                </tr>
			            </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:when>
        <c:otherwise>
            <%@ include file="_grid.jsp" %>
        </c:otherwise>
    </c:choose>
</c:forEach>
<sage:submit code="Save"/>
<sage:cancel url="/journal/${sessionScope.BridgeUser.ownerId}/forms/${descriptor.id}.html"/>
