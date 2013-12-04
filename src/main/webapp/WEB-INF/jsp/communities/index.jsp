<%@ include file="../directives.jsp" %>
<sage:community code="${community.name}">
    <c:if test="${wikiId != community.welcomePageWikiId}">
	    <div class="sage-crumbs">
            <c:url var="homeUrl" value="/communities/${community.id}.html"/>
            <a href="${homeUrl}"><spring:message code="Home"/></a> &#187; 
	    </div>
    </c:if>
    ${wikiContent}
</sage:community>
