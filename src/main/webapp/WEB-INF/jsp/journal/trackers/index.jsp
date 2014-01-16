<%@ include file="../../directives.jsp" %>
<sage:journal code="MyJournal">
    <jsp:attribute name="content">
	    <div class="sage-crumbs">
	        <c:url var="journalUrl" value="/journal.html"/>
	        <a href="${journalUrl}"><spring:message code="MyJournal"/></a> &#187; 
	    </div>
	    <h3>All ${spec.name}s</h3>
	    
	    <sage:table formId="dynamicForm" action="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.id}.html" 
	        itemId="id" items="${records}" caption="${form.description}s">
	        <sage:button id="newTrackerAct" type="primary" label="New Tracker" 
	            action="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.id}/new.html"/>

	            <sage:spec-column specification="${spec}" 
	               link="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.id}/row/{id}.html"/>
	        <%-- 
	        <sage:button id="deleteAct" type="danger" label="Delete" action="delete" confirm="Are you sure you wish to delete this data?"/>
	        --%>
	    </sage:table>
	    
	    <c:url var="exportUrl" value="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.id}/export.html"/>
	    <a class="btn btn-sm btn-default" href="${exportUrl}">Export (*.csv)</a>
	    
    </jsp:attribute>
</sage:journal>
