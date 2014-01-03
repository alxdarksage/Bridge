<%@ include file="../directives.jsp" %>
<sage:community code="${community.name}">
    ${wikiContent}
</sage:community>
<script>
var loc = new String(document.location);
var links = document.querySelectorAll("#user-nav a");
for (var i=0; i < links.length; i++) {
	var link = links[i];
    if (loc.indexOf(link.getAttribute('href')) > -1) {
    	link.parentNode.className = "active list-group-item";
    }	
}
</script>
