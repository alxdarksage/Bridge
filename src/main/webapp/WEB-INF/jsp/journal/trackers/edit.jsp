<%@ include file="../../directives.jsp" %>
<sage:journal code="MyJournal">
    <jsp:attribute name="content">
	    <c:url var="journalUrl" value="/journal.html"/>
	    <c:url var="trackersUrl" value="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.id}.html"/>
	    <div class="sage-crumbs">
	        <a href="${journalUrl}"><spring:message code="MyJournal"/></a> &#187;
	        <a href="${trackersUrl}">All ${descriptor.name}s</a> 
	    </div>
	    <h3>${descriptor.name}</h3>
	
	    <c:url var="trackerUrl" value="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.id}/row/${rowId}.html"/>
	    <form:form role="form" modelAttribute="dynamicForm" method="post" action="${trackerUrl}">
            <sage:router element="${form.editStructure}"/>
            <sage:submit id="saveAct" code="Save" action="save"/>
            <sage:cancel url="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.id}.html"/>
	    </form:form> 
    </jsp:attribute>
</sage:journal>