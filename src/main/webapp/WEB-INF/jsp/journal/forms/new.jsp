<%@ include file="../../directives.jsp" %>
<sage:journal code="MyJournal">
    <jsp:attribute name="content">
	    <c:url var="journalUrl" value="/journal.html"/>
	    <c:url var="formsUrl" value="/journal/${sessionScope.BridgeUser.ownerId}/forms/${descriptor.id}.html"/>
	    <div class="sage-crumbs">
	        <a href="${journalUrl}"><spring:message code="MyJournal"/></a> &#187;
	        <a href="${formsUrl}">All "${descriptor.description}" Surveys</a> 
	    </div>
	    <h3>New ${descriptor.description}</h3>

	    <c:if test="${not empty defaultedFields}">
	       <table class="aboutDefaults">
	           <tr>
	               <td><div class="example defaulted">&#160;</div></td>
	               <td><spring:message code="DefaultsPresent"/></td>
	           </tr>
	       </table>
	    </c:if>
	    
	    <c:url var="formUrl" value="/journal/${sessionScope.BridgeUser.ownerId}/forms/${descriptor.id}/new.html"/>
	    <form:form role="form" modelAttribute="dynamicForm" method="post" action="${formUrl}">
	        <%@ include file="_form.jsp" %>
	    </form:form>    
    </jsp:attribute>
</sage:journal>