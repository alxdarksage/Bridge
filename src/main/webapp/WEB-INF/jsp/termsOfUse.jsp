<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sage" tagdir="/WEB-INF/tags" %>
<sage:minimal title="Terms of Use" boxSize="90%">
    <c:url var="termsOfUseUrl" value="/termsOfUse.html"/>
    <div class="tou">
        ${termsOfUseForm.termsOfUse}
    </div>
    <spring:bind path="termsOfUseForm">
        <c:if test="${not empty status.errorMessages}">
            <div class="alert alert-danger">
                <form:errors path="termsOfUseForm"></form:errors>
            </div>
        </c:if>
    </spring:bind>
    <form:form role="form" modelAttribute="termsOfUseForm" method="post" action="${termsOfUseUrl}">
        <spring:hasBindErrors name="*">
            <div class="alert alert-danger">
                <form:errors></form:errors>
            </div>
        </spring:hasBindErrors>
        <sage:checkbox field="acceptTermsOfUse">
            I agree to the terms of use
        </sage:checkbox>
        <button type="submit" class="btn btn-sm btn-default">Continue</button>
        <a id="cancelButton" class="btn" href='<c:url value="/termsOfUse/cancel.html"/>'>Cancel</a>
    </form:form>
</sage:minimal>
