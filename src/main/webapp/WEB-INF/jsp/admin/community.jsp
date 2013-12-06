<%@ include file="../directives.jsp" %>
<sage:admin code="AdminCommunity" active="Communities">
    <div class="sage-crumbs">
        <a href="<c:url value="/admin/index.html"/>">Admin Dashboard</a> &#187; 
        <a href="<c:url value="/admin/communities.html"/>">Communities</a> &#187;
    </div>
    <h3><spring:message code="${communityForm.formName}"/></h3>
    
    <sage:formErrors formName="communityForm"/>
    <c:url var="communityUrl" value="/admin/communities/${communityForm.formId}.html"/>
    <form:form role="form" modelAttribute="communityForm" method="post" action="${communityUrl}">
        <sage:text field="name"/>
        <sage:textarea field="description"/>
        <%-- There are no members at this point. --%>
        <c:if test="${communityForm.formId != 'new'}">
	        <div class="form-group">
	            <label>Community Administrators</label>
	            <div class="checkbox-box">
	                <c:forEach items="${administrators}" var="admin">
	                    <div class="checkbox" title="${admin.displayName}">
	                        <label>
	                            <c:choose>
	                                <c:when test="${admin.selected}">
	                                    <input type="checkbox" name="administrators" value="${admin.id}" checked/>
	                                </c:when>
	                                <c:otherwise>
	                                    <input type="checkbox" name="administrators" value="${admin.id}" />                
	                                </c:otherwise>
	                            </c:choose>
	                            ${admin.displayName}
	                        </label>
	                    </div>
	                </c:forEach>
	            </div>
                <spring:bind path="administrators">
                    <div class="${status.error ? 'has-error' : ''}">
                        <form:errors id="administrators_errors" path="administrators" htmlEscape="false"/>
                    </div>
                </spring:bind>
	        </div>
        </c:if>
        
        <sage:hidden field="id"/>
        <sage:submit code="Save"/>
        <sage:cancel url="/admin/communities.html"/>
    </form:form>
</sage:admin>
