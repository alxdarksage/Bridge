<%@ include file="../jsp/directives.jsp" %>
<%@ attribute name="code" required="true" %>
<div class="header row">
    <div class="col-sm-6 community-header">
        <c:url var="communityUrl" value="/communities/${community.id}.html"/>
        <a href="${communityUrl}">
            <spring:message code="${code}"/>
        </a>
    </div>
    <div class="col-sm-6 portal-header">
        <div class="portal-subheader visible-sm visible-md visible-lg">
            <a href="<c:url value='/portal/index.html'/>">Bridge Community Portal</a>
        </div>
        <c:if test="${code != 'SignIn'}">
            <div class="portal-links visible-xs visible-sm">
                <c:choose>
                    <c:when test="${sessionScope.BridgeUser.isAuthenticated()}">
                        <a href='<c:url value="/signOut.html"/>'><spring:message code="SignOut"/></a> | 
                        <a href='<c:url value="/index.html"/>'>Bridge</a>
                    </c:when>
                    <c:otherwise>
                        <a href='<c:url value="/signIn.html"/>'><spring:message code="SignIn"/></a> |
                        <a href='<c:url value="/signUp.html"/>'><spring:message code="SignUp"/></a> | 
                        <a href='<c:url value="/index.html"/>'>Bridge</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>
    </div>
</div>
