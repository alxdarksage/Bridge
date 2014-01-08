<%@ include file="../../jsp/directives.jsp" %>
<script src="<c:url value="/assets/smiley-slider-gh-pages/smiley-slider.js"/>"></script>
<c:if test="${not empty descriptorsAlways}">
	<div class="reminder-direct">
		How are you feeling right now?
		<div>Mind <span id="mind-slider"></span></div>
		<div>Body <div id="body-slider"></div></div>
		<script type="text/javascript">
		    var body = new SmileySlider(document.getElementById("body-slider"))
		    var mind = new SmileySlider(document.getElementById("mind-slider"))
		    body.position(-0.5);
		    mind.position(-0.5);
		    body.position(function (p) {
		        // do something when it changes
		    });
		    mind.position(function (p) {
		        // do something when it changes
		    });
		</script>
		<sage:comma-list first="Do you have a new " items="${descriptorsAlways}" separator=", " endSeparator=" or " last="?">
			<a href="/bridge/journal/${sessionScope.BridgeUser.ownerId}/forms/${item.id}.html">
					${(not empty item.description) ? item.description : item.name}</a
		></sage:comma-list>
	</div>
</c:if>
