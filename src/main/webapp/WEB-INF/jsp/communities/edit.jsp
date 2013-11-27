<%@ include file="../directives.jsp" %>
<sage:community code="${community.name}">
    <sage:formErrors formName="wikiForm"/>
    <c:url var="editUrl" value="/communities/${community.id}/wikis/${wikiForm.wikiId}/edit.html"/>
    <form:form role="form" modelAttribute="wikiForm" method="post" action="${editUrl}">
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
</sage:community>
