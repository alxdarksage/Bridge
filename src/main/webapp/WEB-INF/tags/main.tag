<%@ include file="../jsp/directives.jsp" %>
<%@ attribute name="code" required="true" %>
<%@ attribute name="navigation" required="true" fragment="true" %>
<%@ attribute name="content" required="true" fragment="true" %>
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
            <div class="col-sm-3 visible-sm visible-md visible-lg nav-pane">
                <c:choose>
                    <c:when test="${sessionScope.BridgeUser.isAuthenticated()}">
			            <div id="profile-pane" class="well hidden-sm">
	                        <table cellspacing="0" cellpadding="0">
	                            <tr>
	                                <td><img src='<c:url value="/static/images/default_avatar.png"/>'/></td>
	                                <td>
	                                    <div class="userName">${sessionScope['BridgeUser'].displayName}</div>
	                                    <c:url var="signOutUrl" value="/signOut.html"/>
                                        <c:url var="editProfileUrl" value="/profile.html"/>
                                        <a id="signOutButton" href="${signOutUrl}">
                                            <spring:message code="SignOut"/>
                                        </a> <span class="div">&bull;</span> 
                                        <a id="editProfileLink" href="${editProfileUrl}">
                                            <spring:message code="EditProfile"/>
                                        </a>
	                                </td>
	                            </tr>
	                        </table>
			            </div>
                    </c:when>
                    <c:otherwise>
                        <div class="well">
                            <sage:signIn/>
                        </div>
                    </c:otherwise>
                </c:choose>
                <jsp:invoke fragment="navigation" />
            </div>
            <div class="col-sm-9 content-pane">
                <jsp:invoke fragment="content" />
            </div>
        </div>
    </div>
    <script type="text/javascript" src="<c:url value='/assets/footer.js'/>"></script>
    <sage:notifications/>
</body>
</html>