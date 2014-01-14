<%@ include file="../../directives.jsp" %>
<sage:journal code="MyJournal">
    <jsp:attribute name="content">
        <c:url var="journalUrl" value="/journal.html"/>
        <c:url var="formsUrl" value="/journal/${sessionScope.BridgeUser.ownerId}/forms/${descriptor.id}.html"/>
        <div class="sage-crumbs">
            <a href="${journalUrl}"><spring:message code="MyJournal"/></a> &#187;
            <a href="${formsUrl}">All "${descriptor.description}" Surveys</a> 
        </div>
        <h3>${descriptor.description}</h3>
    
        <c:url var="editUrl" value="/journal/${sessionScope.BridgeUser.ownerId}/forms/${descriptor.id}/row/${rowId}/edit.html"/>
        <a id="editAct" class="btn btn-sm btn-primary bottomSpaced" href="${editUrl}">
            Edit This Survey
        </a>
        <sage:router element="${form.formStructure}"/>
    </jsp:attribute>
</sage:journal>