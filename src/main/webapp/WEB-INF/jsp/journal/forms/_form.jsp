<%@ include file="../../directives.jsp" %>
<sage:journal code="MyJournal">
    <c:url var="journalUrl" value="/journal.html"/>
    <c:url var="formsUrl" value="/journal/${sessionScope.BridgeUser.ownerId}/forms/${descriptor.id}.html"/>
    <div class="sage-crumbs">
        <a href="${journalUrl}"><spring:message code="MyJournal"/></a> &#187;
        <a href="${formsUrl}">All "${descriptor.description}" Surveys</a> 
    </div>
    
    <h3>New ${descriptor.description}</h3>
    
    <c:forEach var="row" items="${form.rows}">
        ${row}
    </c:forEach>
</sage:journal>
