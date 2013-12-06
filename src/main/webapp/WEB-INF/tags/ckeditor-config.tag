<%@ include file="../jsp/directives.jsp" %>
<%@ attribute name="community" required="true" type="org.sagebionetworks.bridge.model.Community" %>
<%@ attribute name="wikiForm" required="true" type="org.sagebionetworks.bridge.webapp.forms.WikiForm" %>
<%@ attribute name="wikiHeaders" required="true" type="java.util.List" %>
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