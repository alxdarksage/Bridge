<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="code" required="true" %>
<!DOCTYPE html>
<html lang="en">
<sage:head code="${code}"/>
<body>
	<jsp:doBody/>
	<script src="<c:url value='/assets/footer.js'/>"></script>
</body>
</html>