<%@ include file="../../directives.jsp" %>
<sage:journal code="MyJournal">
    <jsp:attribute name="content">
	    <div class="sage-crumbs">
	        <c:url var="journalUrl" value="/journal.html"/>
	        <a href="${journalUrl}"><spring:message code="MyJournal"/></a> &#187; 
	    </div>
	    <h3>All "${descriptor.description}" Surveys</h3>
	    <sage:table formId="dynamicForm" action="/journal/${sessionScope.BridgeUser.ownerId}/forms/${descriptor.id}.html" 
	        itemId="id" items="${records}" caption="Participant Surveys">
	        <sage:button id="newSurveyAct" type="primary" label="New Survey" 
	            action="/journal/${sessionScope.BridgeUser.ownerId}/forms/${descriptor.id}/new.html"/>
	        <sage:column label="Created" field="createdOn" 
	            link="/journal/${sessionScope.BridgeUser.ownerId}/forms/${descriptor.id}/row/{id}.html" className="nowrap"/>
	        <sage:column label="Last Modified" field="modifiedOn" className="nowrap"/>
	        <%-- 
	        <sage:button id="deleteAct" type="danger" label="Delete" action="delete" confirm="Are you sure you wish to delete this data?"/>
	        --%>
	    </sage:table>
    </jsp:attribute>
</sage:journal>
