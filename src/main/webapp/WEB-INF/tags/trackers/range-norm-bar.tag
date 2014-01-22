<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="element" required="true" type="org.sagebionetworks.bridge.webapp.specs.FormElement" %>
<div class="rangeNormBar">
    <c:set var="hasRange" value="${not empty dynamicForm.valuesMap[element.lowKey] && not empty dynamicForm.valuesMap[element.highKey]}"/>
    <c:choose>
        <c:when test="${not empty dynamicForm.valuesMap[element.valueKey]}">
            <c:choose>
                <c:when test="${hasRange}">
		            <div class="measure"><span class="value has-range">${dynamicForm.valuesMap[element.valueKey]}</span><span class="unit has-range">${dynamicForm.valuesMap[element.unitKey]}</span></div>
		            <canvas></canvas>
		            <span class="range" data-lower="${dynamicForm.valuesMap[element.lowKey]}" data-upper="${dynamicForm.valuesMap[element.highKey]}">
		                ${dynamicForm.valuesMap[element.lowKey]}-${dynamicForm.valuesMap[element.highKey]}
		            </span>
                </c:when>
                <c:otherwise>
                    <div class="measure"><span class="value">${dynamicForm.valuesMap[element.valueKey]}</span><span class="unit">${dynamicForm.valuesMap[element.unitKey]}</span></div>
                    <canvas></canvas>
                </c:otherwise>
            </c:choose>
            <div class="measure"><span class="value">${dynamicForm.valuesMap[element.valueKey]}</span><span class="unit">${dynamicForm.valuesMap[element.unitKey]}</span></div>
            <canvas></canvas>
            <c:if test="${hasRange}">
                <span class="range" data-lower="${dynamicForm.valuesMap[element.lowKey]}" data-upper="${dynamicForm.valuesMap[element.highKey]}">
                    ${dynamicForm.valuesMap[element.lowKey]}-${dynamicForm.valuesMap[element.highKey]}
                </span>
            </c:if>
        </c:when>
        <c:otherwise>
            <div class="measure">
                <span class="unit">N/A</span>
            </div>
        </c:otherwise>
    </c:choose>
</div>
