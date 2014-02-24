<%@ include file="../../jsp/directives.jsp" %>
<c:if test="${not empty medicationsIfChanged}">
	<div class="reminder alert alert-success" id="${medicationsIfChanged.id}-med-span">
		<c:if test="${empty medications}">
			Are you taking any medications or supplements?
		</c:if>
		<c:if test="${not empty medications}">
			Are there any changes to your current medications or supplements?
			<ul>
				<c:forEach var="medication" items="${medications}" varStatus="loop">
					<li>${medication.data['dose'].value} ${medication.data['medication'].value}</li>
				</c:forEach>
			</ul>
		</c:if>
		<div class="table-buttons">
			<div>
				<a href="/bridge/journal/${sessionScope.BridgeUser.ownerId}/trackers/${medicationsIfChanged.id}.html" class="btn btn-sm btn-info">Yes</a>
				<a href="#" id="${medicationsIfChanged.id}-med" class="btn btn-sm btn-info">No</a>
			</div>
		</div>
	</div>
	<script type="text/javascript">
		$("#${medicationsIfChanged.id}-med").click( function() {
	         $.ajax({
	             success: function(data) {
	            	 $("#${medicationsIfChanged.id}-med-span").fadeOut(1000);
	             },
	             error: function(req,error,status) {
	            	 $("#${medicationsIfChanged.id}-med-span").text($("#${medicationsIfChanged.id}-med-span") + status +": " + error);
	             },
	             type: "GET",
	             url: "/bridge/medication/${medicationsIfChanged.id}/ajax/nochange.html"
	         });
	    });
	</script>
</c:if>
