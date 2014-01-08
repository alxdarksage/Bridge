<c:forEach var="group" items="${form.formStructure.children}">
    <c:choose>
        <c:when test="${group.children[0].children.size() == 0}">
            <div class="form well">
			    <table>
			        <thead>
			            <tr>
			                <th colspan="2"><h4><sage:form-label element="${group}"/></h4></th>
			            </tr>
			        </thead>
			        <tbody>
	                    <c:forEach var="row" items="${group.children}">
			                <tr>
			                    <td style="width: 60%"><sage:form-label element="${row}"/></td>
			                    <td>
                                    <sage:field field="${row}" dynamicForm="${dynamicForm}" defaultedFields="${defaultedFields}"/>
                                                <!-- 
<div class="input-group date" id="dp3" data-date="12-02-2012" data-date-format="dd-mm-yyyy">
    <input class="form-control" style="width:15rem!important" type="text" value="12-02-2012" readonly/>
    <span class="input-group-addon"><i class="glyphicon glyphicon-calendar"></i></span>
</div>
                                                     -->
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
