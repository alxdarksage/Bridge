<%@ include file="../../jsp/directives.jsp" %>
<script src="<c:url value="/assets/smiley-slider-gh-pages/smiley-slider.js"/>"></script>
<c:if test="${not empty descriptorsAlways}">
	<div class="list-group-item">
		<c:forEach var="descriptor" items="${descriptorsAlways}">
<%-- 			<a href="/bridge/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.descriptor.id}.html">${descriptor.descriptor.name}</a> --%>
			<c:set var="id" value="id${descriptor.descriptor.id}"/>
			<c:url var="formUrl" value="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.descriptor.id}"/>
			<form:form id="${id}-form" role="form" modelAttribute="dynamicForm" method="post" action="${formUrl}">
				<input type="hidden" id="${id}-form-rowId" name="rowId" value="${descriptor.currentData.rowId}"/>
				<c:forEach var="column" items="${descriptor.columns}" varStatus="loop">
					<c:if test="${(not empty column.type)}">
						<sage:formtag trackerId="${id}-form" columnDescriptor="${column}"
							currentValue="${descriptor.currentData.data[column.name]}"
							previousValue="${descriptor.previousData.data[column.name]}"/>
					</c:if>
				</c:forEach>
			</form:form>
		</c:forEach>
	</div>
</c:if>
