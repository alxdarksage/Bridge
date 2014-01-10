<%@ include file="../../jsp/directives.jsp" %>
<script src="<c:url value="/assets/smiley-slider-gh-pages/smiley-slider.js"/>"></script>
<c:if test="${not empty descriptorsAlways}">
	<div class="alert alert-info">
		<c:forEach var="descriptor" items="${descriptorsAlways}">
			<a href="/bridge/journal/${sessionScope.BridgeUser.ownerId}/forms/${descriptor.dataDescriptor.id}.html">${descriptor.dataDescriptor.name}</a>
			<c:set var="id" value="id${descriptor.dataDescriptor.id}"/>
			<c:url var="formUrl" value="/journal/${sessionScope.BridgeUser.ownerId}/forms/${descriptor.dataDescriptor.id}"/>
			<form:form id="${id}-form" role="form" modelAttribute="dynamicForm" method="post" action="${formUrl}">
				<input type="hidden" id="${id}-form-rowId" name="rowId" value="${descriptor.rowId}"/>
				<c:forEach var="column" items="${descriptor.columnsWithData}" varStatus="loop">
					<c:if test="${(not empty column.columnDescriptor.type)}">
						<div>
							<sage:formtag formId="${id}-form" columnDescriptor="${column.columnDescriptor}" value="${column.previousValue}"/>
						</div>
					</c:if>
				</c:forEach>
			</form:form>
		</c:forEach>
	</div>
</c:if>
