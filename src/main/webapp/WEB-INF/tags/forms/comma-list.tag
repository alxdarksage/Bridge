<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="items" required="true" type="java.util.List" %>
<%@ attribute name="first" required="true" type="java.lang.String" %>
<%@ attribute name="separator" required="true" type="java.lang.String" %>
<%@ attribute name="endSeparator" required="true" type="java.lang.String" %>
<%@ attribute name="last" required="true" type="java.lang.String" %>
<%@ attribute name="emptyList" required="false" type="java.lang.String" %>
<%@ variable name-given="item" scope="NESTED" %>
<c:choose>
	<c:when test="${(not empty items)}">
		${first}<c:forEach var="item" items="${items}" varStatus="loop"
			><c:choose
				><c:when test="${loop.first}"></c:when
				><c:when test="${loop.last}">${endSeparator}</c:when
				><c:when test="${not loop.first}">${separator}</c:when>
			</c:choose
			><c:set var="item" value="${item}" scope="page"
			/><jsp:doBody
		/></c:forEach>${last}
	</c:when>
	<c:otherwise>${emptyList}</c:otherwise>
</c:choose>
