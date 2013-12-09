<%@ include file="directives.jsp" %>
<sage:minimal code="Profile">
    <h3><spring:message code="ProfileFor"/> ${profileForm.displayName}</h3>
    <sage:formErrors formName="profileForm"/>
    <c:url var="profileUrl" value="/profile.html"/>
    <form:form role="form" modelAttribute="profileForm" method="post" action="${profileUrl}" enctype="multipart/form-data">
        <spring:hasBindErrors name="*">
            <div class="alert alert-danger">
                <form:errors htmlEscape="false"></form:errors>
            </div>
        </spring:hasBindErrors>
        
        <div class="row">
            <div class="col-sm-6">
		        <b><spring:message code="PhotoOrAvatar"/></b>
		        <div class="avatar_container">
		            <img id="photoImg" src=""/>
		        </div>
		        
		        <div style="position:relative; margin-bottom: 2rem">
		            <a class='btn btn-sm btn-default' href='javascript:;'><spring:message code="ChooseFile"/>
		                <input id="photoFileInput" type="file" name="photoFile" size="40"/>
		            </a>
		        </div>
		        
		        <div class="form-group">
		            <label>Community Memberships</label>
		            <div class="checkbox-box">
		                <c:forEach items="${memberships}" var="community">
		                    <div class="checkbox" title="${community.displayName} Membership">
		                        <label>
		                            <c:choose>
		                                <c:when test="${community.selected}">
		                                    <input type="checkbox" name="memberships" value="${community.id}" checked="checked" />
		                                </c:when>
		                                <c:otherwise>
		                                    <input type="checkbox" name="memberships" value="${community.id}" />
		                                </c:otherwise>
		                            </c:choose>
		                            ${community.displayName}
		                        </label>
		                    </div>
		                </c:forEach>
		            </div>
                    <spring:bind path="memberships">
                        <div class="${status.error ? 'has-error' : ''}">
	                        <form:errors id="memberships_errors" path="memberships" htmlEscape="false"/>
                        </div>
                    </spring:bind>
		        </div>
            </div>
            <div class="col-sm-6">
                <div class="form-group">
			        <label class="control-label"><spring:message code="displayName"/></label>
			        <p class="form-control-static">${profileForm.displayName}</p>
			    </div>
		        <sage:text field="firstName"/>
		        <sage:text field="lastName"/>
		        <sage:textarea field="summary"/>
            </div>
        </div>
        <sage:submit code="Save"/>
        <sage:cancel url="${pageContext.request.origin}"/>
        <a class="btn btn-sm btn-default right" href='<c:url value="/requestResetPassword.html"/>'>
            <spring:message code="ChangePassword"/>
        </a>
    </form:form>
</sage:minimal>