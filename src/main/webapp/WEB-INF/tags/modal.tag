<%@ include file="../jsp/directives.jsp" %>
<%@ attribute name="id" required="true" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="action" required="true" %>

<div id="${id}" class="modal">
	<div class="modal-dialog modal-sm">
		<div class="modal-content">
			<form action="${action}">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">${title}</h4>
				</div>
				<div class="modal-body">
	                <jsp:doBody/>
				</div>
			    <div class="modal-footer">
			        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
			        <button type="submit" class="ok btn btn-primary">Ok</button>
				</div>
			</form>
		</div>
	</div>
</div>
