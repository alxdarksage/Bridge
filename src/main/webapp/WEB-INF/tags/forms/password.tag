<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="field" required="true" %>
<%@ attribute name="required" required="false" %>
<spring:bind path="${field}">
    <div class="form-group ${status.error ? 'has-error' : ''}">
        <label class="control-label" for="${field}">
            <spring:message code="${field}"/>
            <c:if test="${required}"><span class="error-text">*</span></c:if>
        </label>
        <form:password cssClass="form-control input-sm" id="${field}" path="${field}"/>
        <form:errors id="${field}_errors" path="${field}" htmlEscape="false"/>
    </div>
</spring:bind>
