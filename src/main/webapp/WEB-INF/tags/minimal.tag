<%@ include file="../jsp/directives.jsp" %>
<%@ attribute name="code" required="true" %>
<%@ attribute name="boxSize" required="false" %> <!-- 70rem by default -->
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
            <c:choose>
                <c:when test="${boxSize != ''}">
                    <div class="box" style="max-width: ${boxSize}">
                </c:when>
                <c:otherwise>
                    <div class="box">
                </c:otherwise>
            </c:choose>
            <jsp:doBody/>
            </div>
        </div>
    </div>
    <script type="text/javascript" src="<c:url value='/assets/footer.js'/>"></script>
    <sage:notifications/>
</body>
</html>