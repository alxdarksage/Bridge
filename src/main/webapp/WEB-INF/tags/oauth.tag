<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="auth-divider">
    <div></div> OR <div></div>
</div>
<c:url var="authUrl" value="/openId.html"/>
<form action="${authUrl}" method="post" class="google-button">
    <input name="OPEN_ID_PROVIDER" type="hidden" value="GOOGLE"/>
    <input name="RETURN_TO_URL" type="hidden" value="${sessionScope['origin']}"/>
    <div><button type="submit"><img src='<c:url value="/images/google-sign-in.png"/>'></button></div>
</form>
