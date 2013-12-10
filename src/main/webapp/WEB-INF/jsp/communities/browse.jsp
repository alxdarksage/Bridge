<%@ include file="../directives.jsp" %>
<sage:blank code="BrowseServerFiles">
    <div id="funcNum" data-value="${param.CKEditorFuncNum}"/>
	<div class="panel panel-default">
	    <div class="panel-heading panel-heading-container">
           <div><spring:message code="SelectAFile"/></div>
           <div><a class="btn btn-small" href="javascript:window.close()"><spring:message code="Close"/></a></div>
	    </div>
	    <div class="panel-body">
	        <c:forEach items="${images}" var="file">
	           <div class="attachment-container">
	               <div class="fileName">${file.fileName}</div>
	               <div class="attachment" style="background-image: url('${file.previewURL}')"
	                   data-link="${file.permanentURL}"></div>
	               <a href="${file.deleteURL}" data-confirm="Are you sure you wish to delete this file?" class="glyphicon glyphicon-remove text-danger"></a>
	           </div>
	        </c:forEach>
	    </div>
	</div>
</sage:blank>
<script>
var funcNum = document.getElementById('funcNum').getAttribute('data-value');
var links = document.querySelectorAll(".attachment");
for (var i=0; i < links.length; i++) {
	var link = links[i];
	link.addEventListener("click", function(e) {
        var href = e.target.getAttribute('data-link');
        window.close();
		window.opener.CKEDITOR.tools.callFunction(funcNum, href);
	}, false);
}
</script>