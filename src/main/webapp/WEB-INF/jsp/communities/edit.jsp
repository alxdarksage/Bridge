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
    <script src="/webapp/assets/ckeditor/ckeditor.js"></script>
<script>
<c:choose>
<c:when test="${wikiForm.indexWiki}">
CKEDITOR.replace( 'markdown', {
    baseHref: "http://localhost:8888<c:url value="/communities/${community.id}/wikis/"/>",
    filebrowserBrowseUrl: "<c:url value="/files/communities/${community.id}/browse.html"/>",
    filebrowserUploadUrl: "<c:url value="/files/communities/${community.id}/upload.html"/>",
    filebrowserWindowWidth : '300',
    filebrowserWindowHeight : '400',
    toolbar: [{name: 'Basic', items: ['BulletedList', 'Link','Unlink']}]
});
</c:when>
<c:otherwise>
CKEDITOR.replace( 'markdown', {
	// ?!?
    baseHref: "http://localhost:8888<c:url value="/communities/${community.id}/wikis/"/>",
    filebrowserBrowseUrl: "<c:url value="/files/communities/${community.id}/browse.html"/>",
    filebrowserUploadUrl: "<c:url value="/files/communities/${community.id}/upload.html"/>",
    filebrowserWindowWidth : '300',
    filebrowserWindowHeight : '400',
    toolbar: [
        { name: 'clipboard', items: ['Cut','Copy','Paste','PasteText',/*'PasteFromWord',*/'-','Undo','Redo']},
        { name: 'editing', items: [ 'Find','Replace','-','SelectAll'/*,'-','SpellCheck', 'Scayt'*/ ] },
        { name: 'paragraph', items: [ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote','CreateDiv',
           '-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock' ] },
        { name: 'insert', items: [ 'Link','Unlink','Anchor','Image','Table','HorizontalRule','SpecialChar','PageBreak'/*,'Iframe'*/ ]  },
        { name: 'styles', items : [ 'Styles','Format','Font','FontSize' ] },
        { name: 'basicstyles', items: [ 'Bold','Italic','Underline','Strike','Subscript','Superscript','-',
           'RemoveFormat','-','TextColor','BGColor' ] }
    ]
});
</c:otherwise>
</c:choose>

function selectPage() {
	var dialog = this._.dialog;
	dialog.selectPage('info');
	dialog.setValueOf('info', 'url', this.url);
}
CKEDITOR.on( 'dialogDefinition', function( e ) {
    var dialogName = e.data.name;
    var dialogDefinition = e.data.definition;
    if (dialogName === "link") {
    	dialogDefinition.removeContents( 'advanced' );
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
    } else if (dialogName == 'image') {
    	dialogDefinition.removeContents( 'advanced' );
    }
});
</script>
</sage:community-wiki-edit>
