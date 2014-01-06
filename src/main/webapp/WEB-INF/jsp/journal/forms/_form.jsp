<c:forEach var="group" items="${form.formStructure.children}">
    <c:choose>
        <c:when test="${group.children[0].children.size() == 0}">
            <div class="form well">
			    <table>
			        <thead>
			            <tr>
			                <th colspan="2"><h4>${group.label}</h4></th>
			            </tr>
			        </thead>
			        <tbody>
	                    <c:forEach var="row" items="${group.children}">
			                <tr>
			                    <td style="width: 60%">${row.label}</td>
			                    <td>
	                                <c:choose>
	                                    <c:when test="${row.getClass().getSimpleName() == 'EnumeratedFormField'}">
	                                        <c:choose>
			                                    <c:when test="${fn:length(row.enumeratedValues) == 1}">
			                                        <p class="form-control-static">${row.enumeratedValues[0]}</p>
			                                        <input type="hidden" id="${row.name}" name="${row.name}" 
			                                            value="${row.enumeratedValues[0]}"/>
			                                    </c:when>
			                                    <c:otherwise>
			                                        <form:select id="${row.name}" path="values['${row.name}']" 
			                                            cssClass="form-control input-sm" items="${row.enumeratedValues}" />
			                                    </c:otherwise>
	                                        </c:choose>
	                                    </c:when>
	                                    <c:otherwise>
	                                       <form:input cssClass="form-control input-sm" id="${row.name}" 
	                                            data-type="float" path="values['${row.name}']"/>
	                                            
<div class="input-group date" id="dp3" data-date="12-02-2012" data-date-format="dd-mm-yyyy">
	<input class="form-control" style="width:15rem!important" type="text" value="12-02-2012" readonly/>
	<span class="input-group-addon"><i class="glyphicon glyphicon-calendar"></i></span>
</div>
              	                                            
	                                    </c:otherwise>
	                                </c:choose>
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
