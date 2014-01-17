<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="element" required="true" type="org.sagebionetworks.bridge.webapp.specs.FormElement" %>
<div class="rangeNormBar">
    <c:set var="hasRange" value="${not empty dynamicForm.values[element.lowKey] && not empty dynamicForm.values[element.highKey]}"/>
    <c:choose>
        <c:when test="${not empty dynamicForm.values[element.valueKey]}">
            <c:choose>
                <c:when test="${hasRange}">
		            <div class="measure"><span class="value has-range">${dynamicForm.values[element.valueKey]}</span><span class="unit has-range">${dynamicForm.values[element.unitKey]}</span></div>
		            <canvas></canvas>
		            <span class="range" data-lower="${dynamicForm.values[element.lowKey]}" data-upper="${dynamicForm.values[element.highKey]}">
		                ${dynamicForm.values[element.lowKey]}-${dynamicForm.values[element.highKey]}
		            </span>
                </c:when>
                <c:otherwise>
                    <div class="measure"><span class="value">${dynamicForm.values[element.valueKey]}</span><span class="unit">${dynamicForm.values[element.unitKey]}</span></div>
                    <canvas></canvas>
                </c:otherwise>
            </c:choose>
            <div class="measure"><span class="value">${dynamicForm.values[element.valueKey]}</span><span class="unit">${dynamicForm.values[element.unitKey]}</span></div>
            <canvas></canvas>
            <c:if test="${hasRange}">
                <span class="range" data-lower="${dynamicForm.values[element.lowKey]}" data-upper="${dynamicForm.values[element.highKey]}">
                    ${dynamicForm.values[element.lowKey]}-${dynamicForm.values[element.highKey]}
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
