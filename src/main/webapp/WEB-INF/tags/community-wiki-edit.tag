<%@ include file="../jsp/directives.jsp" %>
<%@ attribute name="code" required="true" %>
<sage:main code="${code} Site Editor">
    <jsp:attribute name="navigation">
        <ul class="nav nav-tabs smBottomSpaced">
            <li class="${(wikiForm.indexWiki) ? '' : 'active'}"><a id="allPagesAct" data-target="pages" class="act"><spring:message code="AllPages"/></a></li>
            <li class="${(wikiForm.indexWiki) ? 'active' : ''}"><a id="navAct" data-target="nav" class="act"><spring:message code="Navigation"/></a></li>
        </ul>
        <div id="nav" class="subPanel panel panel-default" style="display: ${(wikiForm.indexWiki) ? 'block' : 'none'}">
            <div class="panel-heading">
                <strong>Site Navigation</strong>
                <c:url var="editIndexUrl" value="/communities/${community.id}/wikis/${community.indexPageWikiId}/edit.html"/>
                <a id="editIndexAct" href="${editIndexUrl}" style="float:right" class="btn btn-xs btn-default">Edit</a>
            </div>
            <ul class="list-group" id="user-nav">
                <li class="list-group-item"><a><spring:message code="Home"/></a></li>
                ${indexContent}
                <li class="list-group-item"><a><spring:message code="Forums"/></a></li>
            </ul>
        </div>
        <div id="pages" class="subPanel panel panel-default" style="display: ${(wikiForm.indexWiki) ? 'none' : 'block'}">
            <div class="panel-heading">
                <strong>All Pages</strong>
	            <c:url var="newUrl" value="/communities/${community.id}/wikis/new.html"/>
	            <a id="newPageAct" style="float:right" class="btn btn-xs btn-primary" href="${newUrl}">New Page</a>
            </div>
            <div class="panel-body">
                <c:forEach items="${wikiHeaders}" var="wikiHeader">
                    <div class="pages ${(wikiHeader.id eq wikiForm.wikiId) ? 'alert alert-info' : 'alert'}" title="${wikiHeader.title}">
                        <c:url var="editUrl" value="/communities/${community.id}/wikis/${wikiHeader.id}/edit.html"/>
                        <a class="linkAct" href="${editUrl}">
                            ${wikiHeader.title}
                        </a>
                        <c:if test="${not wikiHeader.locked}">
	                        <c:url var="deleteUrl" value="/communities/${community.id}/wikis/${wikiHeader.id}/delete.html?rowSelect=${wikiHeader.id}"/>
	                        <a class="deleteAct" href="${deleteUrl}" style="float:right">
	                            <span class="glyphicon glyphicon-remove text-danger"> </span>
	                        </a>
                        </c:if>
                    </div>
                </c:forEach>
            </div>
        </div>
        <div class="community-actions">
            <%-- Don't close the editor to the index page, it's weird. You only view it on the side. --%>
            <c:choose>
                <c:when test="${wikiForm.indexWiki}">
                    <c:url var="editUrl" value="/communities/${community.id}/wikis/${community.welcomePageWikiId}.html"/>
                </c:when>
                <c:otherwise>
                    <c:url var="editUrl" value="/communities/${community.id}/wikis/${wikiForm.wikiId}.html"/>    
                </c:otherwise>
            </c:choose>
            <a id="quitAct" href="${editUrl}" class="btn btn-sm btn-block btn-default topSpaced">
                <spring:message code="QuitEditPage"/>
            </a>
        </div>        
    </jsp:attribute>
    <jsp:attribute name="content"><jsp:doBody/></jsp:attribute>
</sage:main>
<script>
var acts = document.querySelectorAll(".act");
for (var i=0; i < acts.length; i++) {
	acts[i].addEventListener("click", function(e) {
		e.preventDefault();
		var target = e.target.getAttribute("data-target");
		var panels = document.querySelectorAll(".subPanel");
		for (var j=0; j < panels.length; j++) {
			var match = (panels[j].id === target);
			panels[j].style.display = match ? 'block' : 'none';
		}
		for (var k=0; k < acts.length; k++) {
            var match = (acts[k] === e.target);
			acts[k].parentNode.className = (match) ? "active" : "";
		}
	}, false);
}
/*
var delActs = document.querySelectorAll(".deleteAct");
for (var i=0; i < delActs.length; i++) {
    delActs[i].addEventListener("click", function(e) {
    	if (!confirm("Are you sure?")) {
    		e.preventDefault();
    	}
    }, false);
}
*/
</script>
