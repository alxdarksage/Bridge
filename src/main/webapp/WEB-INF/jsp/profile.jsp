<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sage" tagdir="/WEB-INF/tags" %>
<sage:minimal title="Profile">
    <h3>Profile for ${profileForm.displayName}</h3>
    <spring:bind path="profileForm">
        <c:if test="${not empty status.errorMessages}">
            <div class="alert alert-danger">
                <form:errors path="profileForm"></form:errors>
            </div>
        </c:if>
    </spring:bind>
    <c:url var="profileUrl" value="/profile.html"/>
    <form:form role="form" modelAttribute="profileForm" method="post" action="${profileUrl}" enctype="multipart/form-data">
        <spring:hasBindErrors name="*">
            <div class="alert alert-danger">
                <form:errors></form:errors>
            </div>
        </spring:hasBindErrors>
        
        <b>Photo or Avatar</b>
        <div class="avatar_container">
            <img id="photoImg" src=""/>
        </div>
        
        <div style="position:relative; margin-bottom: 2rem">
            <a class='btn btn-sm btn-default' href='javascript:;'>Choose File...
                <input id="photoFileInput" type="file" name="photoFile" size="40"/>
            </a>
        </div>
        
        <sage:text field="displayName" label="Your display name">
        <span class="help-block">Your display name must be unique, but it does not need to be your name
        or even to identify you.</span>
        </sage:text>
        <sage:text field="firstName" label="First Name"/>
        <sage:text field="lastName" label="Last Name"/>
        <sage:textarea field="summary" label="Something about yourself"/>
        <button type="submit" class="btn btn-sm btn-primary">Save</button>
        <a class="btn" href='<c:url value="${sessionScope.BridgeUser.getStartURL()}"/>'>Cancel</a>
    </form:form>
</sage:minimal>