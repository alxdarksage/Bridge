<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sage-lib" uri="http://sagebase.org/bridge" %>
<%@ taglib prefix="sage" tagdir="/WEB-INF/tags" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="boxSize" required="false" %> <!-- 70rem by default -->
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
            <c:choose>
                <c:when test="${boxSize != ''}">
                    <div class="box" style="max-width: ${boxSize}">
                </c:when>
                <c:otherwise>
                    <div class="box">
                </c:otherwise>
            </c:choose>
            <c:if test="${pageTitle}">
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