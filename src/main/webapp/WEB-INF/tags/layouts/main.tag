<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="code" required="true" %>
<%@ attribute name="navigation" required="true" fragment="true" %>
<%@ attribute name="content" required="true" fragment="true" %>
<%@ attribute name="mobile" required="true" %>
<%@ attribute name="scripts" required="false" fragment="true" %>
<!DOCTYPE html>
<html lang="en">
<sage:head code="${code}"/>
<style>
.nav-stacked > li {
  float: none;
}
.nav-stacked > li > a {
  padding: 0px 0px 0px 0px;
  margin-right: 0;
}
.nav-stacked > li > ul {
 	list-style-type: none;
 	padding-left: 10px;
}
.navbar-toggle > .icon-bar {
  background-color: #cccccc;
}
.container {
	max-width: none;
}
.slider {
	width: 80px;
	border: none;
}
.event {
	padding-left: 10px;
	padding-bottom: 5px;
}
.event input {
	width: 120px;
}
.count {
	display: inline;
	background-color: #400;
	border: 1px solid black;
}
</style>
<body>
    <c:choose>
        <c:when test="${mobile}">
		    <sage:mobile-nav>
		        <div class="container">
		            <sage:header code="${code}" mobile="true"/>
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
		    </sage:mobile-nav>
        </c:when>
        <c:otherwise>
	        <div class="container">
	            <sage:header code="${code}" mobile="false"/>
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
        </c:otherwise>
    </c:choose>
  <div class="container">
    <div class="navbar-header">
      <button class="navbar-toggle" type="button" data-toggle="collapse" data-target=".bs-navbar-collapse">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a href="../" class="navbar-brand">Bridge Journal</a>
    </div>
	    <nav class="collapse navbar-collapse bs-navbar-collapse" role="navigation">
	      <ul class="nav hidden-xs navbar-nav navbar-right">
	        <li><a href="../getting-started">Account</a></li>
	        <li><a href="../getting-started">Logout</a></li>
	      </ul>
      </nav>
  </div>
        <div class="row main-pane">
            <div id="nav-pane" class="col-sm-3">
			    <nav class="collapse navbar-collapse bs-navbar-collapse" role="navigation">
				<c:choose>
				    <c:when test="${sessionScope.BridgeUser.isAuthenticated()}">
				        <div id="profile-pane" class="panel panel-default">
				            <div class="avatar-side">
				                <div class="avatar" style="background-image: url('<c:url value="/static/images/default_avatar.png"/>')">
				                </div>
				                <div class="msgs">
				                    <span class="glyphicon glyphicon-inbox"></span>&#160;<a>0 Msgs</a>
				                </div>
				                <div class="msgs">
				                    <div class="userName">${sessionScope['BridgeUser'].userName}</div>
				                </div>
				            </div>
		            		<div class="userName-side">
				            	<sage:reminders-direct/>
		            		</div>
							<sage:reminders-event/>
				        </div>
				    </c:when>
				</c:choose>
			      <ul class="nav navbar-nav nav-stacked">
			        <li class="active">
			          <a href="../getting-started">Current questions</a>
			        </li>
			        <li>
			          <a href="../css">History</a>
			        </li>
			        <li class="visible-xs"><hr/></li>
			        <li class="visible-xs"><a href="../getting-started">Account</a></li>
			        <li class="visible-xs"><a href="../getting-started">Logout</a></li>
			        <li><hr/></li>
			        <li>Trackers
				        <ul>
				        	<li><a href="<c:url value="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${cbc.id}.html"/>">Complete Blood Counts</a></li>
				        	<li><a href="../components">Questionaires</a></li>
				        	<li><a href="<c:url value="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${medicationsIfChanged.id}.html"/>">Medications</a></li>
				        	<li><a href="<c:url value="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${events.id}.html"/>">Events</a></li>
				        </ul>
			        </li>
			        <li><hr/></li>
			        <li>Communities
				        <ul>
				        	<li><a href="../components">Fanconi Anemia</a></li>
				        </ul>
			        </li>
			      </ul>
			    </nav>
            </div>
            <div id="content-pane" class="col-sm-9">
                <jsp:invoke fragment="content" />
            </div>
        </div>
        <sage:footer/>
    </div>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>
    <script src="<c:url value='/assets/footer.js'/>"></script>
    <jsp:invoke fragment="scripts" />
    <sage:notice/>
</body>
</html>