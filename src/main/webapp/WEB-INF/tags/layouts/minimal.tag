<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="code" required="true" %>
<%@ attribute name="boxSize" required="false" %> <%-- 70rem by default --%>
<!DOCTYPE html>
<html lang="en">
<sage:head code="${code}"/>
<body>
    <div class="container">
        <sage:header code="${code}"/>
        <div class="row main-pane">
            <c:choose>
                <c:when test="${boxSize != ''}">
                    <div class="box" style="max-width: ${boxSize}"><jsp:doBody/></div>
                </c:when>
                <c:otherwise>
                    <div class="box"><jsp:doBody/></div>
                </c:otherwise>
            </c:choose>
        </div>
        <sage:footer/>
    </div>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>
    <script src="<c:url value='/assets/footer.js'/>"></script>
</body>
</html>