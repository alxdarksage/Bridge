<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="element" required="true" type="org.sagebionetworks.bridge.webapp.specs.FormElement" %>

<h3>${element.label}</h3>
<c:choose>
    <c:when test="${not empty requestScope[element.modelName]}">
	    <table id="${element.modelName}Table" width="100%" class="smBottomSpaced">
	        <tr>
	            <c:forEach var="el" items="${element.children}">
	                <th>
	                    <sage:form-label field="${el}"/>
	                </th>
	            </c:forEach>
	        </tr>
            <c:forEach var="row" items="${requestScope[element.modelName]}">
	            <tr data-id="${row.rowId}">
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
<script>
window.addEventListener("load", function() {
    var template = "<c:url value="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.id}/ajax/row/@/finish.html"/>";

    var finished = throttle(function(e) {
    	this.readonly = true;
    	var row = $(this).closest("tr"),
    	    rowId = row.data("id"),
    	    url = template.split("@").join(rowId),
    	    query = $(this).attr('name') + "=" + encodeURIComponent($(this).val());
        
    	humane.log("Updating your medications");
        $.ajax(url, {
            method: "post",
            data: query
        }).success(function(params) {
        	document.location.reload();
        });
    }, 500);
    $('#activeTable input').on('input', finished);
});
</script>