<%@ include file="../directives.jsp" %>
<sage:community-wiki-edit code="${community.name}">
    <c:url var="allUrl" value="/communities/${community.id}/wikis/new/all.html"/>
    <ul class="nav nav-tabs" style="margin-bottom: 2rem">
        <li class="active"><a><spring:message code="NewPage"/></a></li>
        <li><a href="${allUrl}"><spring:message code="AllPages"/></a></li>
    </ul>
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
    <script src="/webapp/assets/ckeditor/ckeditor.js"></script>
<script>
CKEDITOR.replace( 'markdown', {
    baseHref: "http://localhost:8888<c:url value="/communities/${community.id}/wikis/${wikiForm.wikiId}/pages/"/>",
    filebrowserBrowseUrl: "<c:url value="/communities/${community.id}/wikis/${wikiForm.wikiId}/browse.html"/>",
    filebrowserUploadUrl: "<c:url value="/communities/${community.id}/wikis/${wikiForm.wikiId}/upload.html"/>",
    filebrowserWindowWidth : '300',
    filebrowserWindowHeight : '400',
    toolbarGroups: [
        { name: 'clipboard', groups: [ 'clipboard', 'undo' ] },
        { name: 'editing', groups: [ 'find', 'selection', 'spellchecker' ] },
        { name: 'insert' },
        { name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align' ] },
        { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
        { name: 'styles' },
        { name: 'colors' },
        { name: 'links' },
        { name: 'source', groups: ['mode'] }
    ]
});
</script>
</sage:community-wiki-edit>
