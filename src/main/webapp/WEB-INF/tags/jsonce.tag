<%@ attribute name="id" required="true" %>
<%
	String jsid = "javascript-" + id;
	if ( request.getAttribute(jsid) == null ) {
	    %><jsp:doBody/><%
	    request.setAttribute(jsid, Boolean.TRUE);
	}
%>
