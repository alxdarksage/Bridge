<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sage" tagdir="/WEB-INF/tags" %>
<sage:minimal code="TermsOfUse" boxSize="90%">
    <div class="tou">
        ${termsOfUseForm.termsOfUse}
    </div>
    <sage:formErrors formName="termsOfUseForm"/>
    <c:url var="termsOfUseUrl" value="/termsOfUse.html"/>
    <form:form role="form" modelAttribute="termsOfUseForm" method="post" action="${termsOfUseUrl}">
        <sage:checkbox field="acceptTermsOfUse">
            <spring:message code="AgreeToTOU"/>
        </sage:checkbox>
        <sage:submit code="Continue"/>
        <sage:cancel url="/termsOfUse/cancel.html"/>
        <input type="hidden" name="oauthRedirect" value="${param.oauth ? param.oauth : 'false'}"/>
    </form:form>
</sage:minimal>
