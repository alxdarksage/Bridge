<%@ include file="../jsp/directives.jsp" %>
<div class="auth-divider">
    <div></div> <spring:message code="OR"/> <div></div>
</div>
<c:url var="authUrl" value="/openId.html"/>
<form action="${authUrl}" id="oauthForm" method="post" class="google-button">
    <input name="OPEN_ID_PROVIDER" type="hidden" value="GOOGLE"/>
    <input name="RETURN_TO_URL" type="hidden" value="${sessionScope['origin']}"/>
    <div><button id="oauthButton" type="submit"><img src='<c:url value="/static/images/google-sign-in.png"/>'></button></div>
</form>
