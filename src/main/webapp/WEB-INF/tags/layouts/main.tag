<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="code" required="true" %>
<%@ attribute name="navigation" required="true" fragment="true" %>
<%@ attribute name="content" required="true" fragment="true" %>
<%@ attribute name="scripts" required="false" fragment="true" %>
<!DOCTYPE html>
<html lang="en">
<sage:head code="${code}"/>
<body>
    <div class="snap-drawers">
        <div class="snap-drawer snap-drawer-left">
            <ul class="list-group">
                <c:choose>
                    <c:when test="${sessionScope.BridgeUser.isAuthenticated()}">
                        <li class="list-group-item"><a href='<c:url value="/index.html"/>'>Bridge</a></li>
                        <li class="list-group-item"><a href='<c:url value="/signOut.html"/>'><spring:message code="SignOut"/></a></li> 
                    </c:when>
                    <c:otherwise>
                        <li class="list-group-item"><a href='<c:url value="/index.html"/>'>Bridge</a></li>
                        <li class="list-group-item">
                           <a href='<c:url value="/signIn.html"/>'><spring:message code="SignIn"/></a>
                           <a href='<c:url value="/signUp.html"/>'><spring:message code="SignUp"/></a>
                       </li> 
                    </c:otherwise>
                </c:choose>
            </ul>
            <ul class="list-group">
                <c:url var="journalUrl" value="/journal.html"/>
                <li class="list-group-item"><a id="journalAct" href="${journalUrl}"><spring:message code="MyJournal"/></a></li>
                <c:forEach var="cty" items="${sessionScope['BridgeUser'].communities}">
                    <c:url var="ctyUrl" value="/communities/${cty.id}.html"/>
                    <li class="list-group-item"><a href="${ctyUrl}">${cty.name}</a></li>
                </c:forEach>
            </ul>
        </div>
    </div>
    <div id="content" class="snap-content">
	    <div class="container">
	        <sage:header code="${code}"/>
	        <div class="row main-pane">
	            <div id="nav-pane" class="col-sm-3 visible-sm visible-md visible-lg">
	                <sage:signIn/>
	                <jsp:invoke fragment="navigation" />
	            </div>
	            <div id="content-pane" class="col-sm-9">
	                <jsp:invoke fragment="content" />
	            </div>
	        </div>
	        <sage:footer/>
	    </div>
    </div>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>
    <script src="<c:url value='/assets/footer.js'/>"></script>
    <jsp:invoke fragment="scripts" />
    <script type="text/javascript">
    var snapper = new Snap({element: document.getElementById('content'), disable: "right"});
    document.getElementById('open-left').addEventListener('click', function(e) {
		if( snapper.state().state=="left" ){
		    snapper.close();
		} else {
		    snapper.open('left');
		}
    }, false);
    
    var n = document.querySelectorAll(".snap-drawer-left a");
    console.log(document.location.href);
    for (var i=0; i < n.length; i++) {
    	if (document.location.href.indexOf(n[i].getAttribute('href')) > -1) {
    		$(n[i]).closest('li').addClass('active');
    	}
    }
    </script>
    <sage:notice/>
</body>
</html>