<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="code" required="true" %>
<!DOCTYPE html>
<html lang="en">
<sage:head code="${code}"/>
<body>
    <sage:mobile-nav>
	    <div class="container">
	        <h2>Bridge</h2>
	        <jsp:doBody/>
	        <sage:footer/>
	    </div>
    </sage:mobile-nav>
    <script src="<c:url value='/assets/footer.js'/>"></script>
    <sage:notice/>
</body>
</html>