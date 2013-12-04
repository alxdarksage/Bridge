<%@ include file="../directives.jsp" %>
<sage:community-wiki-edit code="${community.name}">
    <c:url var="allUrl" value="/communities/${community.id}/wikis/${wikiForm.wikiId}/all.html"/>
	<ul class="nav nav-tabs wiki-tabs">
		<li class="active"><a>${wikiForm.title}</a></li>
		<li><a href="${allUrl}">All Pages</a></li>
	</ul>
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

function selectPage() {
	var dialog = this._.dialog;
	dialog.selectPage('info');
	dialog.setValueOf('info', 'url', this.url);
}

CKEDITOR.on( 'dialogDefinition', function( e ) {
    var dialogName = e.data.name;
    var dialogDefinition = e.data.definition;
    if (dialogName === "link") {
    	dialogDefinition.addContents({
            id : 'pagesTab',
            label : 'Pages',
            elements : [
                {
                    type : 'vbox',
                    width: '100%',
                    children: [
                        <c:forEach var="wiki" items="${wikiHeaders}">
                        {
                            type: 'button',
                            id: 'w${wiki.id}',
                            label: "${wiki.title}",
                            url: '${pageContext.request.contextPath}${wiki.viewURL}',
                            onClick: selectPage
                        },
                        </c:forEach>
                    ]
                }
            ]
        });
    }
});
</script>
</sage:community-wiki-edit>
