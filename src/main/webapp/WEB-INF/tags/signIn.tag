<%@ include file="../jsp/directives.jsp" %>
<c:choose>
    <c:when test="${sessionScope.BridgeUser.isAuthenticated()}">
		<c:url var="signOutUrl" value="/signOut.html"/>
		<c:url var="editProfileUrl" value="/profile.html"/>
		<c:url var="journalUrl" value="/journal.html"/>
        <div id="profile-pane" class="panel panel-default hidden-xs">
            <div class="avatar-side">
                <div class="avatar" style="background-image: url('<c:url value="/static/images/default_avatar.png"/>')">
                </div>
                <div class="msgs">
                    <span class="glyphicon glyphicon-inbox"></span>&#160;<a>0 Msgs</a>
                </div>
            </div>
            <div class="userName-side">
                <div class="userName-box">
                    <div class="userName">${sessionScope['BridgeUser'].userName} <span class="caret"></span></div>
                    <div class="smenu">
                        <a id="editProfileAct" href="${editProfileUrl}"><spring:message code="EditProfile"/></a>
                        <a id="signOutButton" href="${signOutUrl}"><spring:message code="SignOut"/></a>
                    </div>
                </div>
                <a id="journalAct" href="${journalUrl}"><spring:message code="MyJournal"/></a>
                <c:forEach var="cty" items="${sessionScope['BridgeUser'].communities}">
                    <c:url var="ctyUrl" value="/communities/${cty.id}.html"/>
                    <a href="${ctyUrl}">${cty.name}</a>
                </c:forEach>
            </div>
        </div>    
    </c:when>
    <c:otherwise>
        <div class="well">
			<c:url var="signInUrl" value="/signIn.html"/>
			<form:form role="form" modelAttribute="signInForm" method="post" action="${signInUrl}">
			    <div class="form-group">
			        <spring:message var="userNameLabel" code="UserName"/>
			        <form:input cssClass="form-control input-sm" id="userName" path="userName" placeholder="${userNameLabel}"/>
			    </div>
			    <div class="form-group">
			        <spring:message var="passwordLabel" code="Password"/>
			        <form:password cssClass="form-control input-sm" id="password" path="password" placeholder="${passwordLabel}"/>
			    </div>
			    <c:if test="${not empty param.login}">
			        <div class="alert alert-danger has-error">
			            <spring:message code="IncorrectLogin"/>
			        </div>
			    </c:if>
			    <sage:submit code="SignIn"/>
			    <c:url var="signUpUrl" value="/signUp.html"/>
			    <a id="signUpLink" href="${signUpUrl}" class="btn"><spring:message code="SignUp"/></a>
			</form:form>
			<p class="forgotLinkWrapper">
                <c:url var="forgotUrl" value="/requestResetPassword.html"/>
			    <a id="forgotPasswordLink" href="${forgotUrl}"><spring:message code="ForgotPassword"/></a>
			</p>
			<sage:oauth/>
        </div>
    </c:otherwise>
</c:choose>



