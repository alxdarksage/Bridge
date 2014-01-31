<%@ include file="../../directives.jsp" %>
<sage:journal code="MyJournal">
    <jsp:attribute name="content">
	    <div class="sage-crumbs">
	        <c:url var="journalUrl" value="/journal.html"/>
	        <a href="${journalUrl}"><spring:message code="MyJournal"/></a> &#187; 
	    </div>
        <c:choose>
            <c:when test="${spec.formLayout == 'ALL_RECORDS_ONE_PAGE_INLINE'}">
                <h3>${spec.name}</h3>
                <div class="bottomSpaced">
                    <sage:button href="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.id}/export.html"  
                        id="exportAct">Export (*.csv)</sage:button>
                </div>
                <sage:router element="${spec.editStructure}"/>
            </c:when>
            <c:otherwise>
                <h3>All ${spec.name}s</h3>
		        <sage:spec-table formId="dynamicForm" action="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.id}.html" 
		            items="${records}" caption="${form.description}s" specification="${spec}">
		            <c:choose>
                        <c:when test="${not descriptor.status.lastEntryComplete}">
		                    <sage:table-button id="resumeAct" type="primary" label="Finish Last Tracker"
		                        action="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.id}/resume.html"/>
		                    <sage:table-button id="newTrackerAct" type="default" label="New Tracker" 
		                        action="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.id}/new.html"/>
                        </c:when>
                        <c:otherwise>
		                    <sage:table-button id="newTrackerAct" type="primary" label="New Tracker" 
		                        action="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.id}/new.html"/>
                        </c:otherwise>
		            </c:choose>
		            <sage:table-button id="exportAct" type="default" label="Export (*.csv)"
		                action="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.id}/export.html"/>
		                
	                <sage:spec-column  
	                   link="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.id}/row/{id}.html"/>
	                   
                    <sage:table-button id="deleteAct" type="danger" label="Delete" action="delete" confirm="Are you sure you wish to delete this data?"/>
		        </sage:spec-table>
            </c:otherwise>
        </c:choose>
    </jsp:attribute>
</sage:journal>
