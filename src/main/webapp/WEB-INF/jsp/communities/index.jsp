<%@ include file="../directives.jsp" %>
<sage:community code="${community.name}">
	<p>${community.description}</p>
	<c:if test="${editable}">
        <c:url var="editUrl" value="/communities/${community.id}/edit.html"/>
        <a href="${editUrl}" class="btn btn-sm btn-default topSpaced">
            <spring:message code="EditPage"/>
        </a>
	</c:if>
</sage:community>
