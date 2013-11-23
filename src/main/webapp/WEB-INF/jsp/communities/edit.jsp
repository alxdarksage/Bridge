<%@ include file="../directives.jsp" %>
<sage:community code="${community.name}">
    <sage:formErrors formName="communityForm"/>
    <c:url var="editUrl" value="/communities/${community.id}/edit.html"/>
    <form:form role="form" modelAttribute="communityForm" method="post" action="${editUrl}">
        <spring:bind path="description">
            <form:textarea cssClass="form-control input-sm" id="description" path="description"/>
        </spring:bind>
        <div class="topSpaced">
	        <sage:submit code="Save"/>
	        <sage:cancel url="/communities/${community.id}.html"/>
        </div>
    </form:form>
    <script src="/webapp/assets/ckeditor/ckeditor.js"></script>
    
<script>
CKEDITOR.replace( 'description', {
    toolbarGroups: [
        { name: 'clipboard', groups: [ 'clipboard', 'undo' ] },
        { name: 'editing', groups: [ 'find', 'selection', 'spellchecker' ] },
        { name: 'insert' },
        { name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align'/*, 'bidi'*/ ] },
        { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
        { name: 'styles' },
        { name: 'colors' },
        { name: 'links' },
        { name: 'source', groups: ['mode'] }
    ]
});


</script>
</sage:community>
