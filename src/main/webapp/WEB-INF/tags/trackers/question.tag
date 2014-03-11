<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="question" required="true" type="java.lang.Object"%>
<%
	request.setAttribute("question", question);
%>
<jsp:include page="/WEB-INF/jsp/elements/${question.descriptor.type}.jsp"/>
