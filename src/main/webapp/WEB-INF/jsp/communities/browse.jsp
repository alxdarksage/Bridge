<%@ include file="../directives.jsp" %>
<sage:blank>
    <div id="funcNum" data-value="${param.CKEditorFuncNum}"/>
	<div class="panel panel-default">
	    <div class="panel-heading">Select an image:</div>
	    <div class="panel-body">
	        <c:forEach items="${images}" var="file">
                <img class="attachment" data-link="${file.permanentURL}" 
                    src="${file.temporaryURL}"/>
	        </c:forEach>
	    </div>
	    <div class="panel-footer">
	        <a class="btn btn-small" href="javascript:window.close()">Cancel</a>
	    </div>
	</div>
</sage:blank>
<script>
var links = document.querySelectorAll(".attachment");
for (var i=0; i < links.length; i++) {
	var link = links[i];
	link.addEventListener("click", function(e) {
		var funcNum = document.getElementById('funcNum').getAttribute('data-value');
        var href = e.target.getAttribute('data-link');
		window.opener.CKEDITOR.tools.callFunction(funcNum, href);
		window.close();
	}, false);
}
</script>