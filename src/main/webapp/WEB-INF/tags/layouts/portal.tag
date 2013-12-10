<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="code" required="true" %>
<!DOCTYPE html>
<html lang="en">
<sage:head code="${code}"/>
<body>
    <div class="container">
        <h2>Bridge</h2>
	    <jsp:doBody/>
    </div>
    <script type="text/javascript" src="<c:url value='/assets/footer.js'/>"></script>
    <sage:notifications/>
</body>
</html>