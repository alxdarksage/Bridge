<%@ include file="../../directives.jsp" %>
<sage:journal code="MyJournal">
    <div class="sage-crumbs">
        <c:url var="journalUrl" value="/journal.html"/>
        <a href="${journalUrl}"><spring:message code="MyJournal"/></a> &#187; 
    </div>
    <h3>${descriptor.description}</h3>
    
    <sage:table formId="dataForm" action="/journal/${sessionScope.BridgeUser.ownerId}/forms/${descriptor.id}.html" itemId="id" items="${records}" caption="Participant Surveys">
        <sage:button id="newSurveyAct" type="primary" label="New Survey" action="/journal/${sessionScope.BridgeUser.ownerId}/forms/${descriptor.id}/new.html"/>
        <sage:column label="Name" field="name" link="/admin/communities/{id}.html" className="nowrap"/>
        <sage:column label="Description" field="description"/>
        <sage:column label="Created" field="createdOn"/>
        <sage:column label="" icon="eye-open" static="View" link="/communities/{id}.html" className="nowrap"/>
        <sage:button id="deleteAct" type="danger" label="Delete" action="delete" confirm="Are you sure you wish to delete this data?"/>
    </sage:table>
    
</sage:journal>
