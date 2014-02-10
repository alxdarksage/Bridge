<%@ include file="../directives.jsp" %>
<sage:journal code="MyJournal">
    <jsp:attribute name="content">
    	<sage:reminders/>
		<c:forEach var="descriptor" items="${descriptorsTimelines}">
	    	<sage:timeseries series="${descriptor.id}"/>
	    </c:forEach>
    </jsp:attribute>
</sage:journal>
