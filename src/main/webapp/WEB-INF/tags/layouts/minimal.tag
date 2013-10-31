<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="title" required="true" %>
<!DOCTYPE html>
<html>
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
        <div class="header row">
            <div class="col-sm-6 community-header">
                ${title}
            </div>
            <div class="col-sm-6 portal-header visible-sm visible-md visible-lg">
                <div class="portal-subheader">
                    <a href="<c:url value='/portal/index.html'/>">Bridge Community Portal</a>
                </div>
                <div class="portal-links">
                </div>
            </div>
        </div>
        <div class="row main-pane">
            <div class="col-md-3 visible-md visible-lg nav-pane">
            </div>
            <div class="col-md-9 content-pane">
                <c:if test="${pageTitle}">
                    <h3>${pageTitle}</h3>
                </c:if>
                <jsp:doBody/>
            </div>
        </div>
    </div>
    <script type="text/javascript" src="<c:url value='/assets/footer.js'/>"></script>
</body>
</html>