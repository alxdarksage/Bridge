<%@ include file="../jsp/directives.jsp" %>
<%@ attribute name="code" required="true" %>
<%@ attribute name="pageTitle" required="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <c:choose>
    	<c:when test="${code != ''}">
    		<title>Bridge : <spring:message code="${code}"/> - Sage Bionetworks</title>
    	</c:when>
    	<c:otherwise>
    		<title>Bridge - Sage Bionetworks</title>
    	</c:otherwise>
    </c:choose>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="http://fonts.googleapis.com/css?family=Archivo+Narrow" type="text/css" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/assets/header.css'/>" />
</head>
<body>
	<div class="container">
        <sage:header code="${code}"/>
		<div class="row main-pane">
			<div class="col-md-3 visible-sm visible-md visible-lg nav-pane">
                <div id="profile-pane" class="well">
                    <c:choose>
                        <c:when test="${sessionScope.BridgeUser.isAuthenticated()}">
                            <c:if test="${sessionScope['BridgeUser'].avatarUrl}">
                                <p><img src="${sessionScope['BridgeUser'].avatarUrl}"/>
                            </c:if>
                            <p>${sessionScope['BridgeUser'].displayName}</p>
                            <c:url var="signOutUrl" value="/signOut.html"/>
                            <form:form role="form" modelAttribute="signInForm" method="post" action="${signOutUrl}">
                                <input type="hidden" name="origin" value="${requestScope['origin']}"/>
                                <button id="signOutButton" type="submit" class="btn btn-sm btn-default">
                                    <spring:message code="SignOut"/>
                                </button>
                                <c:url var="editProfileUrl" value="/profile.html"/>
                                <a class="btn" id="editProfileLink" href="${editProfileUrl}">
                                    <spring:message code="EditProfile"/>
                                </a>
                            </form:form>
                        </c:when>
                        <c:otherwise>
                            <sage:signIn/>
                        </c:otherwise>
                    </c:choose>
				</div>
				<ul class="list-group">
					<li class="active list-group-item"><spring:message code="Home"/></li>
	                <c:if test="${sessionScope.BridgeUser.isAuthenticated()}">
	                    <li class="list-group-item"><a><spring:message code="MyJournal"/></a></li>
	                </c:if>
					<li class="list-group-item"><a><spring:message code="Forums"/></a></li>
				</ul>
			</div>
			<div class="col-md-9 content-pane">
				<jsp:doBody/>
			</div>
		</div>
	</div>
    <script type="text/javascript" src="<c:url value='/assets/footer.js'/>"></script>
    <c:if test="${sessionScope['notice'] != null}">
        <script id="notice">humane.log("<spring:message code="${pageContext.request.getNotification()}"/>");</script>
    </c:if>
</body>
</html>