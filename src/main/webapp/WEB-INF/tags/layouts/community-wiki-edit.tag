<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="code" required="true" %>
<sage:main code="${code} Site Editor">
    <jsp:attribute name="navigation">
        <div id="tab-holder" class="${(wikiForm.indexWiki) ? 'index' : 'pages'}">
	        <ul class="nav nav-tabs smBottomSpaced">
	            <li><a id="allPagesAct" data-target="pages" class="act"><spring:message code="AllPages"/></a></li>
	            <li><a id="navAct" data-target="index" class="act"><spring:message code="Navigation"/></a></li>
	        </ul>
	        <div id="nav" class="panel panel-default">
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
	        <div id="pages" class="panel panel-default">
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
	                            <a class="deleteAct" href="${deleteUrl}" style="float:right" data-confirm="Are you sure?">
	                                <span class="glyphicon glyphicon-remove text-danger"> </span>
	                            </a>
	                        </c:if>
	                    </div>
	                </c:forEach>
	            </div>
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
    <jsp:attribute name="content">
        <jsp:doBody/>
<script>
var acts = document.querySelectorAll(".act");
for (var i=0; i < acts.length; i++) {
    acts[i].addEventListener("click", function(e) {
        e.preventDefault();
        document.getElementById('tab-holder').className = e.target.getAttribute("data-target");
    }, false);
}
</script>
    </jsp:attribute>
</sage:main>
