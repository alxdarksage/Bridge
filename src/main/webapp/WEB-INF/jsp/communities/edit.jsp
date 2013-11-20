<%@ include file="../directives.jsp" %>
<sage:community code="${community.name}">
    <sage:formErrors formName="communityForm"/>
    <c:url var="editUrl" value="/communities/${community.id}/edit.html"/>
    
    <div class="editable">${community.description}</div>
    
    <form:form role="form" modelAttribute="communityForm" method="post" action="${editUrl}">
        <sage:hidden field="description"/>
        <div class="topSpaced">
	        <sage:submit code="Save"/>
	        <sage:cancel url="/communities/${community.id}.html"/>
        </div>
    </form:form>
</sage:community>
