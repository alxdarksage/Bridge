<%@ include file="../directives.jsp" %>
<sage:journal code="MyJournal">
    <jsp:attribute name="content">
    	<sage:reminders/>
	    <p>This would be the date view, or the dashboard, or both. There really isn't a date view 
	    at the moment, at least not in the mid-tier. I could get all records, sort them, and then
	    paginate them in the UI, there are always issues with that. </p>    
    </jsp:attribute>
</sage:journal>
