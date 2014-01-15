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
	
	    <c:url var="formUrl" value="/journal/${sessionScope.BridgeUser.ownerId}/forms/${descriptor.id}/row/${rowId}.html"/>
	    <form:form role="form" modelAttribute="dynamicForm" method="post" action="${formUrl}">
            <sage:router element="${form.editStructure}"/>
            <sage:submit code="Save"/>
            <sage:cancel url="/journal/${sessionScope.BridgeUser.ownerId}/forms/${descriptor.id}.html"/>
	    </form:form> 
    </jsp:attribute>
</sage:journal>