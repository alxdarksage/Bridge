<c:forEach var="entry" items="${form.displayRows}">
    <div class="form well">
        <table>
            <thead>
                <tr>
                    <th><h4>${entry.key}</h4></th>
                    <th>Value</th>
                    <th>Unit</th>
                    <th>Range</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="row" items="${entry.value}">
                    <tr>
                        <td>${row.label}</td>
                        <td>
                           <form:input cssClass="form-control input-sm" id="${row.valueField}" data-type="float" path="values['${row.valueField}']"/>    
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${fn:length(row.unitEnumeration) == 1}">
                                    <p class="form-control-static">${row.unitEnumeration[0]}</p>
                                    <input type="hidden" name="values['${row.unitField}']" value="${row.unitEnumeration[0]}"/>
                                </c:when>
                                <c:otherwise>
                                    <form:select path="values['${row.unitField}']" cssClass="form-control input-sm" items="${row.unitEnumeration}" />
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="multi">
                            <form:input cssClass="form-control input-sm" id="${row.lowRangeField}" data-type="float" placeholder="low" path="values['${row.lowRangeField}']"/>    
                            &mdash; 
                            <form:input cssClass="form-control input-sm" id="${row.highRangeField}" data-type="float" placeholder="high" path="values['${row.highRangeField}']"/>    
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</c:forEach>
<sage:submit code="Save"/>
<sage:cancel url="/journal/${sessionScope.BridgeUser.ownerId}/forms/${descriptor.id}.html"/>
