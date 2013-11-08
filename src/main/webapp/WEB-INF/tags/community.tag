<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sage-lib" uri="http://sagebase.org/bridge" %>
<%@ taglib prefix="sage" tagdir="/WEB-INF/tags" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="pageTitle" required="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <c:choose>
    	<c:when test="${title != ''}">
    		<title>Bridge : ${title} - Sage Bionetworks</title>
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
        <sage:header title="${title}"/>
		<div class="row main-pane">
			<div class="col-md-3 visible-md visible-lg nav-pane">
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
                                <button id="signOutButton" type="submit" class="btn btn-sm btn-default">Sign Out</button>
                                <c:url var="editProfileUrl" value="/profile.html"/>
                                <a class="btn" id="editProfileLink" href="${editProfileUrl}">Edit Profile</a>
                            </form:form>
                        </c:when>
                        <c:otherwise>
                            <sage:signIn/>
                        </c:otherwise>
                    </c:choose>
				</div>
				<ul class="list-group">
					<li class="active list-group-item">Home</li>
	                <c:if test="${sessionScope.BridgeUser.isAuthenticated()}">
	                    <li class="list-group-item"><a>My Journal</a></li>
	                </c:if>
					<li class="list-group-item"><a>Forums</a></li>
				</ul>
			</div>
			<div class="col-md-9 content-pane">
                <c:if test="${pageTitle != ''}">
                    <h3>${pageTitle}</h3>
                </c:if>
				<jsp:doBody/>
			</div>
		</div>
	</div>
    <script type="text/javascript" src="<c:url value='/assets/footer.js'/>"></script>
    <sage-lib:notification/>
</body>
</html>