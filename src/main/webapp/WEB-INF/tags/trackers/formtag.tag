<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="trackerId" required="true" rtexprvalue="true" type="java.lang.Object"%>
<%@ attribute name="columnDescriptor" required="true" rtexprvalue="true" type="java.lang.Object"%>
<%@ attribute name="currentValue" required="true" rtexprvalue="true" type="java.lang.Object"%>
<%@ attribute name="previousValue" required="true" rtexprvalue="true" type="java.lang.Object"%>
<%
	request.setAttribute("trackerId", trackerId);
	request.setAttribute("columnDescriptor", columnDescriptor);
	request.setAttribute("currentValue", currentValue);
	request.setAttribute("previousValue", previousValue);
%>
<jsp:include page="/WEB-INF/jsp/elements/${columnDescriptor.type}.jsp"/>
