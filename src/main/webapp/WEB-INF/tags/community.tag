<%@ include file="../jsp/directives.jsp" %>
<%@ attribute name="code" required="true" %>
<sage:main code="${code}">
    <jsp:attribute name="navigation">
        <div class="community-actions">
            <c:if test="${joinable}">
                <c:url var="joinUrl" value="/communities/${community.id}/join.html"/>
                <a href="${joinUrl}" class="btn btn-sm btn-block btn-primary">
                    <spring:message code="JoinThisCommunity"/>
                </a>
            </c:if>
	        <c:if test="${editable}">
	            <c:url var="editUrl" value="/communities/${community.id}/wikis/${wikiId}/edit.html"/>
	            <a href="${editUrl}" class="btn btn-sm btn-block btn-default topSpaced">
	                <spring:message code="EditPage"/>
	            </a>
	        </c:if>
        </div>
        
        <ul class="list-group">
            <c:choose>
                <c:when test="${wikiId != community.welcomePageWikiId}">
                    <li class="list-group-item">
                        <c:url var="homeUrl" value="/communities/${community.id}.html"/>
                        <a href="${homeUrl}"><spring:message code="Home"/></a>
                    </li>
                </c:when>
                <c:otherwise>
                    <li class="active list-group-item"><spring:message code="Home"/></li>
                </c:otherwise>
            </c:choose>
            <li class="list-group-item"><a style="color:black"><em>Space here for user-supplied index wiki.</em></a></li>
            <li class="list-group-item"><a><spring:message code="Forums"/></a></li>
        </ul>
    </jsp:attribute>
    <jsp:attribute name="content"><jsp:doBody/></jsp:attribute>
</sage:main>
