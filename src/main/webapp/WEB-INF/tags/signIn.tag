<%@ include file="../jsp/directives.jsp" %>

<c:choose>
    <c:when test="${sessionScope.BridgeUser.isAuthenticated()}">
        <%-- 
        <div id="profile-pane" class="well hidden-sm">
            <table cellspacing="0" cellpadding="0">
                <tr>
                    <td><img src='<c:url value="/static/images/default_avatar.png"/>'/></td>
                    <td>
                        <div class="userName">${sessionScope['BridgeUser'].displayName}</div>
                        <c:url var="signOutUrl" value="/signOut.html"/>
                        <c:url var="editProfileUrl" value="/profile.html"/>
                        <a id="signOutButton" href="${signOutUrl}">
                            <spring:message code="SignOut"/>
                        </a> <span class="divider-element">&bull;</span> 
                        <a id="editProfileLink" href="${editProfileUrl}">
                            <spring:message code="EditProfile"/>
                        </a>
                    </td>
                </tr>
            </table>
        </div>
        --%>
        
        <div id="profile-pane" class="panel panel-default hidden-sm">
            <div class="panel-body">
	            <table>
	                <tr>
	                    <td><img src='<c:url value="/static/images/default_avatar.png"/>'/></td>
	                    <td>
	                        <div class="userName">${sessionScope['BridgeUser'].displayName}</div>
	                        <c:url var="signOutUrl" value="/signOut.html"/>
	                        <c:url var="editProfileUrl" value="/profile.html"/>
	                        <a id="signOutButton" href="${signOutUrl}">
	                            <spring:message code="SignOut"/>
	                        </a> <span class="divider-element">&bull;</span> 
	                        <a id="editProfileLink" href="${editProfileUrl}">
	                            <spring:message code="EditProfile"/>
	                        </a>
	                    </td>
	                </tr>
	            </table>
            </div>
            <div class="panel-footer">
                <a>My Journal</a>
            </div>
        </div>
        
    </c:when>
    <c:otherwise>
        <div class="well">
			<h3><spring:message code="SignIn"/></h3>
			<c:url var="signInUrl" value="/signIn.html"/>
			<form:form role="form" modelAttribute="signInForm" method="post" action="${signInUrl}">
			    <div class="form-group">
			        <spring:message var="emailLabel" code="Email"/>
			        <form:input cssClass="form-control input-sm" id="email" path="email" placeholder="${emailLabel}"/>
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
			    <a id="signUpLink" href='<c:url value="/signUp.html"/>' class="btn"><spring:message code="SignUp"/></a>
			</form:form>
			<p class="forgotLinkWrapper">
			    <a id="forgotPasswordLink" href='<c:url value="/requestResetPassword.html"/>'><spring:message code="ForgotPassword"/></a>
			</p>
			<sage:oauth/>
        </div>
    </c:otherwise>
</c:choose>



