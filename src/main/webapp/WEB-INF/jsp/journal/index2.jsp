<%@ include file="../directives.jsp" %>
<sage:journal code="MyJournal">
    <jsp:attribute name="content">
    	<c:choose>
	    	<c:when test="${not empty descriptorsQuestionsToAsk}">
	    		<sage:question question="${descriptorsQuestionsToAsk[0]}"/>
	    		<c:forEach var="question" items="${descriptorsQuestionsAnswered}" varStatus="loop">
		    		<sage:questionAnswered question="${descriptorsQuestionsAnswered}" index=""/>
	    		</c:forEach>
		    	<sage:reminders/>
				<c:forEach var="descriptor" items="${descriptorsTimelines}">
			    	<sage:timeseries series="${descriptor.id}"/>
			    </c:forEach>
	    	</c:when>
	    	<c:otherwise>
		    	<sage:reminders/>
				<c:forEach var="descriptor" items="${descriptorsTimelines}">
			    	<sage:timeseries series="${descriptor.id}"/>
			    </c:forEach>
	    	</c:otherwise>
    	</c:choose>
    </jsp:attribute>
</sage:journal>
