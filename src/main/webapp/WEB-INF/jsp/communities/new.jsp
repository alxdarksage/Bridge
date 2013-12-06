<%@ include file="../directives.jsp" %>
<sage:community-wiki-edit code="${community.name}">
    <sage:formErrors formName="wikiForm"/>
    <c:url var="createUrl" value="/communities/${community.id}/wikis/new.html"/>
    <form:form role="form" modelAttribute="wikiForm" method="post" action="${createUrl}">
        <sage:text field="title">
            <span class="help-block"><spring:message code="title.help"/></span>
        </sage:text>
        <sage:textarea field="markdown" hasLabel="false"/>
        <div class="topSpaced">
            <sage:submit code="Save"/>
            <sage:cancel url="/communities/${community.id}.html"/>
        </div>
    </form:form>
    <sage:ckeditor-config community="${community}" wikiForm="${wikiForm}" wikiHeaders="${wikiHeaders}"/>
</sage:community-wiki-edit>
