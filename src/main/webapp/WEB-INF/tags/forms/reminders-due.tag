<%@ include file="../../jsp/directives.jsp" %>
<c:if test="${(not empty descriptorsDue)}">
	<div class="reminder-due">
		<sage:comma-list first="Please fill out " items="${descriptorsDue}" separator=", " endSeparator=" and " last="?">
			<a href="/bridge/journal/${sessionScope.BridgeUser.ownerId}/forms/${item.id}.html">
					${(not empty item.description) ? item.description : item.name}</a
		></sage:comma-list>
	</div>
</c:if>
