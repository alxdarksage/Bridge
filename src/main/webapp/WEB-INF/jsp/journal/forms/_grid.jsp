<div class="form well">
    <table class="grid">
        <thead>
            <tr>
                <th><h4><sage:form-label element="${group}"/></h4></th>
                <th>Value</th>
                <th>Unit</th>
                <th>Range</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="row" items="${group.children}">
                <tr>
                    <td><sage:form-label element="${row}"/></td>
                    <td data-title="${row.children[0].label}: ">
                       <sage:field field="${row.children[0]}" dynamicForm="${dynamicForm}" defaultedFields="${defaultedFields}"/>    
                    </td>
                    <td data-title="${row.children[1].label}: ">
                        <sage:field field="${row.children[1]}" dynamicForm="${dynamicForm}" defaultedFields="${defaultedFields}"/>
                    </td>
                    <td class="multi" data-title="Low-High ${row.children[1].label}: ">
                        <sage:field field="${row.children[2]}" dynamicForm="${dynamicForm}" defaultedFields="${defaultedFields}"/>    
                        &mdash; 
                        <sage:field field="${row.children[3]}" dynamicForm="${dynamicForm}" defaultedFields="${defaultedFields}"/>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>
