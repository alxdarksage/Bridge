<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="code" required="true" %>
<%@ attribute name="active" required="false" %>
<sage:main code="${code}">
    <jsp:attribute name="navigation">
        <ul class="list-group">
            <c:choose>
                <c:when test="${active == 'Home'}">
                    <li class="active list-group-item"><spring:message code="AdminHome"/></li>
                    <li class="list-group-item"><a id="communitiesAct" href='<c:url value="/admin/communities.html"/>'><spring:message code="Communities"/></a></li>
                    <li class="list-group-item"><a id="pddAct" href='<c:url value="/admin/descriptors.html"/>'><spring:message code="Participant Data Descriptors"/></a></li>
                </c:when>
                <c:when test="${active == 'Communities'}">
                    <li class="list-group-item"><a id="adminAct" href='<c:url value="/admin/index.html"/>'><spring:message code="AdminHome"/></a></li>
		            <li class="active list-group-item"><spring:message code="Communities"/></li>
		            <li class="list-group-item"><a id="pddAct" href='<c:url value="/admin/descriptors.html"/>'><spring:message code="Participant Data Descriptors"/></a></li>
                </c:when>
                <c:when test="${active == 'PDD'}">
                    <li class="list-group-item"><a id="adminAct" href='<c:url value="/admin/index.html"/>'><spring:message code="AdminHome"/></a></li>
                    <li class="list-group-item"><a id="communitiesAct" href='<c:url value="/admin/communities.html"/>'><spring:message code="Communities"/></a></li>
                    <li class="list-group-item active"><spring:message code="Participant Data Descriptors"/></li>
                </c:when>
                <c:when test="${active == 'Users'}">
                    <li class="list-group-item"><a id="adminAct" href='<c:url value="/admin/index.html"/>'><spring:message code="AdminHome"/></a></li>
                    <li class="list-group-item"><a id="communitiesAct" href='<c:url value="/admin/communities.html"/>'><spring:message code="Communities"/></a></li>
                </c:when>
            </c:choose>
        </ul>
    </jsp:attribute>
    <jsp:attribute name="content"><jsp:doBody/></jsp:attribute>
</sage:main>
