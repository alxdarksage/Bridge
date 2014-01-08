<%@ include file="../../jsp/directives.jsp" %>
<c:if test="${(not empty descriptorsIfNew || not empty descriptorsIfChanged)}">
	<div class="reminder-always">
		<sage:comma-list first="Do you have any new " items="${descriptorsIfNew}" separator=", " endSeparator=" or " last="?">
			<a href="/bridge/journal/${sessionScope.BridgeUser.ownerId}/forms/${item.id}.html">
					${(not empty item.description) ? item.description : item.name}</a
		></sage:comma-list>
		<sage:comma-list first="Are there any changes for " items="${descriptorsIfChanged}" separator=", " endSeparator=" or " last="?">
			<a href="/bridge/journal/${sessionScope.BridgeUser.ownerId}/forms/${item.id}.html">
					${(not empty item.description) ? item.description : item.name}</a
		></sage:comma-list>
	</div>
</c:if>
