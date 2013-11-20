<%@ include file="../directives.jsp" %>
<%--
    This is just some junk thrown in, mostly to point out that we need to think through what will be here.
    Feel free to throw away what is here.
 --%>
<sage:portal code="Tagline">
    <div class="row">
        <div class="col-md-4">
            <ul class="list-group">
                <c:forEach var="cty" items="${communities}">
                    <li class="list-group-item"><a href='<c:url value="/communities/${cty.id}.html"/>'>${cty.name}</a></li>
                </c:forEach>
            </ul>
        </div>
        <div class="col-md-8">
            <div style="margin-bottom: 1rem; outline: 1px solid #aaa; position: relative; background: transparent url('/webapp/images/bridge.jpg') 4% 48% no-repeat; max-width: 600px; height: 200px ! important">&#160;</div>
            <p id="portal-page">
                This is the portal page. It has anonymous access, but needs to know if you are authenticated or not. 
                It also needs content of some kind. The current appearance is intended to be slightly embarassing, 
                as call to action. 
            </p>
        </div>
    </div>
    <p style="border-top: 1px solid #aaa; margin-top: 2rem">
        <c:if test="${pageContext.request.isUserInRole('admin')}">
            <a id="adminAct" href='<c:url value="/admin/"/>'>Admin</a> &bull;
        </c:if>
        <c:choose>
            <c:when test="${sessionScope.BridgeUser.isAuthenticated()}">
                <c:url var="signOutUrl" value="/signOut.html"/>
		        <a id="signOutAct" href="${signOutUrl}">
		            <spring:message code="SignOut"/>
		        </a> 
            </c:when>
            <c:otherwise>
                <c:url var="signInUrl" value="/signIn.html"/>
                <a id="signInAct" href="${signInUrl}">
                    <spring:message code="SignIn"/>
                </a> &bull; 
                <c:url var="signUpUrl" value="/signUp.html"/>
                <a id="signUpAct" href="${signUpUrl}">
                    <spring:message code="SignUp"/>
                </a>
            </c:otherwise>
        </c:choose>
    </p>
</sage:portal>
