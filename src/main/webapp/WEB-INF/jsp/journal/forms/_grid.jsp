<div class="form well">
    <table class="grid">
        <thead>
            <tr>
                <th><h4>${group.label}</h4></th>
                <th>Value</th>
                <th>Unit</th>
                <th>Range</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="row" items="${group.children}">
                <tr>
                    <td>${row.label}</td>
                    <td data-title="${row.children[0].label}: ">
                       <form:input cssClass="form-control input-sm ${(anyDefaulted and not empty dynamicForm.values[row.children[0].name]) ? 'defaulted' : ''}" id="${row.children[0].name}" data-type="float" path="values['${row.children[0].name}']"/>    
                    </td>
                    <td data-title="${row.children[1].label}: ">
                        <c:choose>
                            <c:when test="${fn:length(row.children[1].enumeratedValues) == 1}">
                                <p class="form-control-static">${row.children[1].enumeratedValues[0]}</p>
                                <input type="hidden" id="${row.children[1].name}" name="${row.children[1].name}" 
                                    value="${row.children[1].enumeratedValues[0]}"/>
                            </c:when>
                            <c:otherwise>
                                <form:select id="${row.children[1].name}" path="values['${row.children[1].name}']" 
                                    cssClass="form-control input-sm ${(anyDefaulted and not empty dynamicForm.values[row.children[1].name]) ? 'defaulted' : ''}" items="${row.children[1].enumeratedValues}" />
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td class="multi" data-title="Low-High ${row.children[1].label}: ">
                        <form:input cssClass="form-control input-sm ${(anyDefaulted and not empty dynamicForm.values[row.children[2].name]) ? 'defaulted' : ''}" id="${row.children[2].name}" data-type="float" placeholder="low" path="values['${row.children[2].name}']"/>    
                        &mdash; 
                        <form:input cssClass="form-control input-sm ${(anyDefaulted and not empty dynamicForm.values[row.children[3].name]) ? 'defaulted' : ''}" id="${row.children[3].name}" data-type="float" placeholder="high" path="values['${row.children[3].name}']"/>    
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>
