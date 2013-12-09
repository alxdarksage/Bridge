<%@ include file="../jsp/directives.jsp" %>
<%@ attribute name="community" required="true" type="org.sagebionetworks.bridge.model.Community" %>
<%@ attribute name="wikiForm" required="true" type="org.sagebionetworks.bridge.webapp.forms.WikiForm" %>
<%@ attribute name="wikiHeaders" required="true" type="java.util.List" %>
<div id="sageTestValue" style="position: absolute; left: -10000px"></div>
<div id="sageEditorReady" style="position: absolute; left: -10000px">false</div>
<div id="sageDialogOpen" style="position: absolute; left: -10000px">false</div>
<script src="/webapp/assets/ckeditor/ckeditor.js"></script>
<script>
<c:choose>
<c:when test="${wikiForm.indexWiki}">
CKEDITOR.replace( 'markdown', {
	allowedContent: true,
    filebrowserBrowseUrl: "<c:url value="/files/communities/${community.id}/browse.html"/>",
    filebrowserUploadUrl: "<c:url value="/files/communities/${community.id}/upload.html"/>",
    filebrowserWindowWidth : '300',
    filebrowserWindowHeight : '400',
    toolbar: [{name: 'Basic', items: ['BulletedList', 'Link','Unlink']}]
});
</c:when>
<c:otherwise>
CKEDITOR.replace( 'markdown', {
	allowedContent: true,
    filebrowserBrowseUrl: "<c:url value="/files/communities/${community.id}/browse.html"/>",
    filebrowserUploadUrl: "<c:url value="/files/communities/${community.id}/upload.html"/>",
    filebrowserWindowWidth : '300',
    filebrowserWindowHeight : '400',
    toolbar: [
        { name: 'clipboard', items: ['Cut','Copy','Paste','PasteText','-','Undo','Redo']},
        { name: 'editing', items: [ 'Find','Replace','-','SelectAll' ] },
        { name: 'paragraph', items: [ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote','CreateDiv',
           '-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock' ] },
        { name: 'insert', items: [ 'Link','Unlink','Anchor','Image','Table','HorizontalRule','SpecialChar','PageBreak' ]  },
        { name: 'styles', items : [ 'Styles','Format','Font','FontSize' ] },
        { name: 'basicstyles', items: [ 'Bold','Italic','Underline','Strike','Subscript','Superscript','-',
           'RemoveFormat','-','TextColor','BGColor', 'Source' ] }
    ]
});
</c:otherwise>
</c:choose>
function selectPage() {
    var dialog = this._.dialog;
    dialog.selectPage('info');
    dialog.setValueOf('info', 'url', this.url);
}
CKEDITOR.instances.markdown.on('instanceReady', function(e) {
	document.getElementById('sageEditorReady').textContent = "true";
});
CKEDITOR.on( 'dialogDefinition', function( e ) {
    var dialogName = e.data.name;
    var dialogDefinition = e.data.definition;
    var dialog = e.data.definition.dialog;
    dialog.on('show', function() {
    	document.getElementById('sageDialogOpen').textContent = "true";
    });
    dialog.on('hide', function() {
    	document.getElementById('sageDialogOpen').textContent = "false";
    });
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