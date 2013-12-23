<%@ include file="../jsp/directives.jsp" %>
<c:if test="${sessionScope['notice'] != null}">
<script id="notice">humane.log("<spring:message code="${pageContext.request.getNotification()}"/>");</script>
</c:if>
