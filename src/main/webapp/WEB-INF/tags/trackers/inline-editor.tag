<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="element" required="true" type="org.sagebionetworks.bridge.webapp.specs.FormElement" %>

<c:url var="trackerUrl" value="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.id}/row/${inprogress.rowId}.html"/>
<c:url var="trackerAjaxUrl" value="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.id}/ajax/row/${inprogress.rowId}.html"/>
<form:form role="form" modelAttribute="dynamicForm" method="post" action="${trackerUrl}">
    <table width="100%" class="smBottomSpaced">
        <tr>
            <td></td>
            <c:forEach var="el" items="${element.children}">
                <td>
                    <sage:form-label field="${el}"/>
                    <sage:form-errors field="${el}"/>
                </td>
            </c:forEach>
        </tr>
        <tr>
            <td>${inprogress.rowId}</td>
            <c:forEach var="el" items="${element.children}">
                <td><sage:router element="${el}"/></td>
            </c:forEach>
        </tr>
    </table>
    <sage:submit code="Save"/>
</form:form>
<script>
window.addEventListener("load", function() {
	var url = "${trackerAjaxUrl}";
	var append = throttle(function(e) {
		var query = $(this).attr('name') + "=" + encodeURIComponent($(this).val());
		console.log(query);
		$.ajax(url, { method: "post", data: query });
	},500);
	$('#dynamicForm #medication,#dynamicForm #dose,#dynamicForm #dose_instructions,#dynamicForm #start_date,#dynamicForm #end_date').on('input', append);
});
</script>