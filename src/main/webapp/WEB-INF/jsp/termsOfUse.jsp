<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sage" tagdir="/WEB-INF/tags" %>
<sage:minimal code="TermsOfUse" boxSize="90%">
    <c:url var="termsOfUseUrl" value="/termsOfUse.html"/>
    <div class="tou">
        ${termsOfUseForm.termsOfUse}
    </div>
    <spring:bind path="termsOfUseForm">
        <c:if test="${not empty status.errorMessages}">
            <div class="alert alert-danger">
                <form:errors path="termsOfUseForm" htmlEscape="false"></form:errors>
            </div>
        </c:if>
    </spring:bind>
    <form:form role="form" modelAttribute="termsOfUseForm" method="post" action="${termsOfUseUrl}">
        <spring:hasBindErrors name="*">
            <div class="alert alert-danger">
                <form:errors htmlEscape="false"></form:errors>
            </div>
        </spring:hasBindErrors>
        <sage:checkbox field="acceptTermsOfUse">
            <spring:message code="AgreeToTOU"/>
        </sage:checkbox>
        <button type="submit" class="btn btn-sm btn-default">
            <spring:message code="Continue"/>
        </button>
        <input type="hidden" name="oauthRedirect" value="${param.oauth ? param.oauth : 'false'}"/>
        <a id="cancelButton" class="btn" href='<c:url value="/termsOfUse/cancel.html"/>'>
            <spring:message code="Cancel"/>
        </a>
    </form:form>
</sage:minimal>
