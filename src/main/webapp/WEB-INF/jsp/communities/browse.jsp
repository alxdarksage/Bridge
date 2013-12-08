<%@ include file="../directives.jsp" %>
<sage:blank>
    <div id="funcNum" data-value="${param.CKEditorFuncNum}"/>
	<div class="panel panel-default">
	    <div class="panel-heading">
	       <div style="display: table-row">
	           <div style="display: table-cell; width: 100%">Select a file:</div>
	           <div style="display: table-cell"><a class="btn btn-small" href="javascript:window.close()">Close</a></div>
	       </div>
	    </div>
	    <div class="panel-body">
	        <c:forEach items="${images}" var="file">
	           <div style="max-width: 100px; float: left; margin-right: 1rem; display: none">
	               <img class="attachment" 
	                   onload="this.parentNode.style.display='block'"
	               data-link="${file.permanentURL}" src="${file.previewURL}" style="display:block; outline: 1px solid #aaa; "/>
	               <a href="${file.deleteURL}" style="float: right" class="glyphicon glyphicon-remove text-danger"> </a>
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