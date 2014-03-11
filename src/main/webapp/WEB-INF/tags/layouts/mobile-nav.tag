<%@ include file="../../jsp/directives.jsp" %>
<div id="content" class="snap-content">
    <jsp:doBody/>
</div>
<div class="snap-drawers">
    <div class="snap-drawer snap-drawer-left">
        <ul class="list-group">
            <c:if test="${sessionScope.BridgeUser.isAuthenticated()}">
                <c:url var="journalUrl" value="/journal.html"/>
                <li class="list-group-item"><a href="${journalUrl}"><spring:message code="MyJournal"/></a></li>
            </c:if>
            <c:forEach var="tracker" items="${trackers}">
                <c:url var="trackerUrl" value="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${tracker.id}.html"/>
                <li class="list-group-item">
                    <a href="${trackerUrl}">
                       ${(not empty tracker.name) ? tracker.name : tracker.description}
                    </a>
                </li>
            </c:forEach>
        </ul>
        <ul class="list-group">
            <c:forEach var="cty" items="${allCommunities}">
                <c:url var="ctyUrl" value="/communities/${cty.id}.html"/>
                <li class="list-group-item"><a href="${ctyUrl}">${cty.name}</a></li>
            </c:forEach>
        </ul>
        <ul class="list-group">
            <li class="list-group-item">
                <a href='<c:url value="/index.html"/>'>Bridge</a>
            </li>
            <c:if test="${sessionScope.BridgeUser.isAuthenticated()}">
                <li class="list-group-item">
                    <a href="<c:url value="/profile.html"/>"><spring:message code="Profile"/></a>
                </li>
            </c:if>
            <c:choose>
                <c:when test="${sessionScope.BridgeUser.isAuthenticated()}">
                    <li class="list-group-item">
                        <a href='<c:url value="/signOut.html"/>'><spring:message code="SignOut"/></a>
                    </li> 
                </c:when>
                <c:otherwise>
                    <li class="list-group-item">
                       <a href='<c:url value="/signIn.html"/>'><spring:message code="SignIn"/></a>
                    </li>
                    <li class="list-group-item">
                       <a href='<c:url value="/signUp.html"/>'><spring:message code="SignUp"/></a>
                    </li>
                    <li class="list-group-item">
                       <a href='<c:url value="/requestResetPassword.html"/>'><spring:message code="ForgotPassword"/></a>
                   </li> 
                </c:otherwise>
            </c:choose>
        </ul>
    </div>
</div>
