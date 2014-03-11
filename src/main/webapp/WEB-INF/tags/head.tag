<%@ include file="../jsp/directives.jsp" %>
<%@ attribute name="code" required="true" %>
<head>
    <meta charset="utf-8">
    <c:choose>
        <c:when test="${code == 'BrowseServerFiles'}">
            <title><spring:message code="${code}"/></title>
        </c:when>
        <c:when test="${code != ''}">
            <title>Bridge : <spring:message code="${code}"/> - Sage Bionetworks</title>
        </c:when>
        <c:otherwise>
            <title>Bridge - Sage Bionetworks</title>
        </c:otherwise>
    </c:choose>
    <meta name="viewport" content="width=device-width, user-scalable=no" />
    <c:url var="styleUrl" value="/assets/header.css"/>
    <link rel="stylesheet" href="http://fonts.googleapis.com/css?family=Archivo+Narrow" type="text/css" />
    <link rel="stylesheet" href="${styleUrl}" type="text/css" />
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>
</head>