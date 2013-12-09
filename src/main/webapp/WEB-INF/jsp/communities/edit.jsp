<%@ include file="../directives.jsp" %>
<sage:community-wiki-edit code="${community.name}">
    <sage:formErrors formName="wikiForm"/>
    <c:url var="editUrl" value="/communities/${community.id}/wikis/${wikiForm.wikiId}/edit.html"/>
    <form:form role="form" modelAttribute="wikiForm" method="post" action="${editUrl}">
        <c:choose>
            <c:when test="${wikiForm.indexWiki}">
                <h3><spring:message code="CommunityPageNavigation"/></h3>
                <div class="help-block">
                    <spring:message code="CommunityPageNavigation.help"/>
                </div>
                <sage:hidden field="title"/>
            </c:when>
            <c:otherwise>
	            <sage:text field="title">
	                <span class="help-block"><spring:message code="title.help"/></span>
	            </sage:text>
            </c:otherwise>
        </c:choose>
        <sage:textarea field="markdown" hasLabel="false"/>
        <div class="topSpaced">
	        <sage:submit code="Save"/>
	        <sage:cancel url="/communities/${community.id}.html"/>
        </div>
    </form:form>
    <sage:ckeditor-config community="${community}" wikiForm="${wikiForm}" wikiHeaders="${wikiHeaders}"/>
</sage:community-wiki-edit>
