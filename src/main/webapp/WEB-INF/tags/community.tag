<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
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
                <div class="well">
                    <c:choose>
                        <c:when test="${sessionScope.BridgeUser.isAuthenticated()}">
                            <p>${sessionScope['BridgeUser'].displayName}</p>
                            <c:url var="signOutUrl" value="/signOut.html"/>
                            <form:form role="form" modelAttribute="signInForm" method="post" action="${signOutUrl}">
                                <input type="hidden" name="origin" value="${requestScope['origin']}"/>
                                <button type="submit" class="btn btn-sm btn-default">Sign Out</button>
                            </form:form>
                        </c:when>
                        <c:otherwise>
                            <sage:signIn/>
                        </c:otherwise>
                    </c:choose>
				</div>
				<ul class="list-group">
					<li class="active list-group-item">Cras justo odio</li>
					<li class="list-group-item"><a>Dapibus ac facilisis in</a></li>
					<li class="list-group-item"><a>Morbi leo risus</a></li>
					<li class="list-group-item"><a>Porta ac consectetur ac</a></li>
					<li class="list-group-item"><a>Vestibulum at eros</a></li>
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
    <sage:notifications/>
</body>
</html>