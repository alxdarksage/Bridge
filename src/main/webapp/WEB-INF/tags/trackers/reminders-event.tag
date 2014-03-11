<%@ include file="../../jsp/directives.jsp" %>
<c:if test="${not empty events}">
	<form class="reminder event" id="${events.id}-event-span" action="/bridge/event/${events.id}/ajax/new">
		Thought or event:
		<input type="text" name="valuesMap['event-name']" value="">
			<button type="submit" class="btn btn-sm btn-info">Add</button>
			<div class="error"></div>
	</form>
	<script type="text/javascript">
	$(function(){
		$(".reminder.event").submit( function(event) {
			event.preventDefault();
			var form = $(event.target).closest("form");
			var url = form.attr("action") + ".html";
		    var serialized = form.serialize();
		    var today = new Date().toISOString().replace(/T.*/,"");
	        serialized += '&' + $.param({"valuesMap['event-start']":today}) + '&' + $.param({"valuesMap['event-end']":today});
		    $.ajax({
		        success: function(data) {
		        	document.location.reload();
		        },
		        error: function(req,error,status) {
		        	form.find(".error").text(status +": " + error);
		        },
		        type: "POST",
		        url: url,
		        data: serialized,
		        dataType: "json"
		    });
		});
	});
	</script>
</c:if>
