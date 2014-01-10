<%@ include file="../../jsp/directives.jsp" %>
<c:if test="${(not empty descriptorsDue)}">
	<div class="alert alert-danger">
		<sage:comma-list first="Please fill out " items="${descriptorsDue}" separator=", " endSeparator=" and " last="?">
			<a href="/bridge/journal/${sessionScope.BridgeUser.ownerId}/forms/${item.id}.html">${item.name}</a
		></sage:comma-list>
	</div>
</c:if>
