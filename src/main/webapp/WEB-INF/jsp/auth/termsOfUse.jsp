<%@ include file="../directives.jsp" %>
<sage:minimal code="TermsOfUse" boxSize="90%" mobile="false">
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
