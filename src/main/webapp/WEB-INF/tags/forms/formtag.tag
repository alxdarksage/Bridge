<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="formId" required="true" rtexprvalue="true" type="java.lang.Object"%>
<%@ attribute name="columnDescriptor" required="true" rtexprvalue="true" type="java.lang.Object"%>
<%@ attribute name="value" required="true" rtexprvalue="true" type="java.lang.Object"%>
<%
	request.setAttribute("formId", formId);
	request.setAttribute("columnDescriptor", columnDescriptor);
	request.setAttribute("value", value);
%>
<jsp:include page="/WEB-INF/jsp/elements/${columnDescriptor.type}.jsp"/>
