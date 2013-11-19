<%@ include file="../directives.jsp" %>
<sage:community code="${community.name}">
    <sage:formErrors formName="communityForm"/>
    <c:url var="editUrl" value="/communities/${community.id}/edit.html"/>
    <form:form role="form" modelAttribute="communityForm" method="post" action="${editUrl}">
        <sage:textarea field="description" showLabel="false"/>
        <sage:submit code="Save"/>
        <sage:cancel url="/communities/${community.id}.html"/>
    </form:form>
</sage:community>
