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
            <sage:checkboxes label="CommunityAdministrators" field="administrators" items="${administrators}"/>
        </c:if>
        <sage:hidden field="id"/>
        <sage:submit code="Save"/>
        <sage:cancel url="/admin/communities.html"/>
    </form:form>
</sage:admin>
