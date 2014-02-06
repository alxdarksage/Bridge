<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="element" required="true" type="org.sagebionetworks.bridge.webapp.specs.FormElement" %>

<c:url var="trackerUrl" value="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.id}/row/${inprogress.rowId}.html"/>
<c:url var="trackerAjaxUrl" value="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.id}/ajax/row/${inprogress.rowId}.html"/>
<form:form class="form-inline" role="form" modelAttribute="dynamicForm" method="post" action="${trackerUrl}">
    <c:forEach var="el" items="${element.children}">
		<div class="form-group smTopSpaced" style="width:${98 / fn:length(element.children)}%">
			<label><sage:form-label field="${el}"/></label>
            <sage:router element="${el}"/>
		</div>
    </c:forEach>
    <p class="help-block">We're saving your information. You can come back and finish this entry at any time.</p>
    <div class="topSpaced">
        <sage:submit id="saveAct" code="Save"/>
    </div>
</form:form>

<script>
window.addEventListener("DOMContentLoaded", function() {
    var url = "${trackerAjaxUrl}";
	var fields = $('#dynamicForm #medication,#dynamicForm #dose,#dynamicForm #dose_instructions,#dynamicForm #start_date,#dynamicForm #end_date');
	var values = {};
	
	setInterval(function() {
		fields.each(function() {
			if (values[this.id] != this.value) {
				values[this.id] = this.value;
		        var query = $(this).attr('name') + "=" + encodeURIComponent($(this).val());
		        $.ajax(url, { method: "post", data: query });
			}
		});
	}, 300);
	/*
	var append = throttle(function(e) {
		var query = $(this).attr('name') + "=" + encodeURIComponent($(this).val());
		console.log(query);
		$.ajax(url, { method: "post", data: query });
	},500);
	$('#dynamicForm #medication,#dynamicForm #dose,#dynamicForm #dose_instructions,#dynamicForm #start_date,#dynamicForm #end_date').on('input', append);
	*/
});
</script>