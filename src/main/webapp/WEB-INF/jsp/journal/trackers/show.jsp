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
    
        <c:url var="editUrl" value="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.id}/row/${rowId}/edit.html"/>
        <a id="editAct" class="btn btn-sm btn-primary bottomSpaced" href="${editUrl}">
            Edit This Tracker
        </a>
        <sage:router element="${form.showStructure}"/>
    </jsp:attribute>
</sage:journal>