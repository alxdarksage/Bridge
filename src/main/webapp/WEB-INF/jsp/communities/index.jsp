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
<script>
var loc = new String(document.location);
var links = document.querySelectorAll("#user-nav a");
for (var i=0; i < links.length; i++) {
	var link = links[i];
    if (loc.indexOf(link.getAttribute('href')) > -1) {
    	link.parentNode.className = "active list-group-item";
    }	
}
</script>
