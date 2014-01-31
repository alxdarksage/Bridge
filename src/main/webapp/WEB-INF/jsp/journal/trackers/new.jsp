<%@ include file="../../directives.jsp" %>
<sage:journal code="MyJournal">
    <jsp:attribute name="content">
	    <c:url var="journalUrl" value="/journal.html"/>
	    <c:url var="trackerUrl" value="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.id}.html"/>
	    <div class="sage-crumbs">
	        <a href="${journalUrl}"><spring:message code="MyJournal"/></a> &#187;
	        <a href="${trackerUrl}">All ${descriptor.name}s</a> 
	    </div>
	    <h3>New ${descriptor.name}</h3>

	    <c:if test="${not empty defaultedFields}">
	       <table class="aboutDefaults">
	           <tr>
	               <td><div class="example defaulted">&#160;</div></td>
	               <td><spring:message code="DefaultsPresent"/></td>
	           </tr>
	       </table>
	    </c:if>
	    
	    <c:url var="trackerUrl" value="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.id}/new.html"/>
	    <form:form role="form" modelAttribute="dynamicForm" method="post" action="${trackerUrl}">
            <sage:router element="${form.editStructure}"/>
            <sage:submit id="finishAct" code="Finish"/>
            <sage:submit id="saveAct" code="Save For Later" action="save"/>
            <sage:cancel url="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.id}.html"/>
	    </form:form>
    </jsp:attribute>
</sage:journal>