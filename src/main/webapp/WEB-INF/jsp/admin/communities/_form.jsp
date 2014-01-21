<sage:formErrors formName="communityForm"/>
<c:url var="communityUrl" value="/admin/communities/${communityForm.formId}.html"/>
<form:form role="form" modelAttribute="communityForm" method="post" action="${communityUrl}">
    <sage:text field="name" required="true"/>
    <sage:textarea field="description"/>
    <%-- There are no members at this point. --%>
    <c:if test="${communityForm.formId != 'new'}">
        <sage:checkboxes label="CommunityAdministrators" field="administrators" items="${administrators}"/>
    </c:if>
    <sage:hidden field="id"/>
    <sage:submit code="Save"/>
    <sage:cancel url="/admin/communities/index.html"/>
</form:form>